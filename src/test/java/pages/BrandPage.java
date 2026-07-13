package pages;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;

import java.util.regex.Pattern;

public class BrandPage {

    private final Page page;

    public BrandPage(Page page) {
        this.page = page;
    }

    public void activateCashbackAndClose(BrowserContext context) {

        Page newTab = context.waitForPage(
                new BrowserContext.WaitForPageOptions().setTimeout(45000),
                () -> {
                    page.getByRole(AriaRole.BUTTON,
                            new Page.GetByRoleOptions().setName("Activate Cashback")
                    ).click();
                }
        );

        System.out.println("New tab opened, waiting for it to finish loading...");

        // Wait for the redirect chain to settle before doing anything else
        try {
            newTab.waitForLoadState(LoadState.NETWORKIDLE,
                    new Page.WaitForLoadStateOptions().setTimeout(20000));
        } catch (Exception e) {
            System.out.println("Networkidle wait timed out, falling back to LOAD state");
            newTab.waitForLoadState(LoadState.LOAD,
                    new Page.WaitForLoadStateOptions().setTimeout(10000));
        }

        System.out.println("New tab finished loading: " + newTab.url());

        newTab.close();
        System.out.println("New tab closed");
    }

    public void closeBrandPopup() {
        page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName(Pattern.compile("^$"))
        ).click(new com.microsoft.playwright.Locator.ClickOptions().setForce(true));
    }

    public boolean isActivateCashbackVisible() {
        return page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Activate Cashback")
        ).isVisible();
    }



    
}