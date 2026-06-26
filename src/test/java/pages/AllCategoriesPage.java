package pages;

import base.BaseTest;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class AllCategoriesPage extends BaseTest {

    // ── Locators ────────────────────────────────────────────────────────────────
    private static final String SEARCH_INPUT        = "//input[@placeholder='Search']";
    private static final String SEARCH_RESULTS      = ".search_result_item";
    private static final String FIRST_SEARCH_RESULT = "//div[@class='pt-2']//div[@class='search_result_item'][1]";
    private static final String NO_RESULT_MESSAGE   = "//p[contains(text(),'No search result found!')]";
    private static final String SEARCH_CLEAR_ICON   = "//*[name()='svg' and contains(@class,'cursor-pointer')]";
    private static final String ALL_CATEGORIES_BACK_button="(//div[contains(@class,'items-center') and .//*[name()='svg']])[2]";
    private static final String Popular_filter_click="//button[contains(text(),'Popular')]";
    private static final String A_To_Z_filter_click="//div[contains(text(),'A-Z')]";
    private static final String Cashback_filter_click="//div[contains(text(),'Cashback%')]";



    // ── Constructor ─────────────────────────────────────────────────────────────
    public AllCategoriesPage(Page page) {
        this.page = page;
    }

    // ── Actions ──────────────────────────────────────────────────────────────────

    public BrandPage searchAndOpenBrand(String brandName) {
        enterSearchText(brandName);
        validateSearchResults(brandName);
        clickFirstSearchResult();
        return new BrandPage(page);
    }

    public void enterSearchText(String text) {
        try {
            System.out.println("Entering search text : " + text);
            Locator searchBox = page.locator(SEARCH_INPUT);
            searchBox.clear();
            searchBox.fill(text);
            System.out.println("Search text entered : " + text);
            page.waitForTimeout(2000);
        } catch (Exception e) {
            System.out.println("Failed to enter search text");
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
    }

    public void clearSearch() {
        click("Search Clear Icon", SEARCH_CLEAR_ICON);
    }
    public void clickBackButton() {
        click("All Categories Back Button", ALL_CATEGORIES_BACK_button);
    }

public void clickPopularFilter() {
        click("Popular Filter Button", Popular_filter_click);
    }
public void clickAToZFilter() {
        click("A-Z Filter Button", A_To_Z_filter_click);
    } 
    public void clickCashbackFilter() {
       
        click("Cashback% Filter Button", Cashback_filter_click);
    }  


public List<String> A_Z_filter_getCampaignNames() {

    List<String> campaignNames = new ArrayList<>();

    Locator campaigns = page.locator(
            "//div[@class='sc-liquwA gTREJt']//p[@class='sc-fTgapq iODjVi']"
    );

    for(int i = 0; i < campaigns.count(); i++) {
        campaignNames.add(campaigns.nth(i).innerText().trim());
    }

    return campaignNames;
}


public List<String> cashback_filter_getCommissionRates() {

    List<String> campaignNames = new ArrayList<>();

    Locator campaigns = page.locator(
            "//div[@class='sc-liquwA gTREJt']//p[@class='sc-foMnoT ezYCvz']"
    );
System.out.println("cashback rates campaigns : "+campaigns.allInnerTexts());
System.out.println("cashback rates campaigns count : "+campaigns.allTextContents());
System.out.println("cashback rates campaigns count : "+campaigns.count());   

    // for(int i = 0; i < campaigns.count(); i++) {
    //     campaignNames.add(campaigns.nth(i).innerText().trim());
    // }

    return campaignNames;
}


}