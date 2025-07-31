package AutoTesting.AppiumT;

import java.net.MalformedURLException;
import java.time.Duration;
import java.util.List;

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


public class sourceButton extends AppTest{
	private static final Logger logger = LogManager.getLogger(sourceButton.class);
    public WebDriverWait wait;
	
	
	@BeforeClass
	public void setUp() throws MalformedURLException {
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        logger.info("Driver and WebDriverWait initialized");
        
    }
	
	@Test
	public void source() {
		logger.info("Starting NameDisplay test");

        logger.info("Selecting English language");
        WebElement englishButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//android.widget.ImageView[@content-desc=\"English\"]")));
        englishButton.click();
        sleep(3000);

        logger.info("Clicking Next button");
        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//android.view.View[@content-desc=\"Next\"]")));
        nextButton.click();
        try {
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
            Thread.sleep(3000);
            
     

            // Use XPath with contains()
            By dynamicElement = By.xpath("//android.view.View[contains(@content-desc, 'Based on a statement by')]");

            List<WebElement> elements = driver.findElements(dynamicElement);

            if (elements.size() > 0) {
                for (WebElement element : elements) {
                    if (element.isDisplayed()) {
                        System.out.println("Visible element found: " + element.getAttribute("content-desc"));
                    } else {
                        System.out.println("Element found but not visible.");
                    }
                }
            } else {
                System.out.println("No elements found with matching content-desc.");
            }

            // Use XPath with contains()
           
           

            

        }catch (Exception e) {
            logger.error("Test failed: " + e.getMessage(), e);
            Assert.fail("Test case failed: " + e.getMessage());
        }
        
        
        
	}
	private void sleep() {
		// TODO Auto-generated method stub
		
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
