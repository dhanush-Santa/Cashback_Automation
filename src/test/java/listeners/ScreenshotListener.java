package listeners;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import org.testng.ITestListener;
import org.testng.ITestResult;
import base.BaseTest;

import java.io.ByteArrayInputStream;

public class ScreenshotListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        Object testInstance = result.getInstance();
        try {
            Page page = ((BaseTest) testInstance).getPage();

            byte[] screenshot = page.screenshot(
                new Page.ScreenshotOptions().setFullPage(true)
            );

            Allure.addAttachment(
                "Screenshot on Failure - " + result.getName(),
                new ByteArrayInputStream(screenshot)
            );
        } catch (Exception e) {
            System.err.println("Could not capture screenshot: " + e.getMessage());
        }
    }
}




