package tests;

import org.junit.*;
import org.junit.rules.TestName;
import pages.CartPage;
import pages.CheckoutCompletePage;
import pages.CheckoutOverviewPage;
import pages.CheckoutPage;
import pages.ProductPurchasePage;
import support.BaseTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * JUnit Test for Product Purchase
 *
 * This test can be executed WITHOUT Cucumber, using pure JUnit
 * It performs the same flow as the Cucumber scenario
 *
 * How to run:
 * 1. Via Maven: mvn clean test -Dtest=ProductPurchaseTest
 * 2. Via IDE: Right-click this class and select "Run"
 */
public class ProductPurchaseTest extends BaseTest {

    private ProductPurchasePage productPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;
    private CheckoutOverviewPage checkoutOverviewPage;
    private CheckoutCompletePage checkoutCompletePage;
    private static final Path SCREENSHOTS_DIR = Paths.get("target", "screenshots");
    private boolean testFailed = false;

    @Rule
    public TestName testName = new TestName();

    static {
        try {
            Files.createDirectories(Paths.get("target", "screenshots"));
        } catch (Exception e) {
            System.err.println("⚠️ Could not create screenshots directory: " + e.getMessage());
        }
    }

    @Before
    public void setup() {
        BaseTest.setupDriver();
        productPage = new ProductPurchasePage(driver);
        cartPage = new CartPage(driver);
        checkoutPage = new CheckoutPage(driver);
        checkoutOverviewPage = new CheckoutOverviewPage(driver);
        checkoutCompletePage = new CheckoutCompletePage(driver);
        testFailed = false;
    }

    @After
    public void teardown() {
        // Capture screenshot on failure
        if (testFailed && driver != null) {
            String currentTestName = testName.getMethodName();
            String screenshotPath = BaseTest.takeScreenshot(currentTestName);
            if (screenshotPath != null) {
                System.out.println("📸 Screenshot saved: " + screenshotPath);
            } else {
                System.out.println("⚠️ Failed to capture screenshot");
            }
        }

        BaseTest.teardownDriver();
    }

    @Test
    public void testCompletePurchaseFlow() {
        try {
            runTestCompletePurchaseFlow();
        } catch (AssertionError | Exception e) {
            testFailed = true;
            throw e;
        }
    }

    private void runTestCompletePurchaseFlow() {
        System.out.println("\n--- Starting Complete Purchase Flow Test ---\n");

        // Step 1: Login
        System.out.println("Step 1: Login to website");
        productPage.loginStandardUser();
        Assert.assertTrue("Products title should be visible after login",
            productPage.isProductsTitleVisible());

        // Step 2: Sort by price (low to high)
        System.out.println("\nStep 2: Sort products by price (low to high)");
        productPage.clickSortDropdown();
        productPage.selectPriceLowToHigh();

        // Step 3: Verify first item has lowest price
        System.out.println("\nStep 3: Verify first item has lowest price");
        Assert.assertTrue("First item should have the lowest price",
            productPage.verifyFirstItemHasLowestPrice());

        // Step 4: Store product info and add to cart
        System.out.println("\nStep 4: Add first item to cart");
        productPage.storeFirstProductInfo();
        productPage.clickFirstItemAddToCart();
        int cartCountBeforeAdd = 0;  // Badge didn't exist before clicking

        // Step 5: Verify Remove button is visible
        System.out.println("\nStep 5: Verify Remove button is visible");
        Assert.assertTrue("Remove button should be visible after adding to cart",
            productPage.isRemoveButtonVisible());

        // Step 6: Verify cart badge is incremented
        System.out.println("\nStep 6: Verify shopping cart badge is incremented");
        Assert.assertTrue("Shopping cart badge should be incremented",
            productPage.verifyCartBadgeIncremented(cartCountBeforeAdd));

        // Step 7: Open shopping cart
        System.out.println("\nStep 7: Open shopping cart");
        productPage.clickShoppingCart();

        // Step 8: Verify product in cart
        System.out.println("\nStep 8: Verify product is in cart");
        Assert.assertTrue("Product should be visible in cart with correct name and price",
            cartPage.isProductInCart(productPage.getStoredProductName(), productPage.getStoredProductPrice()));

        // Step 9: Proceed to checkout
        System.out.println("\nStep 9: Proceed to checkout");
        cartPage.clickCheckout();

        // Step 10: Fill shipping information
        System.out.println("\nStep 10: Fill shipping information");
        checkoutPage.fillCheckoutInformation();

        // Step 11: Continue to overview
        System.out.println("\nStep 11: Continue to payment review");
        checkoutPage.clickContinue();

        // Step 12: Verify product in overview
        System.out.println("\nStep 12: Verify product in overview");
        Assert.assertTrue("Product should be in overview with correct name and price",
            checkoutOverviewPage.isProductInOverview(productPage.getStoredProductName(), productPage.getStoredProductPrice()));

        // Step 13: Complete the order
        System.out.println("\nStep 13: Complete the order");
        checkoutOverviewPage.clickFinish();

        // Step 14: Verify order confirmation
        System.out.println("\nStep 14: Verify order confirmation");
        Assert.assertTrue("Thank you message should be displayed",
            checkoutCompletePage.isThankYouMessageVisible());

        // Step 15: Go back home
        System.out.println("\nStep 15: Go back to products page");
        checkoutCompletePage.clickBackHome();

        // Step 16: Verify back on products page
        System.out.println("\nStep 16: Verify back on products page");
        Assert.assertTrue("Products title should be visible",
            productPage.isProductsTitleVisible());

        System.out.println("\n--- Complete Purchase Flow Test PASSED ---\n");
    }

