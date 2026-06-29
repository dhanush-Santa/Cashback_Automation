package pages;

import base.BaseTest;
import com.microsoft.playwright.*;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;

public class HomePage extends BaseTest {

    // ── Locators ────────────────────────────────────────────────────────────────
    private static final String MENU           = "//div[@class='sc-bTwLay bFNzwR']";
    private static final String ALL_CATEGORIES = "//p[contains(text(),'Categories')]";

    // ── URL ──────────────────────────────────────────────────────────────────────
    private static final String HOME_URL =
            "https://shopping.santabrowser.com/?uuid=2da68a9dd8c5a73";

    // ── Constructor ─────────────────────────────────────────────────────────────
    public HomePage(Page page) {
        this.page = page;
    }

    // ── Actions ──────────────────────────────────────────────────────────────────

    public void open() {
        page.navigate(HOME_URL);
        System.out.println("Cashback home page opened successfully");
    }

    public void clickMenu() {
        click("Menu", MENU);
    }

    public AllCategoriesPage clickAllCategories() {
        click("All Categories", ALL_CATEGORIES);
        return new AllCategoriesPage(page);
    }


public void iterateCategoriesAndCheckCampaigns(Page page) {
    // 1. Selector for tiles on the main page
    String tileSelector = ".sc-czgmHJ.fowaoO";
    // 2. Selector for the back button on the details page (the SVG container)
    String backButtonSelector = ".sc-exayXG .cursor-pointer svg";
    // 3. Selector for campaigns (Cashback buttons)
    String campaignSelector = "button:has-text('Cashback')";

    page.waitForSelector(tileSelector);
    int totalTiles = page.locator(tileSelector).count();

    for (int i = 0; i < totalTiles; i++) {
        // Step A: Click Category Tile
        Locator currentTile = page.locator(tileSelector).nth(i);
        String categoryName = currentTile.locator("h3").innerText();
        System.out.println("Checking Category: " + categoryName);
        currentTile.click();

        // Step B: Wait for details page to load
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForSelector(".sc-dwYcXH.bXozVc"); // Container for campaign details

        // Step C: Verify Campaigns
        int campaignCount = page.locator(campaignSelector).count();
        if (campaignCount > 0) {
            System.out.println("-> SUCCESS: Found " + campaignCount + " campaigns for " + categoryName);
        } else {
            System.out.println("-> WARNING: No campaigns found for " + categoryName);
        }
        

        
           
        // Step D: Click Back Button
        // We use the SVG selector found in the inspection
        Locator backBtn = page.locator(backButtonSelector).first();
        if (backBtn.isVisible()) {
            backBtn.click();
        } else {
            // Fallback if the UI back button isn't reachable
            page.goBack();
        }

        // Step E: Wait for main grid to return
        page.waitForSelector(tileSelector);
    }
}




}





