package AutoTesting.AppiumT;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;    
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class feed1 extends AppTest {
    private static final Logger logger = LogManager.getLogger(feed1.class);
    public WebDriverWait wait;

    @BeforeClass
    public void setUp() throws MalformedURLException {
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        logger.info("Driver and WebDriverWait initialized");
    }

    @Test
    public void NameDisplay() {
        logger.info("Starting NameDisplay test");

        logger.info("Selecting English language");
        WebElement englishButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//android.widget.ImageView[@content-desc=\"English\"]")));
        englishButton.click();
        sleep(2000);

        logger.info("Clicking Next button");
        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//android.view.View[@content-desc=\"Next\"]")));
        nextButton.click();
        sleep(2000);

        try {
            logger.info("Navigating to Profile tab");
            WebElement profileTab = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.accessibilityId("Profile\nTab 4 of 4")));
            profileTab.click();

            logger.info("Verifying guest mode with light mode");
            WebElement guestModeIndicator = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//android.view.View[@content-desc=\"Guest Login\"]")));
            Assert.assertTrue(guestModeIndicator.isDisplayed(), "App is not in guest mode");

            WebElement themeStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//android.widget.ScrollView/android.widget.Switch[1]")));
            Assert.assertEquals(themeStatus.getAttribute("checked"), "false", "Expected light mode in guest mode");

            logger.info("Scrolling to Sign-In button");
            driver.findElement(AppiumBy.androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true))" +
                            ".scrollIntoView(new UiSelector().description(\"Sign-In\"));")).click();

            logger.info("Clicking Google Sign-In button");
            WebElement googleSignInButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ImageView[@content-desc=\"Sign in with Google\"]")));
            googleSignInButton.click();

            logger.info("Selecting Google account");
            WebElement accountOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.TextView[@resource-id='com.google.android.gms:id/account_display_name']")));
            accountOption.click();

            logger.info("Waiting for Profile tab to become available after login");
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//android.widget.ImageView[@content-desc='Profile\nTab 4 of 4']")));

            logger.info("Re-navigating to Profile tab");
            WebElement profileTab1 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ImageView[@content-desc='Profile\nTab 4 of 4']")));
            profileTab1.click();

            logger.info("Verifying login success");
            WebElement profileIndicator = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//android.view.View[@content-desc='Shuaib Malik']")));
            Assert.assertTrue(profileIndicator.isDisplayed(), "Google Sign-In failed");

            logger.info("Navigating to Feed tab");
            WebElement feedTab = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ImageView[contains(@content-desc, 'My Feed')]")));
            feedTab.click();
            logger.info("Clicked on Feed tab successfully.");

            // Wait for Feed tab content to load
            
            logger.info("Scrolling down Feed tab 3 times");
            int startX = driver.manage().window().getSize().getWidth() / 2; // Middle of screen
            int startY = (int) (driver.manage().window().getSize().getHeight() * 0.8); // 80% from top
            int endY = (int) (driver.manage().window().getSize().getHeight() * 0.2); // 20% from top

            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            for (int i = 0; i < 3; i++) {
                try {
                    logger.info("Screen size: " + driver.manage().window().getSize());
                    logger.info("Scrolling from (" + startX + ", " + startY + ") to (" + startX + ", " + endY + ")");
                    Sequence scroll = new Sequence(finger, 1)
                            .addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), startX, startY))
                            .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                            .addAction(finger.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), startX, endY))
                            .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

                    driver.perform(List.of(scroll));
                    logger.info("Performed scroll " + (i + 1));
                    Thread.sleep(1000); // Increased wait for UI to settle
                } catch (Exception e) {
                    logger.error("Scroll operation failed: " + e.getMessage() + "\nStack trace: " + Arrays.toString(e.getStackTrace()), e);
                    Assert.fail("Scroll operation failed: " + e.getMessage());
                }
            }

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