    @Test
    public void testAddLowestPriceRemoveAndLogout() {
        try {
            runTestAddLowestPriceRemoveAndLogout();
        } catch (AssertionError | Exception e) {
            testFailed = true;
            throw e;
        }
    }

    private void runTestAddLowestPriceRemoveAndLogout() {
        System.out.println("\n--- Starting Cart Scenario: Lowest Price Add/Remove/Logout ---\n");

        // Step 1: Login
        System.out.println("Step 1: Login to website");
        productPage.loginStandardUser();
        Assert.assertTrue("Products title should be visible after login",
            productPage.isProductsTitleVisible());

        // Step 2: Sort by price (low to high)
        System.out.println("\nStep 2: Sort products by price (low to high)");
        productPage.clickSortDropdown();
        productPage.selectPriceLowToHigh();

        // Step 3: Verify first item has lowest price
        System.out.println("\nStep 3: Verify first item has lowest price");
        Assert.assertTrue("First item should have the lowest price",
            productPage.verifyFirstItemHasLowestPrice());

        // Step 4: Add lowest price item to cart
        System.out.println("\nStep 4: Add lowest price item to cart");
        productPage.storeProductInfoAtPosition(1);
        productPage.clickAddToCartAtPosition(1);
        productPage.waitForCartBadgeCount(1);
        Assert.assertEquals("Cart badge should be 1", 1, productPage.getCartBadgeCount());

        // Step 5: Open shopping cart
        System.out.println("\nStep 5: Open shopping cart");
        productPage.clickShoppingCart();

        // Step 6: Remove item from cart
        System.out.println("\nStep 6: Remove item from cart");
        cartPage.removeAllItems();

        // Step 7: Logout
        System.out.println("\nStep 7: Logout");
        productPage.logout();

        System.out.println("\n--- Cart Scenario PASSED ---\n");
    }

    @Test
    public void testAddLowestAndHighestRemoveAndLogout() {
        try {
            runTestAddLowestAndHighestRemoveAndLogout();
        } catch (AssertionError | Exception e) {
            testFailed = true;
            throw e;
        }
    }

