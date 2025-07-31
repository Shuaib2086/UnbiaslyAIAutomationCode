package AutoTesting.AppiumT; 

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class b extends AppTest {
    private static final Logger logger = LogManager.getLogger(b.class);
    public WebDriverWait wait;

    @BeforeClass
    public void setUp() throws MalformedURLException {
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        logger.info("Driver and WebDriverWait initialized");
    }

    @Test
    public void extractPrivacyPolicyText() {
        logger.info("Starting extractPrivacyPolicyText test");

        try {
            // Robust profile tab click logic
            WebElement profileTab = null;
            for (int i = 0; i < 5; i++) {
                try {
                    profileTab = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//*[contains(@content-desc, 'Profile') or contains(@text, 'Profile')]")));
                    profileTab.click();
                    break;
                } catch (Exception e) {
                    logger.warn("Retrying to find Profile tab (attempt {}): {}", i + 1, e.getMessage());
                    sleep(2000);
                }
            }
            Assert.assertNotNull(profileTab, "Profile tab not found after retries");
            sleep(1000);
            saveScreenshot("after_profile_tab.png");

            // Click Privacy Policy
            WebElement privacyPolicyLink;
            try {
                privacyPolicyLink = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@content-desc='Privacy Policy'] | //*[contains(@text, 'Privacy Policy')] | //android.widget.ImageView[@content-desc='Privacy Policy']")));
            } catch (Exception e) {
                logger.warn("Privacy Policy link not found, scrolling: {}", e.getMessage());
                scrollToElement("Privacy Policy");
                privacyPolicyLink = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@content-desc='Privacy Policy'] | //*[contains(@text, 'Privacy Policy')] | //android.widget.ImageView[@content-desc='Privacy Policy']")));
            }
            privacyPolicyLink.click();
            sleep(2000);
            saveScreenshot("after_privacy_policy_click.png");

            // Enhanced content extraction and scrolling
            StringBuilder privacyPolicyText = new StringBuilder();
            Set<String> seenLines = new HashSet<>();
            int maxScrollAttempts = 100; // Increased to handle long policies
            int scrollAttempts = 0;
            boolean atEndOfScroll = false;

            while (!atEndOfScroll && scrollAttempts < maxScrollAttempts) {
                // Log page source for debugging
                logger.debug("Page source after scroll {}:\n{}", scrollAttempts, driver.getPageSource());

                // Capture text from all elements with text or content-desc
                List<WebElement> allElements = driver.findElements(By.xpath("//*[contains(@text, '') or @content-desc]"));
                boolean newTextFound = false;
                for (WebElement element : allElements) {
                    String contentDesc = element.getAttribute("contentDescription");
                    String text = element.getAttribute("text");
                    String elementText = (contentDesc != null && !contentDesc.trim().isEmpty()) ? contentDesc : text;
                    if (elementText != null && !elementText.trim().isEmpty() && !seenLines.contains(elementText)) {
                        seenLines.add(elementText);
                        privacyPolicyText.append(elementText).append("\n");
                        logger.info("Added element text: {}", elementText);
                        newTextFound = true;
                    }
                }

                // Wait for new content to load
                try {
                    wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//android.view.View[@content-desc] | //android.widget.TextView")));
                } catch (Exception e) {
                    logger.warn("No new elements found after scroll {}: {}", scrollAttempts, e.getMessage());
                }

                // Scroll to end and check for end of content
                String scrollCommand = "new UiScrollable(new UiSelector().scrollable(true)).scrollToEnd(1)";
                try {
                    driver.findElement(AppiumBy.androidUIAutomator(scrollCommand));
                    scrollAttempts++;
                    sleep(2000); // Increased for dynamic content loading
                    saveScreenshot("scroll_" + scrollAttempts + ".png");
                    logger.info("Completed scroll attempt {}", scrollAttempts);
                } catch (Exception e) {
                    logger.info("Reached end of scrollable content or scroll failed after {} attempts: {}", scrollAttempts, e.getMessage());
                    atEndOfScroll = true;
                }

                // Check for "Load More" button
                try {
                    WebElement loadMoreButton = driver.findElement(By.xpath("//*[contains(@text, 'Load More') or contains(@content-desc, 'Load More')]"));
                    if (loadMoreButton.isDisplayed()) {
                        loadMoreButton.click();
                        sleep(2000);
                        logger.info("Clicked Load More button");
                    }
                } catch (Exception e) {
                    // No Load More button found, continue
                }

                // Additional check: Stop if no new text is found for 3 consecutive scrolls
                if (!newTextFound && scrollAttempts >= 3) {
                    try {
                        driver.findElement(AppiumBy.androidUIAutomator(scrollCommand));
                        scrollAttempts++;
                        sleep(2000);
                        saveScreenshot("scroll_" + scrollAttempts + ".png");
                        List<WebElement> newElements = driver.findElements(By.xpath("//*[contains(@text, '') or @content-desc]"));
                        boolean foundNew = false;
                        for (WebElement element : newElements) {
                            String elementText = element.getAttribute("contentDescription") != null ?
                                    element.getAttribute("contentDescription") : element.getAttribute("text");
                            if (elementText != null && !elementText.trim().isEmpty() && !seenLines.contains(elementText)) {
                                foundNew = true;
                                break;
                            }
                        }
                        if (!foundNew) {
                            logger.info("No new text found after 3 scrolls, stopping at attempt {}", scrollAttempts);
                            atEndOfScroll = true;
                        }
                    } catch (Exception e) {
                        logger.info("Final scroll check failed, stopping: {}", e.getMessage());
                        atEndOfScroll = true;
                    }
                }
            }

            String finalText = privacyPolicyText.toString().trim();
            logger.info("Extracted Privacy Policy Text ({} characters):\n{}", finalText.length(), finalText);
            Files.writeString(Paths.get("privacy_policy.txt"), finalText, StandardCharsets.UTF_8);
            logger.info("Privacy policy text saved to privacy_policy.txt");
            Assert.assertFalse(finalText.isEmpty(), "Privacy policy text is empty");

        } catch (Exception e) {
            logger.error("Error during privacy policy extraction: {}", e.getMessage());
            saveScreenshot("failure_screenshot.png");
            Assert.fail("Failed to extract privacy policy text: " + e.getMessage());
        }
    }

    private void scrollToElement(String text) {
        logger.info("Scrolling to find element with text: {}", text);
        try {
            driver.findElement(AppiumBy.androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(" +
                            "new UiSelector().textContains(\"" + text + "\"))"));
            sleep(1000);
        } catch (Exception e) {
            logger.warn("Scrolling to element failed: {}", e.getMessage());
        }
    }

    private void saveScreenshot(String fileName) {
        try {
            Files.write(Paths.get(fileName), ((AndroidDriver) driver).getScreenshotAs(OutputType.BYTES));
            logger.info("Screenshot saved to {}", fileName);
        } catch (Exception e) {
            logger.warn("Failed to capture screenshot {}: {}", fileName, e.getMessage());
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