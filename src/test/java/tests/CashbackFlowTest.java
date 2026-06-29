package tests;

import base.BaseTest;

import com.microsoft.playwright.options.WaitForSelectorState;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;
import pages.BrandPage;
import pages.AllCategoriesPage;
import pages.HomePage;

import java.util.List;

public class CashbackFlowTest extends BaseTest {

    @Test
    public void cashback_homepage_test() throws InterruptedException {

        // ── 1. Open home page ──────────────────────────────────────────────────
        HomePage homePage = new HomePage(page);
        homePage.open();
        page.waitForLoadState();
        assertHomePageLoaded();

        // ── 2. Open Menu → All Categories ──────────────────────────────────────
        homePage.clickMenu();
        AllCategoriesPage allCategoriesPage = homePage.clickAllCategories();
        assertAllCategoriesPageLoaded();

        // ── 3. Search brand → assert results → then click ──────────────────────
        String brandName = allCategoriesPage.getBrandName();
        allCategoriesPage.enterSearchText(brandName);
        assertSearchResultsVisible();
        assertSearchResultCount();
        allCategoriesPage.validateSearchResults(brandName);
        allCategoriesPage.clickFirstSearchResult();
        BrandPage brandPage = new BrandPage(page);

        // ── 4. Activate cashback → opens external page → close it ───────────────
        brandPage.activateCashbackAndClose(context, 5000);
        assertExternalPageClosed();

        // ── 5. Expand cashback rates (twice) ────────────────────────────────────
        brandPage.expandCashbackRates();
        brandPage.expandCashbackRates();
        assertExpandCashbackRatesButtonVisible();

        // ── 6. Close the brand popup ────────────────────────────────────────────
        brandPage.closeBrandPopup();
        assertBrandPopupClosed();

        // ── 7. Click Popular filter ─────────────────────────────────────────────
        allCategoriesPage.clickPopularFilter();
        assertPopularFilterCampaignsVisible();

        // ── 8. Click A-Z filter and validate sort order ─────────────────────────
        allCategoriesPage.clickAToZFilter();
        List<String> names = allCategoriesPage.A_Z_filter_getCampaignNames();
        assertAToZSortOrder(names);
        allCategoriesPage.clickAToZDropdown();

        // ── 9. Click Cashback filter and validate descending rates ───────────────
        allCategoriesPage.clickCashbackFilter();
        List<String> rates = allCategoriesPage.cashback_filter_getCommissionRates();
        assertCashbackDescendingOrder(rates);
       allCategoriesPage.scrollDownCampaigns(page);

        // ── 10. Go back ──────────────────────────────────────────────────────────
        allCategoriesPage.clickBackButton();
        assertBackOnHomePage();
    }

    // ── Assertion helpers ──────────────────────────────────────────────────────

    private void assertHomePageLoaded() {
        Assertions.assertThat(page.url())
                .as("Home page URL should contain a uuid parameter")
                .contains("uuid=");
    }

    private void assertAllCategoriesPageLoaded() {
        page.waitForSelector("//input[@placeholder='Search']",
                new com.microsoft.playwright.Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(10000));

        Assertions.assertThat(page.locator("//input[@placeholder='Search']").isVisible())
                .as("Search input should be visible on All Categories page")
                .isTrue();
    }

    private void assertSearchResultsVisible() {
        page.waitForSelector(".search_result_item",
                new com.microsoft.playwright.Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(10000));
    }

    private void assertSearchResultCount() {
        Assertions.assertThat(page.locator(".search_result_item").count())
                .as("At least one search result should appear for 'myn'")
                .isGreaterThan(0);
    }

    private void assertExternalPageClosed() {
        Assertions.assertThat(context.pages().size())
                .as("External brand page should be closed; only one page should remain")
                .isEqualTo(1);
    }

    private void assertExpandCashbackRatesButtonVisible() {
        page.waitForSelector(
                "//button[@class='sc-fQpRED bUMSEu' or contains(text(),'See more rates')]",
                new com.microsoft.playwright.Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(10000));

        Assertions.assertThat(
                page.locator("//button[@class='sc-fQpRED bUMSEu' or contains(text(),'See more rates')]")
                        .isVisible())
                .as("Expand cashback rates button should still be present after two expansions")
                .isTrue();
    }

    private void assertBrandPopupClosed() {
        page.waitForSelector(
                "(//button[.//*[name()='path' and contains(@d,'M6 18 18 6')]])[2]",
                new com.microsoft.playwright.Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.HIDDEN)
                        .setTimeout(10000));

