package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for Checkout Overview page
 */
public class CheckoutOverviewPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By overviewItemName = By.cssSelector("div[data-test='inventory-item-name']");
    private final By overviewItemPrice = By.cssSelector("div[data-test='inventory-item-price']");
    private final By finishButton = By.cssSelector("button[data-test='finish']");

    public CheckoutOverviewPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public boolean isProductInOverview(String expectedName, String expectedPrice) {
        try {
            String nameInOverview = driver.findElement(overviewItemName).getText();
            String priceInOverview = driver.findElement(overviewItemPrice).getText();
            boolean matches = nameInOverview.equals(expectedName) && priceInOverview.equals(expectedPrice);

            System.out.println("Product in overview - Name: " + nameInOverview + ", Price: " + priceInOverview);
            System.out.println("Product matches: " + matches);
            return matches;
        } catch (Exception e) {
            System.err.println("Error verifying product in overview: " + e.getMessage());
            return false;
        }
    }

    public void clickFinish() {
        try {
            WebElement button = wait.until(ExpectedConditions.elementToBeClickable(finishButton));
            button.click();
            System.out.println("Clicked Finish button");
        } catch (Exception e) {
            System.err.println("Error clicking finish: " + e.getMessage());
            throw new RuntimeException("Failed to click finish", e);
        }
    }
}

