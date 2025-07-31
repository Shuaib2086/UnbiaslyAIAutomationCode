package AutoTesting.AppiumT;

import java.net.MalformedURLException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.appium.java_client.AppiumBy;

public class sourcewhileScroll extends AppTest {
    private static final Logger logger = LogManager.getLogger(sourcewhileScroll.class);
    public WebDriverWait wait;

    @BeforeClass
    public void setUp() throws MalformedURLException {
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        logger.info("Driver and WebDriverWait initialized");
    }

    @Test
    public void source() {
        logger.info("Starting Source Popup Persistence Test");

        try {
            logger.info("Selecting English language");
            WebElement englishButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ImageView[@content-desc=\"English\"]")));
            englishButton.click();
            sleep(3000);

            logger.info("Clicking Next button");
            WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.view.View[@content-desc=\"Next\"]")));
            nextButton.click();

            logger.info("Verifying feed page");
            WebElement guestModeIndicator = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//android.view.View[@content-desc=\"Summarised by UnbiaslyAI\"]")));
            Assert.assertTrue(guestModeIndicator.isDisplayed(), "Feed is not visible");

            logger.info("Navigating to Profile tab");
            WebElement profileTab = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.accessibilityId("Profile\nTab 4 of 4")));
            profileTab.click();

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

            logger.info("Clicking Source button");
            WebElement sourceButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.view.View[@content-desc=\"Summarised by UnbiaslyAI\"]/android.widget.ImageView[3]")));
            sourceButton.click();
            sleep(2000);

            // Step 1: Verify popup is visible
            By popupLocator = By.xpath("//android.view.View[contains(@content-desc, 'Based on a statement by')]");
            List<WebElement> popupElements = driver.findElements(popupLocator);
            Assert.assertTrue(popupElements.size() > 0 && popupElements.get(0).isDisplayed(), "Popup not displayed");

            // Step 2: Swipe to next news card using bounds
            logger.info("Swiping to next news card using W3C gesture");
            swipeBetweenCoordinates(720, 2300, 300);
            sleep(2000);

            // Step 3: Check if popup still persists
            List<WebElement> popupAfterScroll = driver.findElements(popupLocator);
            boolean stillVisible = false;
            for (WebElement el : popupAfterScroll) {
                if (el.isDisplayed()) {
                    stillVisible = true;
                    logger.warn("Popup still visible after scroll: " + el.getAttribute("content-desc"));
                    break;
                }
            }

            Assert.assertFalse(stillVisible, "Popup persisted after scrolling — Test Failed");
            logger.info("Popup did not persist — Test Passed");

        } catch (Exception e) {
            logger.error("Test failed: " + e.getMessage(), e);
            Assert.fail("Test case failed: " + e.getMessage());
        }
    }

    // Updated swipe using W3C PointerInput
    private void swipeBetweenCoordinates(int x, int yStart, int yEnd) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);

        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, yStart));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), x, yEnd));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(swipe));
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
