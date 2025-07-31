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

import io.appium.java_client.AppiumBy;

public class LoginfromNotification extends AppTest {
    private static final Logger logger = LogManager.getLogger(LoginfromNotification.class);
    public WebDriverWait wait;

    @BeforeClass
    public void setUp() throws MalformedURLException {
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        logger.info("Driver and WebDriverWait initialized");
    }

    @Test
    public void source() {
        logger.info("Starting LoginfromNotification test");

        try {
            logger.info("Selecting English language");
            WebElement englishButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//android.widget.ImageView[@content-desc='English']")));
            englishButton.click();
            sleep(3000);

            logger.info("Clicking Next button");
            WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//android.view.View[@content-desc='Next']")));
            nextButton.click();

            logger.info("Verifying feed page");
            WebElement guestModeIndicator = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//android.view.View[@content-desc='Summarised by UnbiaslyAI']")));
            Assert.assertTrue(guestModeIndicator.isDisplayed(), "Feed is not visible");

            logger.info("Navigating to Notifications tab");
            WebElement notificationsTab = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//android.widget.ImageView[contains(@content-desc, 'Notifications') and contains(@content-desc, 'Tab 3 of 4')]")));
            notificationsTab.click();

            logger.info("Clicking Sign-In button");
            WebElement signButton = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.accessibilityId("Sign-In")));
            signButton.click();
            
            logger.info("2 screen clicking Sign-In button");
            WebElement SignInButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//android.view.View[@content-desc=\"Sign-In\"]")));
            SignInButton.click();

            logger.info("Clicking 2 Sign-In button");
            WebElement googleSignInButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//android.widget.ImageView[@content-desc='Sign in with Google']")));
            googleSignInButton.click();

            logger.info("Selecting Google account");
            WebElement accountOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//android.widget.TextView[@resource-id='com.google.android.gms:id/account_display_name']")));
            accountOption.click();
            Thread.sleep(4000);

        } catch (Exception e) {
            logger.error("Test failed: ", e);
            Assert.fail("Test case failed due to exception: " + e.getMessage());
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
