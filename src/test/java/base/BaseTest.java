package base;

import com.microsoft.playwright.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.nio.file.Paths;
import java.util.List;

public class BaseTest {

    public Playwright playwright;
    public Browser browser;
    public BrowserContext context;
    public Page page;

    // ── Browser Setup ────────────────────────────────────────────────────────────

    @BeforeMethod
    public void setUp() {
        playwright = Playwright.create();

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setExecutablePath(Paths.get("C:\\Users\\DELL\\AppData\\Local\\Santa\\Application\\santa.exe"))
                        .setHeadless(false)
                        .setSlowMo(1000)
                        .setArgs(List.of("--start-maximized"))
        );

    context = browser.newContext(
            new Browser.NewContextOptions()
                    .setViewportSize(null)
    );

    page = context.newPage();
    System.out.println("Santa Browser setup complete");
}
    // ── Browser Teardown ─────────────────────────────────────────────────────────

    @AfterMethod
    public void tearDown() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
        System.out.println("Browser closed");
    }

    // ── Shared Action Helpers ────────────────────────────────────────────────────

    public void click(String name, String xpath) {
        try {
            System.out.println("Trying to click on : " + name);
            page.locator(xpath).click();
            System.out.println(name + " clicked successfully");
        } catch (Exception e) {
            System.out.println("Failed to click on : " + name);
            System.out.println("XPath used : " + xpath);
            e.printStackTrace();
        }
    }

    public void fill(String name, String xpath, String value) {
        try {
            System.out.println("Trying to fill : " + name);
            page.locator(xpath).clear();
            page.locator(xpath).fill(value);
            System.out.println("Filled " + name + " with : " + value);
        } catch (Exception e) {
            System.out.println("Failed to fill : " + name);
            System.out.println("XPath used : " + xpath);
            e.printStackTrace();
        }
    }
}