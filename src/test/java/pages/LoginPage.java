package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

/**
 * Page Object Model for Login page
 * Centralizes all selectors and methods related to the login page
 */
public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Selectors - defined as constants (best practice)
    private By usernameField = By.id("user-name");
    private By passwordField = By.id("password");
    private By loginButton = By.id("login-button");
    private By errorMessageContainer = By.cssSelector(".error-message-container.error h3[data-test='error']");
    private By errorIcon = By.cssSelector(".error_icon");

    /**
     * Constructor
     * @param driver WebDriver instance
     */
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Navigation methods

    /**
     * Navigate to SauceDemo login page
     */
    public void navigateToLoginPage() {
        driver.get("https://www.saucedemo.com/");
        System.out.println("✓ Navigated to: https://www.saucedemo.com/");
    }

    // Input methods

    /**
     * Enter username in the username field
     * @param username username to enter
     */
    public void enterUsername(String username) {
        WebElement usernameElement = wait.until(ExpectedConditions.presenceOfElementLocated(
            usernameField
        ));
        usernameElement.clear();
        usernameElement.sendKeys(username);
        System.out.println("✓ Username entered: " + username);
    }

    /**
     * Enter password in the password field
     * @param password password to enter
     */
    public void enterPassword(String password) {
        WebElement passwordElement = wait.until(ExpectedConditions.presenceOfElementLocated(
            passwordField
        ));
        passwordElement.clear();
        passwordElement.sendKeys(password);
        System.out.println("✓ Password entered: (hidden for security)");
    }

    // Action methods

    /**
     * Click the Login button
     */
    public void clickLoginButton() {
        WebElement loginButtonElement = wait.until(ExpectedConditions.elementToBeClickable(
            driver.findElement(loginButton)
        ));
        loginButtonElement.click();
        System.out.println("✓ Login button clicked");
    }

    // Validation methods

    /**
     * Check if error message is displayed
     * @return true if error message is visible, false otherwise
     */
    public boolean isErrorMessageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(
                driver.findElement(errorMessageContainer)
            ));
            System.out.println("✓ Error message is displayed");
            return true;
        } catch (Exception e) {
            System.out.println("✗ Error message is NOT displayed");
            return false;
        }
    }

    /**
     * Get the error message text
     * @return error message text
     */
    public String getErrorMessage() {
        try {
            String errorText = wait.until(ExpectedConditions.visibilityOf(
                driver.findElement(errorMessageContainer)
            )).getText();
            System.out.println("✓ Error message captured: " + errorText);
            return errorText;
        } catch (Exception e) {
            System.out.println("✗ Error capturing message: " + e.getMessage());
            return "";
        }
    }

    /**
     * Check if error icons are visible
     * @return true if error icons are visible
     */
    public boolean areErrorIconsDisplayed() {
        try {
            int errorIconCount = driver.findElements(errorIcon).size();
            boolean hasErrors = errorIconCount > 0;
            System.out.println("✓ Error icons found: " + errorIconCount);
            return hasErrors;
        } catch (Exception e) {
            System.out.println("✗ Error validating icons: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if username field has error class
     * @return true if username field has error class
     */
    public boolean doesUsernameFieldHaveErrorClass() {
        WebElement element = driver.findElement(usernameField);
        String classes = element.getAttribute("class");
        boolean hasError = classes.contains("error");
        System.out.println("✓ Username field has error class: " + hasError);
        return hasError;
    }

    /**
     * Check if password field has error class
     * @return true if password field has error class
     */
    public boolean doesPasswordFieldHaveErrorClass() {
        WebElement element = driver.findElement(passwordField);
        String classes = element.getAttribute("class");
        boolean hasError = classes.contains("error");
        System.out.println("✓ Password field has error class: " + hasError);
        return hasError;
    }
}