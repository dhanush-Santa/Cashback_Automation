package pages;

import com.microsoft.playwright.*;

public class CashbackPage {

    private final Page page;

    public CashbackPage(Page page) {
        this.page = page;
    }

    // ================= LOCATORS =================
    private final String menu = "//div[@class='sc-bTwLay bFNzwR']";
    private final String categories = "//p[contains(text(),'Categories')]";
    private final String searchBox = "//input[@placeholder='Search']";
    private final String firstResult = "//div[@class='pt-2']//div[@class='search_result_item'][1]";
    private final String expandRates = "//button[@class='sc-fQpRED bUMSEu' or contains(text(),'See more rates')]";
    private final String clearIcon = "//*[name()='svg' and contains(@class,'cursor-pointer')]";
    private final String activateCashback =
            "//button[contains(@class,'activate_cashback')]";
    private final String closePopup =
            "(//button[.//*[name()='path' and contains(@d,'M6 18 18 6')]])[2]";

    // ================= ACTIONS =================

    public void openUrl(String url) {
        page.navigate(url);
        System.out.println("Cashback homepage opened");
    }

    public void clickMenu() {
        click(menu, "Menu");
    }

    public void clickCategories() {
        click(categories, "Categories");
    }

    public void searchBrand(String brand) {

        System.out.println("Searching brand: " + brand);

        Locator search = page.locator(searchBox);
        search.clear();
        search.fill(brand);

        page.waitForTimeout(2000);

        Locator results = page.locator(".search_result_item");

        int count = results.count();

        boolean found = false;

        if (count > 0) {

            for (int i = 0; i < count; i++) {

                String text = results.nth(i).innerText();
                System.out.println("Result: " + text);

                if (text.toLowerCase().contains(brand.toLowerCase())) {
                    found = true;
                    break;
                }
            }

            System.out.println(found
                    ? "Search validation PASSED"
                    : "Search validation FAILED");
        } else {
            System.out.println("No search results found");
        }

        if (count > 0) {
            click(firstResult, "First Result");
        }
    }

    public void clickActivateCashback() {

        Page newPage = page.context().waitForPage(() -> {
            click(activateCashback, "Activate Cashback");
        });

        newPage.waitForLoadState();
        System.out.println("New brand page opened");

        newPage.close();
        System.out.println("Brand page closed");
    }

    public void expandCashbackRates() {
        click(expandRates, "Expand Cashback Rates");
        click(expandRates, "Expand Cashback Rates");
    }

    public void closePopup() {
        click(closePopup, "Close Popup");
    }

    // ================= GENERIC CLICK =================

    private void click(String xpath, String name) {
        try {
            System.out.println("Clicking: " + name);
            page.locator(xpath).click();
        } catch (Exception e) {
            System.out.println("Failed clicking: " + name);
            System.out.println("XPath: " + xpath);
            e.printStackTrace();
        }
    }
}