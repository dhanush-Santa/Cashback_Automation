package pages;

import base.BaseTest;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;

public class BrandPage extends BaseTest {

    // ── Locators ────────────────────────────────────────────────────────────────
    private static final String ACTIVATE_CASHBACK =
            "//button[@class='py-[3px] px-3 bg-main rounded-full hover:opacity-70 " +
            "inline-flex justify-center items-center gap-2.5 text-white text-xs " +
            "font-medium transition-opacity ease-in-out duration-300 !rounded-5 " +
            "!py-1.5 !px-3 !text-sm activate_cashback']";

    private static final String EXPAND_CASHBACK_RATES =
            "//button[@class='sc-fQpRED bUMSEu' or contains(text(),'See more rates')]";

    private static final String CLOSE_BRAND_POPUP =
            "(//button[.//*[name()='path' and contains(@d,'M6 18 18 6')]])[2]";

    // ── Constructor ─────────────────────────────────────────────────────────────
    public BrandPage(Page page) {
        this.page = page;
    }

    // ── Actions ──────────────────────────────────────────────────────────────────

    public void activateCashbackAndClose(BrowserContext context, int waitMs)
            throws InterruptedException {

        Page brandExternalPage = context.waitForPage(() ->
                click("Activate Cashback", ACTIVATE_CASHBACK)
        );

        brandExternalPage.waitForLoadState();
        Thread.sleep(waitMs);
        brandExternalPage.close();
        System.out.println("Brand external page closed");
    }

    public void expandCashbackRates() {
        click("Expand Cashback Rates", EXPAND_CASHBACK_RATES);
    }

    public void closeBrandPopup() {
        click("Close Brand Popup", CLOSE_BRAND_POPUP);
    }
}