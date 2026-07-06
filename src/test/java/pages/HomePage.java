package pages;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;

public class HomePage {

    private static final int DEFAULT_TIMEOUT = 10000;

    private final Page page;

    public HomePage(Page page) {
        this.page = page;
    }

    public boolean iterateCategoriesAndCheckCampaigns() {

        String tileSelector = ".sc-czgmHJ.fowaoO";
        String backButtonSelector = ".sc-exayXG .cursor-pointer svg";
        String campaignSelector = "button:has-text('Cashback')";

        page.waitForSelector(tileSelector);

        int totalTiles = page.locator(tileSelector).count();

        for (int i = 0; i < totalTiles; i++) {

            Locator currentTile = page.locator(tileSelector).nth(i);

            String categoryName = currentTile.locator("h3").innerText();

            currentTile.click();

            page.waitForLoadState(LoadState.NETWORKIDLE);

            int campaignCount = page.locator(campaignSelector).count();

            if (campaignCount == 0) {
                System.out.println("No campaigns found for " + categoryName);
                return false;
            }

            Locator backBtn = page.locator(backButtonSelector).first();
            backBtn.click();

            page.waitForSelector(tileSelector);
        }

        return true;
    }

    /*
     * -------------------------------------------------------------------------
     * Existing methods (kept as-is)
     * -------------------------------------------------------------------------
     */
//Categories that are present in the home page which are displayed as tiles.
    public void verifyAllCategoryFilters() {

        verifyCategory("Fashion");
        verifyCategory("Beauty");
        verifyCategory("Gaming");
        verifyCategory("Food & Grocery");
        verifyCategory("Electronics");
        verifyCategory("Travel");
        verifyCategory("Health & Fitness");
        verifyCategory("Home & Kitchen");
    }

    private void verifyCategory(String categoryName) {

        page.getByRole(
                AriaRole.HEADING,
                new Page.GetByRoleOptions().setName(categoryName))
                .click();

        verifyFilters();

        page.locator(".sc-jcHdAB > svg").click();
    }

    private void verifyFilters() {

        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Popular"))
                .click();

        page.getByText("A-Z").click();

        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("A-Z"))
                .click();

        page.getByText("Cashback %").click();
    }

    /*
     * -------------------------------------------------------------------------
     * New reusable methods for sorting validation
     * -------------------------------------------------------------------------
     */

    public void openCategory(String categoryName) {

        page.getByRole(
                AriaRole.HEADING,
                new Page.GetByRoleOptions().setName(categoryName))
                .click();
    }

    public void clickAZFilter() {

        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Popular"))
                .click();

        page.getByText("A-Z").click();
    }

    public void clickCashbackFilter() {

        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("A-Z"))
                .click();

        page.getByText("Cashback %").click();
    }

    public void closeFilterPopup() {

        page.locator(".sc-jcHdAB > svg").click();
    }

  

    public List<String> A_Z_filter_getCampaignNames() {

        page.waitForSelector(
                "//div[@class='sc-liquwA gTREJt']//p[@class='sc-fTgapq iODjVi']",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(DEFAULT_TIMEOUT));

        Locator campaigns = page.locator(
                "//div[@class='sc-liquwA gTREJt']//p[@class='sc-fTgapq iODjVi']");

        List<String> campaignNames = new ArrayList<>();

        int count = campaigns.count();

        for (int i = 0; i < count; i++) {
            campaignNames.add(campaigns.nth(i).innerText().trim());
        }

        return campaignNames;
    }

    /*
     * -------------------------------------------------------------------------
     * Returns cashback percentages after Cashback filter
     * -------------------------------------------------------------------------
     */

    public List<String> cashback_filter_getCommissionRates() {

        page.waitForSelector(
                "//div[@class='sc-liquwA gTREJt']//p[@class='sc-foMnoT ezYCvz']",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(DEFAULT_TIMEOUT));

        Locator campaigns = page.locator(
                "//div[@class='sc-liquwA gTREJt']//p[@class='sc-foMnoT ezYCvz']");

        List<String> commissionRates = new ArrayList<>();

        int count = campaigns.count();

        for (int i = 0; i < count; i++) {
            commissionRates.add(campaigns.nth(i).innerText().trim());
        }

        return commissionRates;
    }
public List<String> getCampaignNames() {

    page.waitForSelector("//div[@class='sc-liquwA gTREJt']//p[@class='sc-fTgapq iODjVi']");

    return page.locator("//div[@class='sc-liquwA gTREJt']//p[@class='sc-fTgapq iODjVi']")
            .allInnerTexts();
}

public void changeCountryToUnitedStates() {

    page.getByRole(AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("India"))
            .click();

    page.getByRole(AriaRole.TEXTBOX,
            new Page.GetByRoleOptions().setName("India"))
            .fill("uni");

    page.getByRole(AriaRole.LINK,
            new Page.GetByRoleOptions().setName("United States"))
            .click();
}

public void openAllCategories() {

    page.locator(".sc-bTwLay").click();

    page.getByRole(
            AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("All Categories All Categories"))
            .click();
}
public void openCategoriesMenu() {

    page.locator(".sc-bTwLay").click();
}
public List<String> getAllCategories() {

    page.waitForSelector("p.sc-hDcvty.fyGVpJ");

    return page.locator("p.sc-hDcvty.fyGVpJ")
               .allInnerTexts();
}
public void clickCategory(String categoryName) {

    page.getByRole(
            AriaRole.BUTTON,
            new Page.GetByRoleOptions()
                    .setName(categoryName + " " + categoryName))
            .click();
}
public void closeCategoryPopup() {

    page.locator(".sc-jcHdAB > svg").click();
}

}