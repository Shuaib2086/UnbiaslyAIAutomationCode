package AutoTesting.AppiumT;

import java.net.MalformedURLException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

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

public class PrivacyAndPolicy extends AppTest {
    private static final Logger logger = LogManager.getLogger(PrivacyAndPolicy.class);
    public WebDriverWait wait;

    @BeforeClass
    public void setUp() throws MalformedURLException {
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        logger.info("Driver and WebDriverWait initialized with 30-second timeout");
    }

    @Test
    public void testPrivacyPolicy() {
        logger.info("Starting Privacy & Policy test");

        try {
            logger.info("Selecting English language");
            WebElement englishButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ImageView[@content-desc='English']")));
            englishButton.click();
            sleep(2000);

            logger.info("Clicking Next button");
            WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.view.View[@content-desc='Next']")));
            nextButton.click();

            logger.info("Verifying feed page");
            WebElement guestModeIndicator = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//android.view.View[@content-desc='Summarised by UnbiaslyAI']")));
            Assert.assertTrue(guestModeIndicator.isDisplayed(), "Feed page is not visible");

            logger.info("Navigating to Profile tab");
            WebElement profileTab = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.accessibilityId("Profile\nTab 4 of 4")));
            profileTab.click();

            logger.info("Clicking Privacy Policy link");
            WebElement privacyPolicyLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.ImageView[@content-desc=\"Privacy Policy\"]")));
            privacyPolicyLink.click();

            // Check for WebView context
            logger.info("Checking for WebView context");
            String originalContext = driver.getContext();
            boolean isWebView = false;
            Set<String> contexts = driver.getContextHandles();
            for (String context : contexts) {
                logger.info("Available context: " + context);
                if (context.contains("WEBVIEW")) {
                    logger.info("Switching to WebView context: " + context);
                    driver.context(context);
                    isWebView = true;
                    sleep(2000); // Wait for WebView content to load
                    break;
                }
            }

            logger.info("Verifying Privacy Policy content");
            WebElement privacyContent = null;
            try {
                privacyContent = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        AppiumBy.androidUIAutomator("new UiSelector().className(\"android.view.View\").instance(6)")));
                logger.info("Privacy Policy content located using UI Automator selector");
            } catch (Exception e) {
                logger.warn("UI Automator selector failed, trying fallback content-desc");
                try {
                    privacyContent = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//android.view.View[@content-desc[contains(., 'Privacy Policy')]]")));
                } catch (Exception e2) {
                    logger.warn("Fallback content-desc failed, trying text-based XPath");
                    privacyContent = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//android.widget.TextView[contains(@text, 'Privacy Policy')]")));
                }
            }
            Assert.assertNotNull(privacyContent, "Privacy Policy content element not found");
            Assert.assertTrue(privacyContent.isDisplayed(), "Privacy Policy content is not visible");

            // Scroll to load full content
            logger.info("Scrolling to ensure full content is loaded");
            try {
                driver.findElement(AppiumBy.androidUIAutomator(
                        "new UiScrollable(new UiSelector().scrollable(true)).scrollToEnd(10)"));
                sleep(1000); // Wait after scrolling
            } catch (Exception e) {
                logger.warn("Scrolling failed, content may already be visible");
            }

            logger.info("Extracting Privacy Policy content");
            String actualContent = null;
            String[] attributes = {"content-desc", "text", "name"};
            int retries = 3;
            for (int i = 0; i < retries; i++) {
                for (String attribute : attributes) {
                    try {
                        actualContent = privacyContent.getAttribute(attribute);
                        if (actualContent != null && !actualContent.trim().isEmpty()) {
                            logger.info("Content extracted from attribute: " + attribute);
                            break;
                        }
                    } catch (Exception e) {
                        logger.warn("Failed to extract content from attribute: " + attribute);
                    }
                }
                if (actualContent != null && !actualContent.trim().isEmpty()) break;
                sleep(1000); // Wait before retrying
            }

            // Aggregate text from all relevant elements
            if (actualContent == null || actualContent.trim().isEmpty()) {
                logger.info("Single element content empty, aggregating from all text elements");
                List<WebElement> textElements = isWebView ?
                        driver.findElements(By.xpath("//*[text()]")) :
                        driver.findElements(By.xpath("//android.widget.TextView|.//android.view.View"));
                StringBuilder aggregatedContent = new StringBuilder();
                for (WebElement element : textElements) {
                    String text = null;
                    for (String attr : attributes) {
                        try {
                            text = element.getAttribute(attr);
                            if (text != null && !text.trim().isEmpty()) break;
                        } catch (Exception ignored) {}
                    }
                    if (text != null && !text.trim().isEmpty()) {
                        aggregatedContent.append(text).append(" ");
                    }
                }
                actualContent = aggregatedContent.toString().trim();
                logger.info("Aggregated content: \n" + actualContent);
            }

            // Debug: Log element attributes if content is empty
            if (actualContent == null || actualContent.trim().isEmpty()) {
                logger.error("Unable to extract content. Dumping primary element details:");
                for (String attribute : attributes) {
                    try {
                        String value = privacyContent.getAttribute(attribute);
                        logger.info("Attribute " + attribute + ": " + (value != null ? value : "null"));
                    } catch (Exception e) {
                        logger.info("Attribute " + attribute + ": not available");
                    }
                }
                Assert.fail("Unable to extract Privacy Policy content");
            }
            actualContent = actualContent.trim();
            logger.info("Actual Privacy Policy content extracted: \n" + actualContent);

            // Validate date format
            String dateRegex = "\\d{1,2}(st|nd|rd|th)?\\s*[A-Za-z]+\\s*\\d{4}|\\d{1,2}/\\d{1,2}/\\d{4}|[A-Za-z]+\\s*\\d{1,2}\\s*\\d{4}|\\d{1,2}\\s*[A-Za-z]+";
            if (!actualContent.contains("updated on") && !actualContent.contains("Last updated")) {
                logger.warn("No 'updated on' or 'Last updated' phrase found, skipping date validation");
            } else if (!Pattern.compile(dateRegex).matcher(actualContent).find()) {
                logger.error("Date format invalid. Expected formats: '12th March 2025', '12/03/2025', 'March 12 2025', or '12th March'");
                logger.error("Actual content: \n" + actualContent);
                Assert.fail("Invalid date format in Privacy Policy content");
            }

            // Normalize content for comparison
            String normalizedActualContent = actualContent.replaceAll("[^a-zA-Z0-9\\s]", "").replaceAll("\\s+", " ");
            String datePlaceholder = Pattern.compile(dateRegex).matcher(normalizedActualContent).replaceAll("DATE");
            datePlaceholder = datePlaceholder.replace("CDATE", "DATE"); // Handle CDATE placeholder

            // Expected Privacy Policy content
            String expectedContent = """
                Privacy Policy
                Last updated: 12th March
                Our Privacy Policy was last updated on DATE. This Privacy Policy describes Our policies and procedures on the collection use and disclosure of Your information when You use the Service and tells You about Your privacy rights and how the law protects You. We use Your Personal data to provide and improve the Service. By using the Service You agree to the collection and use of information in accordance with this Privacy Policy.
                Interpretation and Definitions
                The words of which the initial letter is capitalized have meanings defined under the following conditions. The following definitions shall have the same meaning regardless of whether they appear in singular or in plural.
                Definitions
                For the purposes of this Privacy Policy:
                Account means a unique account created for You to access our Service or parts of our Service.
                Application means the software program provided by the Company downloaded by You on any electronic device named UnbiaslyAI.
                Company (referred to as either the Company We Us or Our in this Agreement) refers to Triverge Insight Private Limited.
                Country refers to India.
                Cookies are small files that are placed on Your computer mobile device or any other device by a website containing the details of Your browsing history on that website among its many uses.
                Device means any device that can access the Service such as a computer a cell phone or a digital tablet.
                Personal Data is any information that relates to an identified or identifiable individual.
                Service refers to the App and the Website
                Service Provider means any natural or legal person who processes the data on behalf of the Company. It refers to third-party companies or individuals employed by the Company to facilitate the Service to provide the Service on behalf of the Company to perform services related to the Service or to assist the Company in analyzing how the Service is used.
                Usage Data refers to data collected automatically either generated by the use of the Service or from the Service infrastructure itself.
                You means the individual accessing or using the Service or the company or other legal entity on behalf of which such individual is accessing or using the Service as applicable.
                Collection of Personal Information
                Personal information encompasses data that can be used to identify an individual including but not limited to their name and email address. As far as sensitive personal information is concerned it will carry the same definition and meaning within the applicable law from time to time.
                We collect various types of personal data to provide and improve our service. The types of personal data we may collect include but are not limited to:
                Name
                Email address
                Its important to note that the collection and processing of this personal data are based on legal grounds that are compliant with Indian data protection laws. The legal basis for processing personal information under Indian law is primarily governed by the Personal Data Protection Act and any other applicable Indian data protection laws. We collect and process personal data based on one or more legal grounds including but not limited to the necessity of processing for the performance of a contract compliance with a legal obligation protection of vital interests consent the performance of a task carried out in the public interest or the legitimate interests pursued by us or a third party. This ensures that your personal information is processed in accordance with the law while providing transparency and clarity about the legal basis for such processing.
                Our App may include links to third-party websites or applications. Whether a link is present or not does not mean that We endorse the website its provider or the information it contains. You agree and acknowledge that We have no control over the privacy policies of these external websites. We cannot vouch for the privacy practices terms of use accuracy integrity or quality of the content on such websites. Once You leave Our servers and use information on these external sites their privacy policy governs how Your data is handled. We advise users to review the privacy policies of each external website understanding that the responsibility for Your data lies with the third party operating the site.
                Use of Your Personal Data
                The Company may use Personal Data for the following purposes:
                To provide and maintain Our Service which includes monitoring how the Service is used.
                Managing Your Account: This involves overseeing your registration as a user of the Service. The Personal Data You provide allows you to access various features and functionalities available to registered users.
                Performance of a contract: This includes activities related to the creation fulfillment and management of purchase contracts for services youve bought or any other agreement with us via the Service.
                Contacting you: We may contact you via email or similar electronic communication methods like push notifications on a mobile app for updates or informational messages about the features or services youve contracted with us. This also encompasses essential security updates when needed or sensible for their implementation.
                Handling your inquiries and requests making sure we address and manage them efficiently.
                Business transfers: Your data may be used when assessing or carrying out actions like mergers divestitures restructures dissolutions or the sale or transfer of some or all of our assets whether as part of a business continuation or as part of bankruptcy liquidation or similar proceedings. In such cases the Personal Data of our Service users could be part of the assets transferred.
                Other purposes: We might use your data for various other objectives such as conducting data analysis recognizing usage patterns assessing the effectiveness of our promotional campaigns and enhancing our Service marketing and your overall user experience.
                Sharing of Your Personal Data
                We understand the importance of your privacy and we are committed to safeguarding your data. While we collect personal information for various purposes as outlined in our Collection of Personal Information section we want to be transparent about how and when your personal data may be shared. It is pertinent that you review this section very carefully.
                With Service Providers: We may disclose your personal data to trusted service providers who assist us in delivering our service. These service providers may include hosting providers customer support services or analytics providers. We ensure that these service providers adhere to strict data protection standards and only process your data for the purposes specified by us.
                For Business Transfers: In the event of a merger acquisition or asset sale your personal data may be transferred as part of the transaction. We will notify you in advance of such a transfer and your data will remain subject to the same level of protection as outlined in this policy.
                With Affiliates: Your data may be shared with our affiliates including our parent company and subsidiaries joint venture partners or entities under our control or common control. We ensure that these affiliates maintain the same level of data protection and adhere to this privacy policy.
                With Business Partners: We may share your information with trusted business partners to provide you with specific products services or promotional offers. Such sharing will be conducted with your consent or as otherwise permitted by applicable data protection laws.
                With Your Consent: We may disclose your personal information for any other purpose with your explicit consent.
                WE DO NOT SELL OR RENT YOUR PERSONAL DATA TO THIRD PARTIES AND WE TAKE ALL NECESSARY PRECAUTIONS TO PROTECT YOUR INFORMATION IN ACCORDANCE WITH APPLICABLE DATA PROTECTION LAWS AND INDUSTRY STANDARDS.
                By sharing your personal data as described above we ensure that we can provide you with the best possible service and experience. If you have any questions or concerns about how your personal data is shared please dont hesitate to contact us using the information provided in the Queries and Complaints section of this policy.
                Retention of Data
                How long are we going to retain the data?
                Our app may send your personal information to our internal servers. The retention of this personal information on our servers is subject to the following principles:
                We will retain your personal data for a maximum of 180 days after you delete the app or cancel your user account unless we have a legitimate need or legal obligation to keep the data for a longer period as required by applicable laws.
                Beyond the 180-day retention period we will retain your personal data only as long as it is necessary to fulfill the specific purposes outlined in this Privacy Policy. Once the data is no longer needed for these purposes it will be securely and permanently deleted.
                Disclosure of Your Personal Data
                In the following scenarios we may need to handle your Personal Data:
                Business Transactions
                If the Company is part of a merger acquisition or asset sale your Personal Data could be transferred. We will notify you before such transfer occurs and it becomes subject to a different Privacy Policy.
                Law Enforcement: In certain situations the Company might have to disclose your Personal Data as required by law or in response to legitimate requests from public authorities such as a court or government agency.
                Other Legal Requirements
                The Company may reveal your Personal Data in good faith when its necessary to:
                Comply with a legal obligation.
                Safeguard and defend the rights or property of the Company.
                Prevent or investigate potential misconduct related to the Service.
                Ensure the personal safety of Service Users or the general public.
                Safeguard against legal liabilities.
                Security
                We prioritize the security of your personal information and employ a range of security measures to safeguard your data from unauthorized access alteration disclosure loss misuse or destruction. These measures encompass physical managerial operational and technical safeguards tailored to the type and sensitivity of the data we collect. We are using same security measures as mentioned here under:
                Data Encryption
                We use industry-standard encryption protocols to protect your data during transmission. This ensures that any data transferred between your device and our servers remains confidential and secure.
                Access Control
                Access to your personal data is restricted to authorized personnel who require access for legitimate business purposes. We implement strict access controls and authentication mechanisms to prevent unauthorized access.
                Regular Security Assessments
                We conduct regular security assessments including vulnerability scanning and penetration testing to identify and address potential security vulnerabilities. This proactive approach helps us ensure the ongoing security of our systems.
                Employee Training
                Our employees undergo training on data protection and security best practices. They are educated on the importance of safeguarding personal data and are required to adhere to our strict security policies.
                Incident Response
                In the event of a security breach or data incident we have established incident response procedures to swiftly address and mitigate any potential harm. We will notify affected users as required by law and take necessary actions to rectify the situation.
                While we take diligent efforts to maintain a secure environment for your personal information its important to recognize that complete security is never absolute. We cannot guarantee that unintended disclosures of your personal information will never occur. If we become aware of any such unauthorized disclosure we will make reasonable efforts to promptly inform you about its nature and extent to the extent of our knowledge and as permitted by the law. Its vital that you do not share your contact information with any third party.
                Changes to This Privacy Policy
                We reserve the right to periodically update our Privacy Policy. Any modifications will be communicated by publishing the revised Privacy Policy on this page.
                Prior to the changes taking effect we will provide notification via email and/or a noticeable notice on our Service. The Last updated date at the top of this Privacy Policy will also be revised accordingly.
                We encourage you to periodically review this Privacy Policy for any alterations. Changes to this Privacy Policy are considered effective once they are posted on this page.
                Limitation of Liability
                We do not make any claims promises or guarantees regarding the accuracy completeness or sufficiency of the content accessible through the App. We explicitly disclaim any liability for errors and omissions in the Apps content.
                We do not provide any warranties of any kind whether implied expressed or statutory. This includes but is not limited to warranties of non-infringement of third-party rights title merchantability fitness for a particular purpose and freedom from computer viruses concerning the content available through the App or its links to other internet resources.
                Any references in the App to specific commercial products processes services or the use of trade names company names or corporation names are provided for public information and convenience. They do not imply endorsement recommendation or favoritism.
                Your Rights Regarding Your Personal Information
                We respect your rights and are committed to ensuring that you have control over your personal information. As a user you have the following rights:
                Review and Correction: You have the right to review your personal information and request corrections if you discover inaccuracies or incomplete data. Please contact us at contact@unbiasly.ai for assistance.
                Withdraw Consent: You have the right to withdraw your consent for the processing of your personal data. If you wish to exercise this right please contact us at contact@unbiasly.ai. Please note that in some cases your information may be necessary for us to provide our services. If you choose not to provide certain information or withdraw your consent to process previously provided information we may have to restrict or deny some of our services.
                Queries and Complaints
                If you have any inquiries or complaints related to the protection of your personal information or privacy concerns we are dedicated to safeguarding your data. For any feedback or concerns you can get in touch with us:
                Address: C-11 Basement Green Park Extension Delhi India 110016
                Email: contact@unbiasly.ai
                """.trim().replaceAll("[^a-zA-Z0-9\\s]", "").replaceAll("\\s+", " ");

            expectedContent = expectedContent.replace("DATE", "DATE");

            // Perform word-by-word comparison
            List<String> actualWords = Arrays.asList(datePlaceholder.split("\\s+"));
            List<String> expectedWords = Arrays.asList(expectedContent.split("\\s+"));

            boolean mismatchFound = false;
            int minLength = Math.min(actualWords.size(), expectedWords.size());

            logger.info("Starting word-by-word comparison");
            for (int i = 0; i < minLength; i++) {
                String actualWord = actualWords.get(i);
                String expectedWord = expectedWords.get(i);
                if (!actualWord.equalsIgnoreCase(expectedWord)) {
                    mismatchFound = true;
                    logger.error("Word mismatch at position " + (i + 1) + ": Expected '" + expectedWord + "', Actual '" + actualWord + "'");
                }
            }

            // Check for length mismatch
            if (actualWords.size() != expectedWords.size()) {
                mismatchFound = true;
                logger.error("Length mismatch detected!");
                logger.error("Expected word count: " + expectedWords.size());
                logger.error("Actual word count: " + actualWords.size());
                if (actualWords.size() > expectedWords.size()) {
                    logger.error("Extra words in actual content: " + actualWords.subList(minLength, actualWords.size()));
                } else {
                    logger.error("Missing words in actual content: " + expectedWords.subList(minLength, expectedWords.size()));
                }
            }

            if (mismatchFound) {
                logger.error("Full Expected Content (normalized):\n" + expectedContent);
                logger.error("Full Actual Content (normalized):\n" + datePlaceholder);
                Assert.fail("Privacy Policy content does not match expected content word by word. See logs for details.");
            } else {
                logger.info("Privacy Policy content matched exactly word by word.");
            }

            // Switch back to original context
            if (isWebView) {
                logger.info("Switching back to original context: " + originalContext);
                driver.context(originalContext);
            }

        } catch (Exception e) {
            logger.error("Privacy Policy test failed", e);
            Assert.fail("Test failed due to exception: " + e.getMessage());
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