        Assertions.assertThat(
                page.locator("(//button[.//*[name()='path' and contains(@d,'M6 18 18 6')]])[2]")
                        .isVisible())
                .as("Brand popup close button should no longer be visible after closing")
                .isFalse();
    }

    private void assertPopularFilterCampaignsVisible() {
        Assertions.assertThat(
                page.locator("//div[@class='sc-liquwA gTREJt']").count())
                .as("Campaign cards should be visible after selecting Popular filter")
                .isGreaterThan(0);
    }

    private void assertAToZSortOrder(List<String> names) {
        Assertions.assertThat(names)
                .as("A-Z filter should return at least one campaign name")
                .isNotEmpty();

        String  previousName           = null;
        char    previousFirstChar      = 0;
        boolean previousWasLatinLetter = false;

        for (String name : names) {

            // Normalize: strip accents, remove spaces, lowercase
            String normalized = java.text.Normalizer
                    .normalize(name, java.text.Normalizer.Form.NFD)
                    .replaceAll("[^\\p{ASCII}]", "")
                    .replaceAll("\\s+", "")
                    .toLowerCase()
                    .trim();

            if (normalized.isEmpty()) {
                System.out.println("Skipping non-ASCII/Cyrillic entry: " + name);
                continue;
            }

            char firstChar = normalized.charAt(0);

            // Skip special characters (not letter or digit)
            if (!Character.isLetterOrDigit(firstChar)) {
                System.out.println("Skipping special character entry: " + name + " [" + firstChar + "]");
                continue;
            }

            // Skip entries whose original name starts with non-Latin script (Cyrillic, Arabic, etc.)
            // These appear at the end in the app and break Latin A-Z ordering
            char originalFirst = name.trim().charAt(0);
            if (!Character.isLetterOrDigit(originalFirst) || originalFirst > 127) {
                System.out.println("Skipping non-Latin script entry: " + name);
                continue;
            }

            if (previousName != null && previousWasLatinLetter) {

                System.out.println("Comparing: " + previousName + " [" + previousFirstChar + "]"
                        + " -> " + name + " [" + firstChar + "]");

                boolean previousIsDigit = Character.isDigit(previousFirstChar);
                boolean currentIsDigit  = Character.isDigit(firstChar);

                if (previousIsDigit && !currentIsDigit) {
                    System.out.println("OK: number -> letter");

                } else if (!previousIsDigit && currentIsDigit) {
                    Assertions.fail(
                        "Sort order invalid: letter before number. " +
                        "Previous: '" + previousName + "' [" + previousFirstChar + "], " +
                        "Current: '"  + name         + "' [" + firstChar + "]"
                    );

                } else {
                    Assertions.assertThat((int) firstChar)
                            .as("Not sorted A-Z. Previous: '%s' [%s], Current: '%s' [%s]",
                                    previousName, previousFirstChar, name, firstChar)
                            .isGreaterThanOrEqualTo((int) previousFirstChar);
                }
            }

            previousName           = name;
            previousFirstChar      = firstChar;
            previousWasLatinLetter = true;
        }

        System.out.println("A-Z sort validation PASSED for " + names.size() + " campaigns");
    }

    private void assertCashbackDescendingOrder(List<String> rates) {
        Assertions.assertThat(rates)
                .as("Cashback filter should return at least one commission rate")
                .isNotEmpty();

        Double previousRate = null;

        for (String rateText : rates) {
            String numeric = rateText.replaceAll("[^0-9.]", "").trim();

            if (numeric.isEmpty()) {
                System.out.println("Skipping non-numeric rate entry: " + rateText);
                continue;
            }

            double currentRate = Double.parseDouble(numeric);
            System.out.println("Commission rate: " + currentRate + "% (raw: " + rateText + ")");

            if (previousRate != null) {
                Assertions.assertThat(currentRate)
                        .as("Commission rates should be sorted descending. " +
                                "Previous: %.2f%%, Current: %.2f%%", previousRate, currentRate)
                        .isLessThanOrEqualTo(previousRate);
            }

            previousRate = currentRate;
        }

        System.out.println("Cashback% sort validation PASSED for " + rates.size() + " rates");
    }

    private void assertBackOnHomePage() {
        Assertions.assertThat(page.locator("//div[@class='sc-bTwLay bFNzwR']").isVisible())
                .as("Menu button should be visible after navigating back to home")
                .isTrue();
    }
}