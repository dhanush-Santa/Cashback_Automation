package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AllCategoriesPage {

    private final Page page;

    public AllCategoriesPage(Page page) {
        this.page = page;
    }

    // ───────────────────────── Navigation ─────────────────────────

    public void openAllCategories() {
        page.locator(".sc-bTwLay").click();
        page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("All Categories All Categories")
        ).click();
    }

    public void ensureOnAllCategories() {
        openAllCategories();
    }

    public void goBackFromAllCategories() {
        Locator backButton = page.locator(".sc-jcHdAB");
        backButton.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));

        backButton.click(new Locator.ClickOptions().setForce(true));
    }

    // ───────────────────────── Brand selection ─────────────────────────

    /**
     * Picks a random brand card from the actual grid (not a raw nth-child guess)
     * and returns its name.
     */
    public String openRandomBrandAndGetName() {
        String gridLocator = "div.sc-liquwA.gTREJt";

        Locator cards = page.locator(gridLocator);
        cards.first().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));

        int count = cards.count();
        System.out.println("Brand cards found in grid: " + count);
        Assert.assertTrue(count > 0, "No brand cards found in grid");

        int randomIndex = new Random().nextInt(count); // 0-based
        Locator card = cards.nth(randomIndex);
        card.scrollIntoViewIfNeeded();

        String brandName = card.locator("p.sc-fTgapq.iODjVi").first().innerText().trim();
        System.out.println("Randomly selected brand at position " + (randomIndex + 1) + " → " + brandName);

        card.click();
        return brandName;
    }

    // Keep index-based method too, in case a specific position is needed
    public String openBrandByIndexAndGetName(int index) {
        String gridLocator = "div.sc-liquwA.gTREJt";

        Locator cards = page.locator(gridLocator);
        cards.first().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));

        int count = cards.count();
        Assert.assertTrue(count >= index, "Requested index " + index + " exceeds available cards: " + count);

        Locator card = cards.nth(index - 1);
        card.scrollIntoViewIfNeeded();

        String brandName = card.locator("p.sc-fTgapq.iODjVi").first().innerText().trim();
        System.out.println("Opening brand at index " + index + " → " + brandName);

        card.click();
        return brandName;
    }

    public List<String> getAllCampaignsWithScroll() {
        String campaignSelector = "//div[@class='sc-liquwA gTREJt']//p[@class='sc-fTgapq iODjVi']";

        List<String> campaigns = new ArrayList<>();
        int previousSize = 0;

        while (true) {
            List<String> current = page.locator(campaignSelector).allInnerTexts();
            for (String c : current) {
                if (!campaigns.contains(c.trim())) campaigns.add(c.trim());
            }

            page.mouse().wheel(0, 2000);
            page.waitForTimeout(800);

            if (campaigns.size() == previousSize) break;
            previousSize = campaigns.size();
        }

        System.out.println("Total campaigns collected: " + campaigns.size());
        return campaigns;
    }

    // ───────────────────────── Search ─────────────────────────

    public void searchCampaign(String text) {
        page.locator("input[placeholder='Search']").fill(text);
    }

    public List<String> getSearchResults() {
        return page.locator(".search_result_item").allInnerTexts();
    }

    public boolean isNoResultDisplayed() {
        return page.locator("text=No search result found!").isVisible();
    }

    // ───────────────────────── Filters ─────────────────────────

    public void applyAZFilter() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Popular")).click();
        page.getByText("A-Z").click();
        page.waitForSelector("//div[@class='sc-liquwA gTREJt']");
    }

    public void applyCashbackFilter() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Popular")).click();
        page.getByText("Cashback %").click();
        page.waitForSelector("//div[@class='sc-liquwA gTREJt']");
    }

    public List<String> getCampaignNames() {
        String locator = "//div[@class='sc-liquwA gTREJt']//p[@class='sc-fTgapq iODjVi']";
        page.waitForSelector(locator);
        return page.locator(locator).allInnerTexts();
    }

    public List<String> getCashbackRates() {
        String locator = "//div[@class='sc-liquwA gTREJt']//p[@class='sc-foMnoT ezYCvz']";
        page.waitForSelector(locator);
        return page.locator(locator).allInnerTexts();
    }

    public void scrollToLoadCampaigns() {
        page.evaluate("""
            async () => {
                await new Promise((resolve) => {
                    let totalHeight = 0;
                    let distance = 100;
                    let timer = setInterval(() => {
                        let scrollHeight = document.body.scrollHeight;
                        window.scrollBy(0, distance);
                        totalHeight += distance;
                        if (totalHeight >= scrollHeight) {
                            clearInterval(timer);
                            resolve();
                        }
                    }, 100);
                });
            }
        """);
    }

    // ───────────────────────── Earnings / Clicks ─────────────────────────

    public void clickTotalEarnings() {
        page.getByText("TOTAL EARNINGS").click();
        page.waitForURL("**/earnings/**", new Page.WaitForURLOptions().setTimeout(10000));
    }

    /**
     * Reads the store name from the latest click row using the exact xpath
     * confirmed from DevTools (tbody[1] = most recent click).
     */
    public String getLatestClickStoreName() {
        String xpath = "/html/body/div/div/div[2]/div[3]/div/div[1]/div[3]/div[1]/table/tbody[1]";

        Locator row = page.locator("xpath=" + xpath);
        row.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));

        // Name is the <p> inside .table_name_text within that row
        Locator nameEl = row.locator(".table_name_text p").first();
        nameEl.waitFor(new Locator.WaitForOptions().setTimeout(10000));

        String text = nameEl.innerText().trim();
        System.out.println("Latest click store: " + text);
        return text;
    }
}