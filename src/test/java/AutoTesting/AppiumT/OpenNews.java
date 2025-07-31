package AutoTesting.AppiumT;

import java.net.MalformedURLException;
import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class OpenNews extends AppTest {
    private static final Logger logger = LogManager.getLogger(OpenNews.class);
    public WebDriverWait wait;

    @BeforeClass
    public void setUp() throws MalformedURLException {
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        logger.info("Driver and WebDriverWait initialized");
    }

    @Test
    public void source() {
        logger.info("Starting source test");

        try {
            // Select English language
            logger.info("Selecting English language");
            WebElement englishButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ImageView[@content-desc=\"English\"]")));
            englishButton.click();
            sleep(3000);

            // Click Next
            logger.info("Clicking Next button");
            WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.view.View[@content-desc=\"Next\"]")));
            nextButton.click();

            // Verify feed page
            logger.info("Verifying feed page");
            WebElement guestModeIndicator = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//android.view.View[@content-desc=\"Summarised by UnbiaslyAI\"]")));
            Assert.assertTrue(guestModeIndicator.isDisplayed(), "Feed is not visible");

            // Locate and click news item (first time)
            logger.info("Clicking news item");
            By newsItemBy = By.xpath("//android.view.View[contains(@content-desc, 'Tap to read more')]");
            WebElement newsItem = wait.until(ExpectedConditions.elementToBeClickable(newsItemBy));
            newsItem.click();

            Thread.sleep(4000);

            // Re-locate and click news item (second time)
          // logger.info("Clicking news item again to expand content");
           // newsItem = wait.until(ExpectedConditions.elementToBeClickable(newsItemBy));
            //newsItem.click();

            // Wait for news content
            logger.info("Waiting for news content to load");
            WebElement newsContent = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//android.view.View[contains(@content-desc, 'by')]")));
            Assert.assertTrue(newsContent.isDisplayed(), "News content is not visible");
            logger.info("News content loaded successfully");

            // Click Back button
            logger.info("Clicking Back button");
            WebElement backButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[1]")));
            backButton.click();

        } catch (Exception e) {
            logger.error("Test failed: " + e.getMessage(), e);
            Assert.fail("Test case failed: " + e.getMessage());
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Sleep interrupted", e);
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            logger.info("Appium driver closed");
        }
    }
}
