package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object for Product Purchase flow
 * Contains all elements and actions for the complete purchase flow
 */
public class ProductPurchasePage {

    private WebDriver driver;
    private WebDriverWait wait;

    // Variables to store product information
    private String productName;
    private String productPrice;

    // Locators (Inventory page)
    private By productsTitle = By.xpath("//span[@data-test='title' and text()='Products']");
    private By sortDropdown = By.cssSelector("select[data-test='product-sort-container']");
    private By activeSortOption = By.cssSelector("span[data-test='active-option']");
    private By inventoryItems = By.cssSelector("div[data-test='inventory-item']");
    private By firstItemPrice = By.xpath("(//div[@data-test='inventory-item'])[1]//div[@data-test='inventory-item-price']");
    private By firstItemName = By.xpath("(//div[@data-test='inventory-item'])[1]//div[@data-test='inventory-item-name']");
    private By firstItemAddToCartButton = By.xpath("(//div[@data-test='inventory-item'])[1]//button[contains(@data-test, 'add-to-cart')]");
    private By shoppingCartBadge = By.cssSelector("span[data-test='shopping-cart-badge']");
    private By shoppingCartLink = By.cssSelector("a[data-test='shopping-cart-link']");
    private By menuButton = By.id("react-burger-menu-btn");
    private By logoutLink = By.id("logout_sidebar_link");

