package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for Checkout Complete page
 */
public class CheckoutCompletePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By thankYouHeader = By.cssSelector("h2[data-test='complete-header']");
    private final By backHomeButton = By.cssSelector("button[data-test='back-to-products']");

    public CheckoutCompletePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public boolean isThankYouMessageVisible() {
        try {
            WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(thankYouHeader));
            boolean isVisible = header.isDisplayed() && header.getText().equals("Thank you for your order!");
            System.out.println("Thank you message visible: " + isVisible);
            return isVisible;
        } catch (Exception e) {
            System.err.println("Thank you message not visible: " + e.getMessage());
            return false;
        }
    }

    public void clickBackHome() {
        try {
            WebElement button = wait.until(ExpectedConditions.elementToBeClickable(backHomeButton));
            button.click();
            System.out.println("Clicked Back Home button");
        } catch (Exception e) {
            System.err.println("Error clicking back home: " + e.getMessage());
            throw new RuntimeException("Failed to click back home", e);
        }
    }
}

