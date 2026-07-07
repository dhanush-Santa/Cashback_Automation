package base;

import com.microsoft.playwright.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.nio.file.Paths;
import java.util.List;

public class BaseTest {

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    protected final String BASE_URL = "https://shopping.santabrowser.com/?uuid=2da68a9dd8c5a73";

    @BeforeClass
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

        System.out.println("Browser launched");
    }

    @BeforeMethod
    public void openCashback() {

        System.out.println("Opening Cashback Home Page");

        page.navigate(BASE_URL);
    }

    @AfterClass
    public void tearDown() {

        if (context != null)
            context.close();

        if (browser != null)
            browser.close();

        if (playwright != null)
            playwright.close();

        System.out.println("Browser Closed");
    }
}