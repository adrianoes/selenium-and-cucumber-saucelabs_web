package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object for Cart page
 */
public class CartPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By cartItems = By.cssSelector("div[data-test='inventory-item']");
    private final By cartItemName = By.cssSelector("div[data-test='inventory-item-name']");
    private final By cartItemPrice = By.cssSelector("div[data-test='inventory-item-price']");
    private final By removeButtons = By.cssSelector("button[data-test^='remove-']");
    private final By checkoutButton = By.cssSelector("button[data-test='checkout']");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public boolean isProductInCart(String expectedName, String expectedPrice) {
        try {
            List<WebElement> items = driver.findElements(cartItems);
            for (WebElement item : items) {
                String name = item.findElement(cartItemName).getText();
                String price = item.findElement(cartItemPrice).getText();
                if (name.equals(expectedName) && price.equals(expectedPrice)) {
                    System.out.println("Product verified in cart - Name: " + name + ", Price: " + price);
                    return true;
                }
            }
            System.err.println("Product not found in cart. Expected: " + expectedName + " " + expectedPrice);
            return false;
        } catch (Exception e) {
            System.err.println("Error verifying product in cart: " + e.getMessage());
            return false;
        }
    }

    public void clickCheckout() {
        try {
            WebElement button = wait.until(ExpectedConditions.elementToBeClickable(checkoutButton));
            button.click();
            System.out.println("Clicked Checkout button");
        } catch (Exception e) {
            System.err.println("Error clicking checkout: " + e.getMessage());
            throw new RuntimeException("Failed to click checkout", e);
        }
    }

    public void removeAllItems() {
        try {
            List<WebElement> buttons = driver.findElements(removeButtons);
            for (WebElement button : buttons) {
                button.click();
            }
            System.out.println("Removed all items from cart");
        } catch (Exception e) {
            System.err.println("Error removing items from cart: " + e.getMessage());
            throw new RuntimeException("Failed to remove items from cart", e);
        }
    }
}

