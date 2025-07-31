package AutoTesting.AppiumT;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.appium.java_client.AppiumBy;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class unbiasly1 extends AppTest {

    private static final Logger logger = LoggerFactory.getLogger(unbiasly1.class);

    @Test
    public void open() throws InterruptedException, TimeoutException {
        try {
            driver.findElement(By.xpath("//android.widget.ImageView[@content-desc=\"English\"]")).click();
            driver.findElement(AppiumBy.accessibilityId("Next")).click();
            Thread.sleep(2000);

            // 5 seconds wait for Discover tab
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement discoverTab = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//android.widget.ImageView[contains(@content-desc, 'Discover')]")
            ));
            if (discoverTab != null) {
                discoverTab.click();
                logger.info("Discover tab opened successfully within 5 seconds.");
            } else {
                logger.error("Discover tab did not open within 5 seconds.");
                Assert.fail("Discover tab not opened within 5 seconds.");
            }

            Thread.sleep(3000);

            driver.findElement(By.xpath("//android.widget.ImageView[@content-desc=\"Select All\"]")).click();
            driver.findElement(By.xpath("//android.view.View[@content-desc=\"Update\"]")).click();
            Thread.sleep(2000);

            // Again 5 seconds wait
            WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement discoverTab1 = wait1.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//android.widget.ImageView[contains(@content-desc, 'Discover')]")
            ));
            if (discoverTab1 != null) {
                discoverTab1.click();
                logger.info("Discover tab re-opened successfully within 5 seconds.");
            } else {
                logger.error("Discover tab did not re-open within 5 seconds.");
                Assert.fail("Discover tab not re-opened within 5 seconds.");
            }

            Thread.sleep(3000);
            driver.findElement(By.xpath("//android.widget.ImageView[@content-desc=\"Select All\"]")).click();
            Thread.sleep(4000);

            driver.findElement(AppiumBy.accessibilityId("Politics")).click();
            Thread.sleep(2000);
            driver.findElement(AppiumBy.accessibilityId("Top News")).click();
            Thread.sleep(1000);
            driver.findElement(AppiumBy.accessibilityId("World News")).click();
            Thread.sleep(2000);
            driver.findElement(AppiumBy.accessibilityId("Business")).click();
            driver.findElement(By.xpath("//android.view.View[@content-desc=\"Update\"]")).click();
            Thread.sleep(2000);

            // Again 5 seconds wait
            WebDriverWait wait11 = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement discoverTab11 = wait11.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//android.widget.ImageView[contains(@content-desc, 'Discover')]")
            ));
            if (discoverTab11 != null) {
                discoverTab11.click();
                logger.info("Discover tab opened again successfully within 5 seconds.");
            } else {
                logger.error("Discover tab not opened again within 5 seconds.");
                Assert.fail("Discover tab not opened again within 5 seconds.");
            }

            Thread.sleep(3000);

            driver.findElement(By.xpath("//android.widget.EditText")).click();
            Thread.sleep(4000);

            WebElement searchField = driver.findElement(By.xpath("//android.widget.EditText"));
            searchField.sendKeys("Crime");

            WebDriverWait wait111 = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement result = wait111.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(@content-desc, 'Crime')]")
            ));
            if (result != null && result.isDisplayed()) {
                result.click();
                logger.info("Clicked on 'Crime' result within 5 seconds.");
            } else {
                logger.error("'Crime' result not found or not visible within 5 seconds.");
                Assert.fail("'Crime' result not found or not visible within 5 seconds.");
            }

            Thread.sleep(3000);
            driver.findElement(AppiumBy.accessibilityId("Update")).click();
            driver.findElement(By.xpath("//android.widget.ImageView[contains(@content-desc, 'Discover')]")).click();
            Thread.sleep(3000);

        } catch (Exception e) {
            logger.error("Error occurred during 'open' test execution: {}", e.getMessage(), e);
            throw e; // rethrow to fail the test
        }
    }
}