    private void runTestAddLowestAndHighestRemoveAndLogout() {
        System.out.println("\n--- Starting Cart Scenario: Lowest + Highest Add/Remove/Logout ---\n");

        // Step 1: Login
        System.out.println("Step 1: Login to website");
        productPage.loginStandardUser();
        Assert.assertTrue("Products title should be visible after login",
            productPage.isProductsTitleVisible());

        // Step 2: Sort by price (low to high)
        System.out.println("\nStep 2: Sort products by price (low to high)");
        productPage.clickSortDropdown();
        productPage.selectPriceLowToHigh();

        // Step 3: Verify first item has lowest price
        System.out.println("\nStep 3: Verify first item has lowest price");
        Assert.assertTrue("First item should have the lowest price",
            productPage.verifyFirstItemHasLowestPrice());

        // Step 4: Add lowest price item
        System.out.println("\nStep 4: Add lowest price item to cart");
        productPage.storeProductInfoAtPosition(1);
        productPage.clickAddToCartAtPosition(1);
        productPage.waitForCartBadgeCount(1);
        Assert.assertEquals("Cart badge should be 1", 1, productPage.getCartBadgeCount());

        // Step 5: Switch to price (high to low)
        System.out.println("\nStep 5: Switch to price (high to low) sorting");
        productPage.clickSortDropdown();
        productPage.selectPriceHighToLow();

        // Step 6: Add highest price item (now at position 1)
        System.out.println("\nStep 6: Add highest price item to cart");
        productPage.clickAddToCartAtPosition(1);
        productPage.waitForCartBadgeCount(2);
        Assert.assertEquals("Cart badge should be 2", 2, productPage.getCartBadgeCount());

        // Step 7: Open shopping cart
        System.out.println("\nStep 7: Open shopping cart");
        productPage.clickShoppingCart();

        // Step 8: Remove all items from cart
        System.out.println("\nStep 8: Remove all items from cart");
        cartPage.removeAllItems();

        // Step 9: Logout
        System.out.println("\nStep 9: Logout");
        productPage.logout();

        System.out.println("\n--- Cart Scenario PASSED ---\n");
    }

    @Test
    public void testAddLowestAndHighestPersistAfterRelogin() {
        try {
            runTestAddLowestAndHighestPersistAfterRelogin();
        } catch (AssertionError | Exception e) {
            testFailed = true;
            throw e;
        }
    }

    private void runTestAddLowestAndHighestPersistAfterRelogin() {
        System.out.println("\n--- Starting Cart Scenario: Persist After Relogin ---\n");

        // Step 1: Login
        System.out.println("Step 1: Login to website");
        productPage.loginStandardUser();
        Assert.assertTrue("Products title should be visible after login",
            productPage.isProductsTitleVisible());

        // Step 2: Sort by price (low to high)
        System.out.println("\nStep 2: Sort products by price (low to high)");
        productPage.clickSortDropdown();
        productPage.selectPriceLowToHigh();

        // Step 3: Verify first item has lowest price
        System.out.println("\nStep 3: Verify first item has lowest price");
        Assert.assertTrue("First item should have the lowest price",
            productPage.verifyFirstItemHasLowestPrice());

        // Step 4: Add lowest price item
        System.out.println("\nStep 4: Add lowest price item to cart");
        productPage.storeProductInfoAtPosition(1);
        productPage.clickAddToCartAtPosition(1);
        productPage.waitForCartBadgeCount(1);
        Assert.assertEquals("Cart badge should be 1", 1, productPage.getCartBadgeCount());

        // Step 5: Switch to price (high to low)
        System.out.println("\nStep 5: Switch to price (high to low) sorting");
        productPage.clickSortDropdown();
        productPage.selectPriceHighToLow();

        // Step 6: Add highest price item (now at position 1)
        System.out.println("\nStep 6: Add highest price item to cart");
        productPage.clickAddToCartAtPosition(1);
        productPage.waitForCartBadgeCount(2);
        Assert.assertEquals("Cart badge should be 2", 2, productPage.getCartBadgeCount());

        // Step 7: Logout
        System.out.println("\nStep 7: Logout");
        productPage.logout();

        // Step 8: Login again
        System.out.println("\nStep 8: Login again");
        productPage.loginStandardUser();

        // Step 9: Verify cart badge persists with count 2
        System.out.println("\nStep 9: Verify cart badge persists after relogin");
        productPage.waitForCartBadgeCount(2);
        Assert.assertEquals("Cart badge should remain 2 after relogin", 2, productPage.getCartBadgeCount());

        System.out.println("\n--- Cart Scenario PASSED ---\n");
    }
}
