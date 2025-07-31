package AutoTesting.AppiumT;

import java.net.MalformedURLException;
import java.time.Duration;
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

public class scrolling extends AppTest {
    private static final Logger logger = LogManager.getLogger(scrolling.class);
    public WebDriverWait wait;

    @BeforeClass
    public void setUp() throws MalformedURLException {
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        logger.info("Driver and WebDriverWait initialized");
    }

    @Test
    public void feedscroll() {
        logger.info("Starting feedscroll test");

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

            logger.info("2nd screen clicking Sign-In button");
            WebElement SignInButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.view.View[@content-desc=\"Sign-In\"]")));
            SignInButton.click();

            logger.info("Clicking Google Sign-In button");
            WebElement googleSignInButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ImageView[@content-desc='Sign in with Google']")));
            googleSignInButton.click();

            logger.info("Selecting Google account");
            WebElement accountOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.TextView[@resource-id='com.google.android.gms:id/account_display_name']")));
            accountOption.click();
            Thread.sleep(4000);

            logger.info("Scrolling down Feed tab 10 times and bookmarking card after each scroll");

            int startX = driver.manage().window().getSize().getWidth() / 2;
            int startY = (int) (driver.manage().window().getSize().getHeight() * 0.8);
            int endY = (int) (driver.manage().window().getSize().getHeight() * 0.2);

            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");

            for (int i = 0; i < 10; i++) {
                try {
                    logger.info("Scrolling iteration " + (i + 1));
                    Sequence scroll = new Sequence(finger, 1)
                            .addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), startX, startY))
                            .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                            .addAction(finger.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), startX, endY))
                            .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

                    driver.perform(List.of(scroll));
                    logger.info("Performed scroll " + (i + 1));
                    Thread.sleep(4000);

                    try {
                        logger.info("Attempting to bookmark after scroll " + (i + 1));
                        WebElement bookmarkButton = driver.findElement(By.xpath("//android.widget.ImageView[@content-desc='Bookmark']"));
                        bookmarkButton.click();
                        logger.info("Bookmarked card " + (i + 1));
                    } catch (Exception e) {
                        logger.warn("Bookmark button not found after scroll " + (i + 1));
                    }

                } catch (Exception e) {
                    logger.error("Failed during scroll " + (i + 1), e);
                }
            }

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
