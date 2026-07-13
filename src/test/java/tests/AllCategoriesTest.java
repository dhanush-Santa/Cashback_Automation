package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;

import pages.AllCategoriesPage;
import pages.BrandPage;

import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.Random;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import java.util.regex.Pattern;
public class AllCategoriesTest extends BaseTest {

    private AllCategoriesPage allCategories;

    @BeforeMethod
    public void setup() {
        allCategories = new AllCategoriesPage(page);

        // ALWAYS start from All Categories page
        allCategories.ensureOnAllCategories();
    }
List<String> allCampaigns;

    @Test
    public void verifyRandomCampaignSearchTest_001() {

        // 1. Collect all campaigns
        List<String> campaigns = allCategories.getAllCampaignsWithScroll();
        allCampaigns = campaigns; // Store for later use in other tests

        Assert.assertFalse(
                campaigns.isEmpty(),
                "No campaigns found in All Categories page"
        );

        // 2. Pick random campaign
        String randomCampaign =
                campaigns.get(new Random().nextInt(campaigns.size()));

        System.out.println("Selected Campaign: " + randomCampaign);

        // 3. Search campaign
        allCategories.searchCampaign(randomCampaign);

        // 4. Get results
        List<String> results = allCategories.getSearchResults();

        // 5. Validate
        boolean found = results.stream()
                .anyMatch(r -> r.toLowerCase()
                        .contains(randomCampaign.toLowerCase()));

        Assert.assertTrue(
                found,
                "Campaign not found in search results: " + randomCampaign
        );
    }
    
    @Test
public void verifyRandomInvalidCampaignSearchTest_002() {

   

    // 2. Known invalid campaigns list
    List<String> fakeCampaigns = List.of(
            "abcdef123",
            "randomXYZ",
            "notacampaign",
            "invalid_store_999",
            "fakebrandtest"
    );

    // 3. Pick random invalid campaign
    int index = new java.util.Random().nextInt(fakeCampaigns.size());
    String invalidCampaign = fakeCampaigns.get(index);

    System.out.println("Searching Invalid Campaign: " + invalidCampaign);

    // 4. Search it
    allCategories.searchCampaign(invalidCampaign);

    // 5. Get results
    List<String> results = allCategories.getSearchResults();

    // 6. Assertions (MAIN LOGIC)

    // Assertion 1: No results should match
    boolean found = results.stream()
            .anyMatch(r -> r.toLowerCase()
                    .contains(invalidCampaign.toLowerCase()));

    Assert.assertFalse(
            found,
            "Invalid campaign should NOT appear in results: " + invalidCampaign
    );

    // Assertion 2: No result message should be shown
    Assert.assertTrue(
            allCategories.isNoResultDisplayed(),
            "'No search result found!' message should be displayed"
    );
}

@Test
public void verifyAZFilterTest_003() {

   
    allCategories.applyAZFilter();

    List<String> names = allCategories.getCampaignNames();

    Assert.assertFalse(names.isEmpty(), "No campaigns found after A-Z filter");

    // reuse your existing assertion logic
    assertAToZSortOrder(names);

    allCategories.scrollToLoadCampaigns();
}

@Test
public void verifyCashbackFilterTest_004() {

   

    allCategories.applyCashbackFilter();

    List<String> rates = allCategories.getCashbackRates();

    Assert.assertFalse(rates.isEmpty(), "No cashback rates found");

    assertCashbackDescendingOrder(rates);

}

@Test
public void verifyActivateCashbackReflectsInClicksTest_005() {
    String clickedBrandName = allCategories.openRandomBrandAndGetName();
    Assert.assertNotNull(clickedBrandName, "Brand name could not be captured");

    BrandPage brand = new BrandPage(page);

    Assert.assertTrue(
            brand.isActivateCashbackVisible(),
            "Activate Cashback button not visible for brand: " + clickedBrandName
    );

    // 3-4. Activate cashback → wait for NTP to fully load → close NTP
    brand.activateCashbackAndClose(context);

    // 5. Confirm we're back on the brand page
    Assert.assertTrue(
            brand.isActivateCashbackVisible(),
            "Should be back on brand page after closing NTP"
    );

    brand.closeBrandPopup();
    Assert.assertFalse(
            brand.isActivateCashbackVisible(),
            "Brand popup should be closed"
    );

    // 6. Go back from All Categories
    allCategories.goBackFromAllCategories();

    // 7. Click Total Earnings (lands on Clicks tab by default)
    allCategories.clickTotalEarnings();

    // 8. Compare latest click's store name to the brand we activated cashback for
    String latestClickStore = allCategories.getLatestClickStoreName();

    Assert.assertEquals(
            latestClickStore.trim().toLowerCase(),
            clickedBrandName.trim().toLowerCase(),
            "Latest click in history does not match the brand where cashback was activated. "
                    + "Expected: " + clickedBrandName + ", Found: " + latestClickStore
    );

    System.out.println("Verified: Activated brand '" + clickedBrandName
            + "' matches latest click entry '" + latestClickStore + "'");
}

@Test
public void clickonsearchresult__006() {

    if (allCampaigns == null || allCampaigns.isEmpty()) {
        allCampaigns = allCategories.getAllCampaignsWithScroll();
    }

    String randomCampaign =
            allCampaigns.get(new Random().nextInt(allCampaigns.size()));

    System.out.println("Selected Campaign: " + randomCampaign);

    allCategories.searchCampaign(randomCampaign);

    // Click the matching result inside the search dialog
    page.getByRole(AriaRole.DIALOG)
            .getByText(randomCampaign, new Locator.GetByTextOptions().setExact(false))
            .first()
            .click();

    // Pass only if the brand popup (Activate Cashback) actually opened
    Locator activateCashback = page.locator("div")
            .filter(new Locator.FilterOptions()
                    .setHasText(Pattern.compile("^Activate Cashback$")))
            .first();

    boolean popupOpened = activateCashback.isVisible();

    Assert.assertTrue(
            popupOpened,
            "Brand popup did not open after clicking search result for: " + randomCampaign
    );

    System.out.println("Brand popup opened successfully for: " + randomCampaign);
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

}

