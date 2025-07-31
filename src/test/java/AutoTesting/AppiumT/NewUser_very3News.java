package AutoTesting.AppiumT;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
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

import java.net.MalformedURLException;
import java.time.Duration;

public class NewUser_very3News extends AppTest {
    private static final Logger logger = LogManager.getLogger(NewUser_very3News.class);
    public WebDriverWait wait;

    @BeforeClass
    public void setUp() throws MalformedURLException {
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        logger.info("Driver and WebDriverWait initialized");
    }

    @Test
    public void NameDisplay() {
        logger.info("Starting NameDisplay test");

        selectLanguageAndProceed();

        try {
            verifyGuestMode();
            performGoogleSignIn();
            verifyLoginSuccess();
            deleteAccountFlow();
            verifyGuestMode();
            performGoogleSignIn();
            verifyLoginSuccess();
            validateCategories();

        } catch (Exception e) {
            logger.error("Test failed: " + e.getMessage(), e);
            Assert.fail("Test case failed: " + e.getMessage());
        }
    }

    private void selectLanguageAndProceed() {
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
    }

    private void verifyGuestMode() {
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
    }

    private void performGoogleSignIn() {
        logger.info("Clicking Google Sign-In button");
        WebElement googleSignInButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//android.widget.ImageView[@content-desc=\"Sign in with Google\"]")));
        googleSignInButton.click();

        logger.info("Selecting Google account");
        WebElement accountOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//android.widget.TextView[@resource-id='com.google.android.gms:id/account_display_name']")));
        accountOption.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(
                AppiumBy.accessibilityId("Profile\nTab 4 of 4")));
    }

    private void verifyLoginSuccess() {
        logger.info("Navigating to Profile tab");
        WebElement profileTab = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.accessibilityId("Profile\nTab 4 of 4")));
        profileTab.click();

        logger.info("Verifying login success");
        WebElement profileIndicator = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//android.view.View[@content-desc='Shuaib Malik']")));
        Assert.assertTrue(profileIndicator.isDisplayed(), "Google Sign-In failed");
    }

    private void deleteAccountFlow() {
        logger.info("Scrolling to Account");
        driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))" +
                        ".scrollIntoView(new UiSelector().description(\"Account\"));")).click();

        logger.info("Clicking delete button");
        WebElement deleteTab1 = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//android.widget.ImageView[@content-desc=\"Delete Account\"]")));
        deleteTab1.click();

        logger.info("Clicking confirm delete button");
        WebElement deleteTab = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//android.view.View[@content-desc=\"Delete Account\"])[2]")));
        deleteTab.click();

        logger.info("Clicking final delete confirmation");
        WebElement deleteTab2 = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//android.view.View[@content-desc=\"Delete\"]")));
        deleteTab2.click();

        logger.info("Waiting for feed tab to become available after deleting account");
        wait.until(ExpectedConditions.presenceOfElementLocated(
                AppiumBy.accessibilityId("My Feed\nTab 2 of 4")));
    }

    private void validateCategories() {
        WebElement discoverButton = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.accessibilityId("Discover\nTab 1 of 4")));
        discoverButton.click();

        WebElement categoryTop = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//android.view.View[@content-desc=\"Top News\"]")));
        Assert.assertTrue(categoryTop.isDisplayed(), "Top news is not selected");

        WebElement categoryBreaking = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//android.view.View[@content-desc=\"Breaking News\"]")));
        Assert.assertTrue(categoryBreaking.isDisplayed(), "Breaking news is not selected");

        WebElement categoryTrending = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//android.view.View[@content-desc=\"Trending News\"]")));
        Assert.assertTrue(categoryTrending.isDisplayed(), "Trending news is not selected");

        logger.info("Three auto selected news are present");
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
