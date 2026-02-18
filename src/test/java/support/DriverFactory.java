package support;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Factory class for creating and configuring ChromeDriver instances
 * Handles both local and CI/CD environments (GitHub Actions)
 */
public class DriverFactory {

    public static WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();

        // ESSENTIAL for GitHub Actions (environment without graphical interface)
        String ciEnvironment = System.getenv("CI");
        if (ciEnvironment != null && "true".equals(ciEnvironment)) {
            System.out.println("CI environment detected - Running Chrome in headless mode");
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
        } else {
            System.out.println("Local environment detected - Running Chrome in normal mode");
            options.addArguments("--start-maximized");
        }

        // Optional flags (work in both environments)
        options.addArguments("--incognito");

        // Detailed logs for debugging (optional)
        System.setProperty("webdriver.chrome.verboseLogging", "true");
        System.setProperty("webdriver.chrome.logfile", "target/chromedriver.log");

        return new ChromeDriver(options);
    }
}

