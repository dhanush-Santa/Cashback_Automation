package pages;

import base.BaseTest;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class AllCategoriesPage extends BaseTest {

    // ── Locators ────────────────────────────────────────────────────────────────
    private static final String SEARCH_INPUT        = "//input[@placeholder='Search']";
    private static final String SEARCH_RESULTS      = ".search_result_item";
    private static final String FIRST_SEARCH_RESULT = "//div[@class='pt-2']//div[@class='search_result_item'][1]";
    private static final String NO_RESULT_MESSAGE   = "//p[contains(text(),'No search result found!')]";
    private static final String SEARCH_CLEAR_ICON   = "//*[name()='svg' and contains(@class,'cursor-pointer')]";
    private static final String ALL_CATEGORIES_BACK_button = "(//div[contains(@class,'items-center') and .//*[name()='svg']])[2]";
    private static final String Popular_filter_click  = "//button[contains(text(),'Popular')]";
    private static final String A_To_Z_filter_click   = "//div[contains(text(),'A-Z')]";
  private static final String Cashback_filter_click = "//div[contains(normalize-space(text()),'Cashback %')]";
    private static final String CAMPAIGN_CARDS        = "//div[@class='sc-liquwA gTREJt']";

    // ── Timeouts ─────────────────────────────────────────────────────────────────
    private static final double DEFAULT_TIMEOUT = 10000; // 10 seconds
    // A-Z dropdown toggle button (reveals Popular, A-Z, Cashback% options)
private static final String A_Z_DROPDOWN_BUTTON = "//button[contains(text(),'A-Z')]";


    // ── Constructor ─────────────────────────────────────────────────────────────
    public AllCategoriesPage(Page page) {
        this.page = page;
    }

    // ── Actions ──────────────────────────────────────────────────────────────────

    public BrandPage searchAndOpenBrand(String brandName) {
        enterSearchText(brandName);
        waitForSearchResults();
        validateSearchResults(brandName);
        clickFirstSearchResult();
        return new BrandPage(page);
    }

    public void enterSearchText(String text) {
        try {
            System.out.println("Entering search text : " + text);

            // Wait for search box to be ready before typing
            page.waitForSelector(SEARCH_INPUT,
                    new Page.WaitForSelectorOptions()
                            .setState(WaitForSelectorState.VISIBLE)
                            .setTimeout(DEFAULT_TIMEOUT));

            Locator searchBox = page.locator(SEARCH_INPUT);
            searchBox.clear();
            searchBox.fill(text);
            System.out.println("Search text entered : " + text);

            // Wait for results or no-result message to appear
            waitForSearchResults();

        } catch (Exception e) {
            System.out.println("Failed to enter search text");
            e.printStackTrace();
        }
    }

    private void waitForSearchResults() {
        try {
            System.out.println("Waiting for search results or no-result message...");

            // Wait until either results appear OR the no-result message appears
            page.waitForFunction(
                    "document.querySelectorAll('.search_result_item').length > 0 || " +
                    "document.querySelector('p') && document.querySelector('p').innerText.includes('No search result found!')",
                    null,
                    new Page.WaitForFunctionOptions().setTimeout(DEFAULT_TIMEOUT)
            );

            System.out.println("Search results loaded");
        } catch (Exception e) {
            System.out.println("Timed out waiting for search results");
            e.printStackTrace();
        }
    }

    public void validateSearchResults(String brandName) {
        try {
            Locator searchResults = page.locator(SEARCH_RESULTS);
            int count = searchResults.count();

            if (count > 0) {
                boolean brandFound = false;

                for (int i = 0; i < count; i++) {
                    String resultText = searchResults.nth(i).innerText();
                    System.out.println("Search Result : " + resultText);

                    if (resultText.toLowerCase().contains(brandName.toLowerCase())) {
                        brandFound = true;
                        System.out.println("Search validation PASSED");
                        System.out.println(brandName + " found in search results");
                        break;
                    }
                }

                if (!brandFound) {
                    System.out.println("Search validation FAILED");
                    System.out.println(brandName + " not found in search results");
                }

            } else {
                Locator noResultMsg = page.locator(NO_RESULT_MESSAGE);
                System.out.println("No Result Message : " + noResultMsg.innerText());
                System.out.println("No search results for : " + brandName);
            }

        } catch (Exception e) {
            System.out.println("Failed to validate search results");
            e.printStackTrace();
        }
    }

    public void clickFirstSearchResult() {
        int count = page.locator(SEARCH_RESULTS).count();
        String target = count > 0 ? FIRST_SEARCH_RESULT : "";
        click("First Search Result", target);

        // Wait for brand page to open after clicking result
        page.waitForLoadState();
        System.out.println("Brand page load state reached after clicking first result");
    }

    public void clearSearch() {
        click("Search Clear Icon", SEARCH_CLEAR_ICON);

        // Wait for search results to clear
        try {
            page.waitForFunction(
                    "document.querySelectorAll('.search_result_item').length === 0",
                    null,
                    new Page.WaitForFunctionOptions().setTimeout(DEFAULT_TIMEOUT)
            );
            System.out.println("Search cleared successfully");
        } catch (Exception e) {
            System.out.println("Timed out waiting for search to clear");
            e.printStackTrace();
        }
    }

    public void clickBackButton() {
        click("All Categories Back Button", ALL_CATEGORIES_BACK_button);

        // Wait for home page menu to appear after going back
        try {
            page.waitForSelector(
                    "//div[@class='sc-bTwLay bFNzwR']",
                    new Page.WaitForSelectorOptions()
                            .setState(WaitForSelectorState.VISIBLE)
                            .setTimeout(DEFAULT_TIMEOUT)
            );
            System.out.println("Navigated back — home menu visible");
        } catch (Exception e) {
            System.out.println("Timed out waiting for home page after back navigation");
            e.printStackTrace();
        }
    }

    public void clickPopularFilter() {
        click("Popular Filter Button", Popular_filter_click);
        waitForCampaignCards("Popular");
    }

    public void clickAToZFilter() {
        click("A-Z Filter Button", A_To_Z_filter_click);
        waitForCampaignCards("A-Z");
    }

    public void clickCashbackFilter() {
        click("Cashback% Filter Button", Cashback_filter_click);
        waitForCampaignCards("Cashback%");
    }

    // Waits until campaign cards are rendered after a filter click
    private void waitForCampaignCards(String filterName) {
        try {
            System.out.println("Waiting for campaign cards after '" + filterName + "' filter...");
            page.waitForSelector(
                    CAMPAIGN_CARDS,
                    new Page.WaitForSelectorOptions()
                            .setState(WaitForSelectorState.VISIBLE)
                            .setTimeout(DEFAULT_TIMEOUT)
            );
            System.out.println("Campaign cards loaded for filter: " + filterName);
        } catch (Exception e) {
            System.out.println("Timed out waiting for campaign cards after filter: " + filterName);
            e.printStackTrace();
        }
    }

    public List<String> A_Z_filter_getCampaignNames() {
        List<String> campaignNames = new ArrayList<>();

        // Wait for name elements specifically before reading
        try {
            page.waitForSelector(
                    "//div[@class='sc-liquwA gTREJt']//p[@class='sc-fTgapq iODjVi']",
                    new Page.WaitForSelectorOptions()
                            .setState(WaitForSelectorState.VISIBLE)
                            .setTimeout(DEFAULT_TIMEOUT)
            );
        } catch (Exception e) {
            System.out.println("Timed out waiting for A-Z campaign name elements");
            e.printStackTrace();
        }

        Locator campaigns = page.locator(
                "//div[@class='sc-liquwA gTREJt']//p[@class='sc-fTgapq iODjVi']"
        );

        System.out.println("A-Z campaign name count: " + campaigns.count());
        for (int i = 0; i < campaigns.count(); i++) {
            campaignNames.add(campaigns.nth(i).innerText().trim());
        }

        return campaignNames;
    }

    public List<String> cashback_filter_getCommissionRates() {
        List<String> commissionRates = new ArrayList<>();

        // Wait for rate elements to be present before reading
        try {
            page.waitForSelector(
                    "//div[@class='sc-liquwA gTREJt']//p[@class='sc-foMnoT ezYCvz']",
                    new Page.WaitForSelectorOptions()
                            .setState(WaitForSelectorState.VISIBLE)
                            .setTimeout(DEFAULT_TIMEOUT)
            );
        } catch (Exception e) {
            System.out.println("Timed out waiting for cashback rate elements");
            e.printStackTrace();
        }

        Locator campaigns = page.locator(
                "//div[@class='sc-liquwA gTREJt']//p[@class='sc-foMnoT ezYCvz']"
        );

        System.out.println("Cashback filter — total rate elements found: " + campaigns.count());
        for (int i = 0; i < campaigns.count(); i++) {
            String rate = campaigns.nth(i).innerText().trim();
            System.out.println("Rate [" + i + "] : " + rate);
            commissionRates.add(rate);
        }

        return commissionRates;
    }
public void clickAToZDropdown() {
    click("A-Z Dropdown Button", A_Z_DROPDOWN_BUTTON);

    // Wait for filter options to appear after clicking dropdown
    try {
        page.waitForSelector(Popular_filter_click,
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(DEFAULT_TIMEOUT));
        System.out.println("Filter options visible after clicking A-Z dropdown");
    } catch (Exception e) {
        System.out.println("Timed out waiting for filter options after A-Z dropdown");
        e.printStackTrace();
    }
}





}