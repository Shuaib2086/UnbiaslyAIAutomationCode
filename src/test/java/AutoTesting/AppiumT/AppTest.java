package AutoTesting.AppiumT;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.google.common.collect.ImmutableMap;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppTest {
    public AndroidDriver driver;
    public AppiumDriverLocalService service;
    private static final Logger logger = LogManager.getLogger(AppTest.class);

    @BeforeClass
    public void ConfigureAppium() throws MalformedURLException, URISyntaxException {
        logger.info("Starting Appium server locally...");
        service = new AppiumServiceBuilder()
                .withAppiumJS(new File("C:\\Users\\Shuaib.Malik\\AppData\\Roaming\\npm\\node_modules\\appium\\build\\lib\\main.js"))
                .withIPAddress("127.0.0.1")
                .usingPort(4723)
                .build();
        service.start();

        logger.info("Configuring UiAutomator2 options...");
        UiAutomator2Options options = new UiAutomator2Options();
        options.setDeviceName("ShuaibMobile");
        options.setApp("C:\\Users\\Shuaib.Malik\\eclipse-workspace\\Appium\\src\\test\\java\\resources\\Unbiasly_18April.apk");
        // options.setChromedriverExecutable("path/to/chromedriver") // Uncomment if WebView involved

        logger.info("Launching AndroidDriver...");
        driver = new AndroidDriver(new URI("http://127.0.0.1:4723").toURL(), options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));

        logger.info("Driver and Appium server started successfully.");
    }

    public void longPressAction(WebElement ele) {
        logger.debug("Performing long press on element");
        ((JavascriptExecutor) driver).executeScript("mobile: longClickGesture",
                ImmutableMap.of("elementId", ((RemoteWebElement) ele).getId(), "duration", 2000));
    }

    public void scrollToEndAction() {
        logger.debug("Scrolling to end of scrollable view");
        boolean canScrollMore;
        do {
            canScrollMore = (Boolean) ((JavascriptExecutor) driver).executeScript("mobile: scrollGesture", ImmutableMap.of(
                    "left", 100, "top", 100, "width", 200, "height", 200,
                    "direction", "down",
                    "percent", 3.0));
        } while (canScrollMore);
    }

    public void swipeAction(WebElement ele, String direction) {
        logger.debug("Swiping element in direction: " + direction);
        ((JavascriptExecutor) driver).executeScript("mobile: swipeGesture", ImmutableMap.of(
                "elementId", ((RemoteWebElement) ele).getId(),
                "direction", direction,
                "percent", 0.75
        ));
    }

    public Double getFormattedAmount(String amount) {
        logger.debug("Parsing amount string: " + amount);
        return Double.parseDouble(amount.replaceAll("[^0-9.]", ""));
    }

    public void testDarkModeScenario() {
        // Placeholder — currently unused.
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        logger.debug("testDarkModeScenario initialized");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            logger.info("Closing Appium driver");
            driver.quit();
        }
        if (service != null && service.isRunning()) {
            logger.info("Stopping Appium service");
            service.stop();
        }
    }
}
