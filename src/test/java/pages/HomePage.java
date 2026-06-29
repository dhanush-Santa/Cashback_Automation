package pages;

import base.BaseTest;
import com.microsoft.playwright.Page;

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
}