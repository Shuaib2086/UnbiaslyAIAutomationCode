package AutoTesting.AppiumT;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.time.Duration;

public class Notification2 extends AppTest {
    private static final Logger logger = LogManager.getLogger(Notification2.class);
    private WebDriverWait wait;

    @BeforeClass
    public void setUp() throws MalformedURLException {
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        logger.info("Driver and WebDriverWait initialized");
    }

    // Utility method to handle stale element retries
    private WebElement getFreshElement(By locator, int maxRetries) {
        WebElement element = null;
        int attempts = 0;
        while (attempts < maxRetries) {
            try {
                element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                element.isDisplayed(); // Force a check to ensure element is valid
                return element;
            } catch (StaleElementReferenceException e) {
                logger.warn("Stale element detected, retrying... Attempt " + (attempts + 1));
                attempts++;
            } catch (Exception e) {
                logger.warn("Failed to locate element: " + e.getMessage());
                throw e;
            }
        }
        throw new RuntimeException("Failed to get fresh element after " + maxRetries + " attempts");
    }

    // Utility method to get attribute with retries
    private String getElementAttribute(WebElement element, String attribute, By locator, int maxRetries) {
        int attempts = 0;
        while (attempts < maxRetries) {
            try {
                return element.getAttribute(attribute);
            } catch (StaleElementReferenceException e) {
                logger.warn("Stale element during attribute fetch, retrying... Attempt " + (attempts + 1));
                element = getFreshElement(locator, 1);
                attempts++;
            }
        }
        throw new RuntimeException("Failed to get attribute " + attribute + " after " + maxRetries + " attempts");
    }

    // Utility method to check notification panel
    private boolean isNotificationPresent(String expectedTitle) {
        try {
            driver.openNotifications();
            WebElement notification = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("android:id/title")));
            boolean isPresent = notification.isDisplayed() && notification.getText().contains(expectedTitle);
            driver.navigate().back(); // Close notification panel
            return isPresent;
        } catch (Exception e) {
            logger.warn("No notification found: " + e.getMessage());
            driver.navigate().back();
            return false;
        }
    }

    @Test
    public void notificationTest() {
        logger.info("Starting Notification test");

        try {
            // Select English language
            logger.info("Selecting English language");
            WebElement englishButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ImageView[@content-desc=\"English\"]")));
            englishButton.click();

            // Click Next button
            logger.info("Clicking Next button");
            WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.view.View[@content-desc=\"Next\"]")));
            nextButton.click();

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

            // Navigate to notification settings submenu
           logger.info("Navigating to notification settings submenu");
            WebElement notificationOption = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//android.view.View[@content-desc=\"Notification\"]")));
            Assert.assertTrue(notificationOption.isDisplayed(),"notification is not present");
            logger.info("verify");
            // Locate notification toggle in submenu
            By submenuLocator = By.xpath("(//android.widget.Switch[@resource-id=\"android:id/switch_widget\"])[1]");
            WebElement notificationToggle = getFreshElement(submenuLocator, 3);

            // Test notification OFF in guest mode
            logger.info("Testing notification OFF in guest mode");
            String initialState = getElementAttribute(notificationToggle, "checked", submenuLocator, 3);
            if (initialState.equals("true")) {
                logger.info("Notification is ON, turning OFF");
                notificationToggle.click();
                notificationToggle = getFreshElement(submenuLocator, 3);
                Assert.assertEquals(getElementAttribute(notificationToggle, "checked", submenuLocator, 3),
                        "false", "Failed to turn off notification in guest mode");
            } else {
                logger.info("Notification is already OFF");
            }
            Assert.assertFalse(isNotificationPresent("Test Notification"), "Notification should not be present when OFF");

            // Test notification ON in guest mode
            logger.info("Testing notification ON in guest mode");
            if (getElementAttribute(notificationToggle, "checked", submenuLocator, 3).equals("false")) {
                logger.info("Notification is OFF, turning ON");
                notificationToggle.click();
                notificationToggle = getFreshElement(submenuLocator, 3);
                Assert.assertEquals(getElementAttribute(notificationToggle, "checked", submenuLocator, 3),
                        "true", "Failed to turn on notification in guest mode");
            } else {
                logger.info("Notification is already ON");
            }
            // Simulate notification trigger (replace with actual app interaction)
            WebElement triggerNotification = getFreshElement(
                    By.xpath("//android.view.View[@content-desc=\"Send Notification\"]"), 3);
            triggerNotification.click();
            Assert.assertTrue(isNotificationPresent("Test Notification"), "Notification should be present when ON");

            // Navigate back from submenu
            logger.info("Navigating back from settings submenu");
            WebElement backButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ImageButton[@content-desc=\"Navigate up\"]")));
            backButton.click();

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

            // Verify dark mode option
            logger.info("Verifying dark mode option");
            WebElement darkModeOption = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//android.view.View[@content-desc='Dark Theme']")));
            Assert.assertTrue(darkModeOption.isDisplayed(), "Dark Mode option not visible");

            // Toggle dark mode
            logger.info("Toggling dark mode");
            WebElement darkModeToggle = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ScrollView/android.widget.Switch[1]")));
            darkModeToggle.click();
            themeStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//android.widget.ScrollView/android.widget.Switch[1]")));
            Assert.assertEquals(themeStatus.getAttribute("checked"), "true", "Dark mode not enabled");

            // Navigate to notification settings submenu again
            logger.info("Navigating to notification settings submenu after login");
            notificationOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ScrollView/android.widget.Switch[2]")));
            notificationOption.click();

            // Locate notification toggle in submenu
            notificationToggle = getFreshElement(submenuLocator, 3);

            // Test notification OFF after login
            logger.info("Testing notification OFF after login");
            initialState = getElementAttribute(notificationToggle, "checked", submenuLocator, 3);
            if (initialState.equals("true")) {
                logger.info("Notification is ON, turning OFF");
                notificationToggle.click();
                notificationToggle = getFreshElement(submenuLocator, 3);
                Assert.assertEquals(getElementAttribute(notificationToggle, "checked", submenuLocator, 3),
                        "false", "Failed to turn off notification after login");
            } else {
                logger.info("Notification is already OFF");
            }
            Assert.assertFalse(isNotificationPresent("Test Notification"), "Notification should not be present when OFF");

            // Test notification ON after login
            logger.info("Testing notification ON after login");
            if (getElementAttribute(notificationToggle, "checked", submenuLocator, 3).equals("false")) {
                logger.info("Notification is OFF, turning ON");
                notificationToggle.click();
                notificationToggle = getFreshElement(submenuLocator, 3);
                Assert.assertEquals(getElementAttribute(notificationToggle, "checked", submenuLocator, 3),
                        "true", "Failed to turn on notification after login");
            } else {
                logger.info("Notification is already ON");
            }
            // Simulate notification trigger (replace with actual app interaction)
            triggerNotification = getFreshElement(
                    By.xpath("//android.view.View[@content-desc=\"Send Notification\"]"), 3);
            triggerNotification.click();
            Assert.assertTrue(isNotificationPresent("Test Notification"), "Notification should be present when ON");

            // Navigate back from submenu
            logger.info("Navigating back from settings submenu");
            backButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ImageButton[@content-desc=\"Navigate up\"]")));
            backButton.click();

        } catch (Exception e) {
            logger.error("Test failed: " + e.getMessage(), e);
            try {
                String pageSource = driver.getPageSource();
                logger.debug("Page source: " + pageSource);
            } catch (Exception ex) {
                logger.warn("Failed to capture page source: " + ex.getMessage());
            }
            Assert.fail("Test case failed: " + e.getMessage());
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