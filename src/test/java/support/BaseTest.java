package support;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Base class for test setup and teardown
 * Manages Selenium WebDriver initialization and cleanup
 */
public class BaseTest {

    protected static WebDriver driver;
    private static final Path SCREENSHOTS_DIR = Paths.get("target", "screenshots");

    static {
        // Create screenshots directory on class load
        try {
            Files.createDirectories(SCREENSHOTS_DIR);
        } catch (IOException e) {
            System.err.println("⚠️ Warning: Could not create screenshots directory: " + e.getMessage());
        }
    }

    /**
     * Setup Selenium WebDriver with Chrome browser
     * Uses DriverFactory for environment-specific configuration
     */
    public static void setupDriver() {
        driver = DriverFactory.createDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }

    /**
     * Take screenshot on test failure
     * @param testName Name of the failed test
     * @return Path to the screenshot file, or null if capture failed
     */
    public static String takeScreenshot(String testName) {
        if (driver == null) {
            System.err.println("⚠️ Cannot take screenshot: WebDriver is null");
            return null;
        }

        try {
            // Create timestamp for unique filename
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            // Sanitize test name for filename (remove special characters)
            String sanitizedTestName = testName.replaceAll("[^a-zA-Z0-9._-]", "_");

            // Generate filename
            String filename = String.format("%s_%s.png", sanitizedTestName, timestamp);
            Path screenshotPath = SCREENSHOTS_DIR.resolve(filename);

            // Capture screenshot
            TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
            File screenshot = screenshotDriver.getScreenshotAs(OutputType.FILE);

            // Copy to target location
            Files.copy(screenshot.toPath(), screenshotPath, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("📸 Screenshot captured: " + screenshotPath.toAbsolutePath());
            return screenshotPath.toAbsolutePath().toString();

        } catch (Exception e) {
            System.err.println("⚠️ Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }

    /**
     * Teardown - quit driver
     */
    public static void teardownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}
