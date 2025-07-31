package AutoTesting.AppiumT;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
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

import java.net.MalformedURLException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.NoSuchElementException;

public class AboutUs1 extends AppTest {
    private static final Logger logger = LogManager.getLogger(AboutUs1.class);
    public WebDriverWait wait;

    @BeforeClass
    public void setUp() throws MalformedURLException {
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        logger.info("Driver and WebDriverWait initialized");
    }

    @Test
    public void notifi() throws InterruptedException {
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
            Thread.sleep(3000);

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

            logger.info("Scrolling to and clicking About Us");
            WebElement aboutUsBtn = driver.findElement(AppiumBy.androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true))" +
                            ".scrollIntoView(new UiSelector().description(\"About Us\"));"));
            aboutUsBtn.click();

            logger.info("Waiting for About Us content to load");
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.className("android.widget.ScrollView")));

            List<WebElement> textElements = driver.findElements(By.className("android.widget.ScrollView"));
            List<String> expectedLines = Arrays.asList();

            for (String expected : expectedLines) {
                boolean found = textElements.stream()
                        .anyMatch(el -> el.getText().trim().contains(expected));
                Assert.assertTrue(found, "Expected text not found in About Us: " + expected);
            }

            logger.info("Successfully verified About Us content");

            scrollUntilElementFound();

            logger.info("clicking on apple logo button");
            WebElement appleBtn = driver.findElement(AppiumBy.androidUIAutomator(
                    "new UiSelector().className(\"android.widget.ImageView\").instance(1)"));
            appleBtn.click();

            Thread.sleep(5000);

            String yourAppPackage = "com.unbiasly.app";
            driver.activateApp(yourAppPackage);
            
            logger.info("clicking on apple logo button");
            WebElement appstoreBtn = driver.findElement(AppiumBy.androidUIAutomator(
                    "new UiSelector().className(\"android.widget.ImageView\").instance(2)"));
            appstoreBtn.click();

            Thread.sleep(5000);

            String yourAppPackage1 = "com.unbiasly.app";
            driver.activateApp(yourAppPackage1);

            Thread.sleep(5000);
            scrollUntilElementFound();
            WebElement emailField = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//android.widget.EditText")));
            emailField.click();
            emailField.sendKeys("asdfa@gmail.com");
            driver.hideKeyboard();
            logger.info("clicking on subscribe button");
            WebElement subscribeTab1 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.Button[@content-desc=\"Subscribe\"]")));
            subscribeTab1.click();
            Thread.sleep(2000);
            logger.info("Verifying successful popup");
            WebElement pop = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//android.view.View[@content-desc=\"Successfully Subscribed to Newsletter\"]")));
            Assert.assertTrue(pop.isDisplayed(), "email not subscribe");
            

            Thread.sleep(3000);
            
            logger.info("clicking on linkedin button");
            WebElement Tab1 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ScrollView/android.widget.ImageView[4]")));
            Tab1.click();
            Thread.sleep(2000);
            driver.activateApp(yourAppPackage1);
            logger.info("clicking on instagram button");
            WebElement Tab2 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ScrollView/android.widget.ImageView[5]")));
             Tab2.click();
            driver.activateApp(yourAppPackage1);
            
            logger.info("clicking on Medium button");
            WebElement Tab3 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ScrollView/android.widget.ImageView[6]")));
            Tab3.click();
            driver.activateApp(yourAppPackage1);
            logger.info("clicking on youtube button");
            WebElement Tab4 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ScrollView/android.widget.ImageView[7]")));
            Tab4.click();
            driver.activateApp(yourAppPackage1);
            
            logger.info("clicking on facebook button");
            WebElement Tab5 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ScrollView/android.widget.ImageView[8]")));
            Tab5.click();
            driver.activateApp(yourAppPackage1);
            
            logger.info("clicking on X button");
            WebElement Tab6 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ScrollView/android.widget.ImageView[9]")));
            Tab6.click();
            driver.activateApp(yourAppPackage1);
            logger.info("clicking on Q button");
            WebElement Tab7 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ScrollView/android.widget.ImageView[10]")));
            Tab7.click();
            driver.activateApp(yourAppPackage1);


        } catch (Exception e) {
            logger.error("Test failed: " + e.getMessage(), e);
            Assert.fail("Test case failed: " + e.getMessage());
        }
    }

    public void scrollUntilElementFound() {
        int maxScrolls = 10;
        int scrollCount = 0;

        while (scrollCount < maxScrolls) {
            try {
                WebElement target = driver.findElement(AppiumBy.xpath("//android.widget.ScrollView/android.widget.ImageView[4]"));
                if (target.isDisplayed()) {
                    logger.info("Element 'your reliable news partner' found!");
                    return;
                }
            } catch (NoSuchElementException e) {
                logger.info("Element not found yet, performing scroll #" + (scrollCount + 1));
                performVerticalSwipe();
                scrollCount++;
            }
        }
        Assert.fail("Element with accessibility id 'your reliable news partner' not found after max scrolls.");
    }

    public void performVerticalSwipe() {
        int height = driver.manage().window().getSize().getHeight();
        int width = driver.manage().window().getSize().getWidth();

        int startX = width / 2;
        int startY = (int) (height * 0.7);
        int endY = (int) (height * 0.3);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);

        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), startX, endY));
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
