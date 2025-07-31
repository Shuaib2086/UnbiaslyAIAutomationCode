package AutoTesting.AppiumT;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
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
import java.util.List;

public class UnBookMarking1 extends AppTest {
    private static final Logger logger = LogManager.getLogger(UnBookMarking1.class);
    private WebDriverWait wait;
    

    @BeforeClass
    public void setUp() throws MalformedURLException {
    	 wait = new WebDriverWait(driver, Duration.ofSeconds(20));
         logger.info("Driver and WebDriverWait initialized");
    }

    @Test
    public void NameDisplay() {
        logger.info("Starting NameDisplay test");

        try {
            // Select English language
            logger.info("Selecting English language");
            WebElement englishButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ImageView[@content-desc=\"English\"]")));
            englishButton.click();
            sleep(4000);

            // Click Next button
            logger.info("Clicking Next button");
            WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.view.View[@content-desc=\"Next\"]")));
            nextButton.click();
            sleep(4000);

            // Navigate to Profile tab
            logger.info("Navigating to Profile tab");
            WebElement profileTab = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.accessibilityId("Profile\nTab 4 of 4")));
            profileTab.click();

            // Verify guest mode with light mode
            logger.info("Verifying guest mode with light mode");
            WebElement guestModeIndicator = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//android.view.View[@content-desc=\"Guest Login\"]")));
            Assert.assertTrue(guestModeIndicator.isDisplayed(), "App is not in guest mode");

            WebElement themeStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//android.widget.ScrollView/android.widget.Switch[1]")));
            Assert.assertEquals(themeStatus.getAttribute("checked"), "false", "Expected light mode in guest mode");

            // Scroll to and click Sign-In button
            logger.info("Scrolling to Sign-In button");
            driver.findElement(AppiumBy.androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true))" +
                            ".scrollIntoView(new UiSelector().description(\"Sign-In\"));")).click();

            // Click Google Sign-In button
            logger.info("Clicking Google Sign-In button");
            WebElement googleSignInButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ImageView[@content-desc=\"Sign in with Google\"]")));
            googleSignInButton.click();

            // Select Google account
            logger.info("Selecting Google account");
            WebElement accountOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.TextView[@resource-id='com.google.android.gms:id/account_display_name']")));
            accountOption.click();

            // Wait for Profile tab after login
            logger.info("Waiting for Profile tab to become available after login");
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//android.widget.ImageView[@content-desc='Profile\nTab 4 of 4']")));

            // Re-navigate to Profile tab
            logger.info("Re-navigating to Profile tab");
            WebElement profileTab1 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ImageView[@content-desc='Profile\nTab 4 of 4']")));
            profileTab1.click();

            // Verify login success
            logger.info("Verifying login success");
            WebElement profileIndicator = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//android.view.View[@content-desc='Shuaib Malik']")));
            Assert.assertTrue(profileIndicator.isDisplayed(), "Google Sign-In failed");

            // Navigate to Feed tab
            logger.info("Navigating to Feed tab");
            WebElement feedTab = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ImageView[contains(@content-desc, 'My Feed')]")));
            feedTab.click();
            logger.info("Clicked on Feed tab successfully.");

            // Bookmark some items for testing (optional, kept for setup)
            logger.info("Scrolling down Feed tab 3 times and bookmarking card after each scroll");
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
                    sleep(4000);

                    logger.info("Attempting to bookmark card after scroll " + (i + 1));
                    try {
                        WebElement bookmarkButton = wait.until(ExpectedConditions.elementToBeClickable(
                                By.xpath("//android.view.View[@content-desc=\"Summarised by UnbiaslyAI\"]/android.widget.ImageView[4]")));
                        bookmarkButton.click();
                        logger.info("Clicked bookmark button successfully after scroll " + (i + 1));
                        Assert.assertTrue(bookmarkButton.isEnabled(), "Bookmark button was not clickable after scroll " + (i + 1));
                    } catch (Exception e) {
                        logger.warn("Bookmark button not found or not clickable after scroll " + (i + 1) + ": " + e.getMessage());
                    }
                } catch (Exception e) {
                    logger.error("Scroll operation failed after scroll " + (i + 1) + ": " + e.getMessage() + "\nStack trace: " + Arrays.toString(e.getStackTrace()), e);
                    Assert.fail("Scroll operation failed after scroll " + (i + 1) + ": " + e.getMessage());
                }
            }

            // Navigate to Profile tab and Bookmarked News
            logger.info("Navigating to Profile tab");
            WebElement profileTab11 = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.accessibilityId("Profile\nTab 4 of 4")));
            profileTab11.click();

            logger.info("Navigating to Bookmarked News");
            WebElement bookmarked = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.accessibilityId("Bookmarked News")));
            bookmarked.click();

            // Verify bookmarked news is present
            logger.info("Verifying bookmarked news is present");
            WebElement bookmarkedNews = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View")));
            Assert.assertTrue(bookmarkedNews.isDisplayed(), "Bookmarked news is not present");
            logger.info("Successfully verified bookmarked news is present");

            // Unbookmark all news
            unbookmarkAllNews();

        } catch (Exception e) {
            logger.error("Test failed: " + e.getMessage(), e);
            Assert.fail("Test case failed: " + e.getMessage());
        }
    }

    private void unbookmarkAllNews() {
        logger.info("Starting to unbookmark all news");

        int startX = driver.manage().window().getSize().getWidth() / 2; // Middle of screen
        int startY = (int) (driver.manage().window().getSize().getHeight() * 0.8); // 80% from top
        int endY = (int) (driver.manage().window().getSize().getHeight() * 0.2); // 20% from top
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        int maxIterations = 100; // Prevent infinite loop
        int currentIteration = 0;

        while (currentIteration < maxIterations) {
            try {
                // Check if a bookmarked item exists
                WebElement bookmarkedItem = wait.until(ExpectedConditions.presenceOfElementLocated(
                        AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.ImageView\").instance(4)")));

                if (bookmarkedItem == null || !bookmarkedItem.isDisplayed()) {
                    logger.info("No more bookmarked news items visible. Attempting to scroll.");
                    if (!scrollToNextItem(startX, startY, endY, finger)) {
                        logger.info("No more items found after scrolling. Exiting.");
                        break;
                    }
                    continue;
                }

                logger.info("Found a bookmarked news item. Attempting to unbookmark.");

                // Click the unbookmark button
                WebElement unbookmarkButton = wait.until(ExpectedConditions.elementToBeClickable(
                        AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.ImageView\").instance(4)")));
                unbookmarkButton.click();

                logger.info("Successfully unbookmarked a news item");
                Assert.assertTrue(unbookmarkButton.isEnabled(), "Unbookmark button was not clickable");

                // Wait for UI to update
                sleep(4000);

            } catch (Exception e) {
                logger.warn("Unbookmark button not found or not clickable: " + e.getMessage());
                // Attempt to scroll one last time before exiting
                if (!scrollToNextItem(startX, startY, endY, finger)) {
                    logger.info("No more items found after scrolling. Exiting.");
                    break;
                }
            }
            currentIteration++;
        }

        logger.info("Finished unbookmarking all news");
    }

    private boolean scrollToNextItem(int startX, int startY, int endY, PointerInput finger) {
        try {
            logger.info("Screen size: " + driver.manage().window().getSize());
            logger.info("Scrolling from (" + startX + ", " + startY + ") to (" + startX + ", " + endY + ")");

            Sequence scroll = new Sequence(finger, 1)
                    .addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), startX, startY))
                    .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                    .addAction(finger.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), startX, endY))
                    .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

            driver.perform(List.of(scroll));
            logger.info("Performed scroll");
            sleep(4000); // Wait 4 seconds after scroll

            // Check if new items are visible after scrolling
            WebElement bookmarkItem = wait.until(ExpectedConditions.presenceOfElementLocated(
                    AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.ImageView\").instance(4)")));
            return bookmarkItem != null && bookmarkItem.isDisplayed();

        } catch (Exception e) {
            logger.warn("Scroll operation failed: " + e.getMessage() + "\nStack trace: " + Arrays.toString(e.getStackTrace()));
            return false;
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