    public ProductPurchasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /**
     * Wait for page to stabilize after sorting or navigation changes
     * Ensures products list is fully loaded and stable
     */
    private void waitForPageStability() {
        try {
            // Wait for inventory list to be present
            WebDriverWait extendedWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            extendedWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//div[@data-test='inventory-item']")
            ));
            // Extra buffer for rendering
            Thread.sleep(500);
        } catch (Exception e) {
            System.out.println("Warning: Page stability wait timed out: " + e.getMessage());
        }
    }

    /**
     * Verify that Products title is visible
     */
    public boolean isProductsTitleVisible() {
        try {
            WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(productsTitle));
            boolean isVisible = title.isDisplayed();
            System.out.println("Products title visible: " + isVisible);
            return isVisible;
        } catch (Exception e) {
            System.err.println("Products title not visible: " + e.getMessage());
            return false;
        }
    }

    /**
     * Click on the sort dropdown
     */
    public void clickSortDropdown() {
        try {
            WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(sortDropdown));
            dropdown.click();
            System.out.println("Clicked on sort dropdown");
        } catch (Exception e) {
            System.err.println("Error clicking sort dropdown: " + e.getMessage());
            throw new RuntimeException("Failed to click sort dropdown", e);
        }
    }

    /**
     * Select "Price (low to high)" sorting option
     */
    public void selectPriceLowToHigh() {
        try {
            WebElement dropdown = driver.findElement(sortDropdown);
            Select select = new Select(dropdown);
            select.selectByValue("lohi");
            System.out.println("Selected 'Price (low to high)' sorting");
            waitForPageStability();
        } catch (Exception e) {
            System.err.println("Error selecting price low to high: " + e.getMessage());
            throw new RuntimeException("Failed to select sorting option", e);
        }
    }

    /**
     * Select "Price (high to low)" sorting option
     */
    public void selectPriceHighToLow() {
        try {
            WebElement dropdown = driver.findElement(sortDropdown);
            Select select = new Select(dropdown);
            select.selectByValue("hilo");
            System.out.println("Selected 'Price (high to low)' sorting");
            waitForPageStability();
        } catch (Exception e) {
            System.err.println("Error selecting price high to low: " + e.getMessage());
            throw new RuntimeException("Failed to select sorting option", e);
        }
    }

    public String getActiveSortOptionText() {
        try {
            WebElement active = wait.until(ExpectedConditions.visibilityOfElementLocated(activeSortOption));
            String text = active.getText();
            System.out.println("Active sort option: " + text);
            return text;
        } catch (Exception e) {
            System.err.println("Error reading active sort option: " + e.getMessage());
            return "";
        }
    }

    /**
     * Verify that the first item has the lowest price
     * @return true if first item has lowest price
     */
    public boolean verifyFirstItemHasLowestPrice() {
        try {
            List<WebElement> items = driver.findElements(inventoryItems);

            // Get first item price
            String firstPriceText = driver.findElement(firstItemPrice).getText();
            double firstPrice = parsePrice(firstPriceText);
            System.out.println("First item price: $" + firstPrice);

            // Check all other prices
            boolean isLowest = true;
            for (int i = 1; i < items.size(); i++) {
                String priceText = driver.findElement(
                    By.xpath("(//div[@data-test='inventory-item'])[" + (i + 1) + "]//div[@data-test='inventory-item-price']")
                ).getText();
                double price = parsePrice(priceText);

                if (price < firstPrice) {
                    isLowest = false;
                    System.err.println("Item at position " + (i + 1) + " has lower price: $" + price);
                }
            }

            System.out.println("First item has lowest price: " + isLowest);
            return isLowest;

        } catch (Exception e) {
            System.err.println("Error verifying lowest price: " + e.getMessage());
            return false;
        }
    }

    /**
     * Store the name and price of the first product
     */
    public void storeFirstProductInfo() {
        try {
            productName = driver.findElement(firstItemName).getText();
            productPrice = driver.findElement(firstItemPrice).getText();
            System.out.println("Stored product info - Name: " + productName + ", Price: " + productPrice);
        } catch (Exception e) {
            System.err.println("Error storing product info: " + e.getMessage());
            throw new RuntimeException("Failed to store product info", e);
        }
    }

    /**
     * Click Add to Cart button for the first item
     */
    public void clickFirstItemAddToCart() {
        try {
            WebElement addButton = driver.findElement(firstItemAddToCartButton);
            addButton.click();
            System.out.println("Clicked 'Add to cart' button for first item");
        } catch (Exception e) {
            System.err.println("Error clicking add to cart: " + e.getMessage());
            throw new RuntimeException("Failed to click add to cart", e);
        }
    }

    /**
     * Verify that Remove button is visible after adding to cart
     */
    public boolean isRemoveButtonVisible() {
        try {
            By removeButton = By.xpath("(//div[@data-test='inventory-item'])[1]//button[contains(@data-test, 'remove')]");
            WebElement button = wait.until(ExpectedConditions.visibilityOfElementLocated(removeButton));
            boolean isVisible = button.isDisplayed() && button.getText().equals("Remove");
            System.out.println("Remove button visible: " + isVisible);
            return isVisible;
        } catch (Exception e) {
            System.err.println("Remove button not visible: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get the shopping cart badge count
     * Uses visibility wait + JavaScript fallback for reliability
     * @return cart count, or 0 if badge not present
     */
    public int getCartBadgeCount() {
        try {
            // First, try to find and wait for badge to be visible
            try {
                WebElement badge = wait.until(ExpectedConditions.visibilityOfElementLocated(shoppingCartBadge));
                int count = Integer.parseInt(badge.getText().trim());
                System.out.println("Cart badge count (DOM): " + count);
                return count;
            } catch (Exception e) {
                // Fallback: Use JavaScript to check if badge exists
                Object result = ((JavascriptExecutor) driver).executeScript(
                    "return document.querySelector('span[data-test=\"shopping-cart-badge\"]')?.innerText || '0';"
                );

                if (result != null) {
                    int count = Integer.parseInt(result.toString().trim());
                    System.out.println("Cart badge count (JavaScript): " + count);
                    return count;
                } else {
                    return 0;
                }
            }
        } catch (Exception e) {
            System.out.println("Cart badge not present (cart is empty)");
            return 0;
        }
    }

    /**
     * Verify cart badge is incremented
     * @param previousCount the count before adding item
     * @return true if incremented
     */
    public boolean verifyCartBadgeIncremented(int previousCount) {
        try {
            Thread.sleep(500); // Wait for badge to update
            int newCount = getCartBadgeCount();

            boolean isIncremented = newCount > previousCount;
            System.out.println("Cart badge incremented: " + isIncremented + " (from " + previousCount + " to " + newCount + ")");
            return isIncremented;
        } catch (Exception e) {
            System.err.println("Error verifying cart increment: " + e.getMessage());
            return false;
        }
    }

    /**
     * Click on shopping cart
     */
    public void clickShoppingCart() {
        try {
            WebElement cart = wait.until(ExpectedConditions.elementToBeClickable(shoppingCartLink));
            cart.click();
            System.out.println("Clicked shopping cart");
        } catch (Exception e) {
            System.err.println("Error clicking shopping cart: " + e.getMessage());
            throw new RuntimeException("Failed to click shopping cart", e);
        }
    }

    /**
     * Open side menu and click logout
     */
    public void logout() {
        try {
            WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(menuButton));
            menu.click();
            WebElement logout = wait.until(ExpectedConditions.elementToBeClickable(logoutLink));
            logout.click();
            System.out.println("Logged out successfully");
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            throw new RuntimeException("Logout failed", e);
        }
    }

    /**
     * Login with standard credentials and wait for Products page
     */
    public void loginStandardUser() {
        try {
            System.out.println("Starting login flow");
            driver.get("https://www.saucedemo.com/");
            System.out.println("Navigated to: https://www.saucedemo.com/");

            WebElement usernameField = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("user-name"))
            );
            usernameField.clear();
            usernameField.sendKeys("standard_user");
            System.out.println("Entered username: standard_user");

            WebElement passwordField = driver.findElement(By.id("password"));
            passwordField.clear();
            passwordField.sendKeys("secret_sauce");
            System.out.println("Entered password: secret_sauce");

            WebElement loginButton = driver.findElement(By.id("login-button"));
            loginButton.click();
            System.out.println("Clicked login button");

            wait.until(ExpectedConditions.visibilityOfElementLocated(productsTitle));
            System.out.println("Login successful - Products page displayed");
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            throw new RuntimeException("Login failed", e);
        }
    }

    /**
     * Helper method to parse price string to double
     */
    private double parsePrice(String priceText) {
        return Double.parseDouble(priceText.replace("$", "").trim());
    }

    /**
     * Verify product in cart matches stored product info
     */
    public boolean verifyProductInCart() {
        try {
            Thread.sleep(1000); // Wait for cart page to load

            // Get all items in cart
            List<WebElement> cartItems = driver.findElements(By.cssSelector("div[data-test='inventory-item']"));

            // Find the product we added
            boolean found = false;
            for (WebElement item : cartItems) {
                String nameInCart = item.findElement(By.cssSelector("div[data-test='inventory-item-name']")).getText();
                String priceInCart = item.findElement(By.cssSelector("div[data-test='inventory-item-price']")).getText();

                if (nameInCart.equals(productName) && priceInCart.equals(productPrice)) {
                    found = true;
                    System.out.println("Product verified in cart - Name: " + nameInCart + ", Price: " + priceInCart);
                    break;
                }
            }

            if (!found) {
                System.err.println("Product not found in cart. Expected: " + productName + " " + productPrice);
            }

            return found;

        } catch (Exception e) {
            System.err.println("Error verifying product in cart: " + e.getMessage());
            return false;
        }
    }

    public String getItemNameAtPosition(int positionOneBased) {
        String name = driver.findElement(
            By.xpath("(//div[@data-test='inventory-item'])[" + positionOneBased + "]//div[@data-test='inventory-item-name']")
        ).getText();
        System.out.println("Item name at position " + positionOneBased + ": " + name);
        return name;
    }

    public String getItemPriceAtPosition(int positionOneBased) {
        String price = driver.findElement(
            By.xpath("(//div[@data-test='inventory-item'])[" + positionOneBased + "]//div[@data-test='inventory-item-price']")
        ).getText();
        System.out.println("Item price at position " + positionOneBased + ": " + price);
        return price;
    }

    public void storeProductInfoAtPosition(int positionOneBased) {
        productName = getItemNameAtPosition(positionOneBased);
        productPrice = getItemPriceAtPosition(positionOneBased);
        System.out.println("Stored product info - Name: " + productName + ", Price: " + productPrice);
    }

    public void clickAddToCartAtPosition(int positionOneBased) {
        try {
            By addButton = By.xpath("(//div[@data-test='inventory-item'])[" + positionOneBased + "]//button[contains(@data-test, 'add-to-cart')]");
            WebElement button = driver.findElement(addButton);
            button.click();
            System.out.println("Clicked 'Add to cart' at position " + positionOneBased);
        } catch (Exception e) {
            System.err.println("Error clicking add to cart at position " + positionOneBased + ": " + e.getMessage());
            throw new RuntimeException("Failed to click add to cart", e);
        }
    }

    public void waitForCartBadgeCount(int expectedCount) {
        try {
            // Increase timeout for CI/headless environments (45s instead of 30s)
            WebDriverWait extendedWait = new WebDriverWait(driver, Duration.ofSeconds(45));
            extendedWait.pollingEvery(Duration.ofMillis(250)); // Poll more frequently

            extendedWait.until(driver -> {
                int currentCount = getCartBadgeCount();
                if (currentCount != expectedCount) {
                    // Try scrolling to ensure element is in viewport
                    try {
                        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
                    } catch (Exception ignored) {}
                }
                return currentCount == expectedCount;
            });

            System.out.println("✓ Cart badge count reached: " + expectedCount);
        } catch (TimeoutException e) {
            int actualCount = getCartBadgeCount();
            System.err.println("✗ Cart badge count timeout - Expected: " + expectedCount + ", Got: " + actualCount);
            throw new RuntimeException("Cart badge count mismatch - Expected: " + expectedCount + ", Got: " + actualCount, e);
        } catch (Exception e) {
            System.err.println("✗ Error waiting for cart badge count: " + e.getMessage());
            throw new RuntimeException("Cart badge count error", e);
        }
    }

    // Getters for stored product info (for assertions)
    public String getStoredProductName() {
        return productName;
    }

    public String getStoredProductPrice() {
        return productPrice;
    }
}
