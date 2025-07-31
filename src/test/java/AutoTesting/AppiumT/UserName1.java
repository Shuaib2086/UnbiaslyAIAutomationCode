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
import java.util.List;

public class UserName1 extends AppTest {
    private static final Logger logger = LogManager.getLogger(UserName1.class);
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

            WebElement darkModeOption = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//android.view.View[@content-desc='Dark Theme']")));
            Assert.assertTrue(darkModeOption.isDisplayed(), "Dark Mode option not visible");

            logger.info("Toggling dark mode");
            WebElement darkModeToggle = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ScrollView/android.widget.Switch[1]")));
            darkModeToggle.click();

            themeStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//android.widget.ScrollView/android.widget.Switch[1]")));
            Assert.assertEquals(themeStatus.getAttribute("checked"), "true", "Dark mode not enabled");

            WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//android.view.View[@content-desc=\"Shuaib Malik\"]")));
            Assert.assertTrue(username.isDisplayed(), "Name is not displaying");

            logger.info("Clicking on edit name button");
            WebElement editNameButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ScrollView/android.widget.ImageView[1]")));
            editNameButton.click();

            String newName = "Shuaib Test";
            logger.info("Attempting to locate and edit name field");
            // Use a more reliable locator for the EditText field
            WebElement nameField = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.EditText")));
            if (nameField != null) {
                nameField.click(); // Ensure the field is focused
                nameField.clear(); // Clear existing text
                nameField.sendKeys(newName); // Enter new name
                driver.hideKeyboard();
                logger.info("Name edited successfully: " + newName);

                WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                        AppiumBy.accessibilityId("Submit")));
                submitButton.click();
                logger.info("Clicked Submit button");

                // Wait for the updated name to appear
                WebElement updatedName = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//android.view.View[@content-desc=\"" + newName + "\"]")));
                Assert.assertTrue(updatedName.isDisplayed(), "Updated name not displayed: " + newName);
                logger.info("Verified updated name: " + newName);
            } else {
                logger.error("EditText field for name not found");
                Assert.fail("Failed to locate EditText field for editing name");
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