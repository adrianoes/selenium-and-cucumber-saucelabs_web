package stepdefinitions.purchase;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import pages.CartPage;
import pages.CheckoutCompletePage;
import pages.CheckoutOverviewPage;
import pages.CheckoutPage;
import pages.ProductPurchasePage;
import support.BaseTest;

/**
 * Step Definitions and Hooks for Product Purchase feature
 * Maps Gherkin steps to Java methods
 * Handles WebDriver setup/teardown via @Before/@After hooks
 */
public class ProductPurchaseStepDefinitions extends BaseTest {

    private ProductPurchasePage productPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;
    private CheckoutOverviewPage checkoutOverviewPage;
    private CheckoutCompletePage checkoutCompletePage;
    private int cartCountBeforeAdd;
    private int lastStoredPosition = 1;

    // ═══════════════════════════════════════════════════════════════════════════════
    // HOOKS - WebDriver Lifecycle Management
    // ═══════════════════════════════════════════════════════════════════════════════

    /**
     * Setup WebDriver before each @purchase scenario
     */
    @Before("@purchase")
    public void beforePurchaseScenario() {
        BaseTest.setupDriver();
        System.out.println("✓ Cucumber @Before: WebDriver initialized for @purchase scenario");
    }

    /**
     * Cleanup WebDriver after each @purchase scenario
     */
    @After("@purchase")
    public void afterPurchaseScenario() {
        BaseTest.teardownDriver();
        System.out.println("✓ Cucumber @After: WebDriver closed after @purchase scenario");
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    // LAZY INITIALIZATION - Page Objects
    // ═══════════════════════════════════════════════════════════════════════════════

    private ProductPurchasePage getProductPage() {
        if (productPage == null) {
            productPage = new ProductPurchasePage(driver);
        }
        return productPage;
    }

    private CartPage getCartPage() {
        if (cartPage == null) {
            cartPage = new CartPage(driver);
        }
        return cartPage;
    }

    private CheckoutPage getCheckoutPage() {
        if (checkoutPage == null) {
            checkoutPage = new CheckoutPage(driver);
        }
        return checkoutPage;
    }

    private CheckoutOverviewPage getCheckoutOverviewPage() {
        if (checkoutOverviewPage == null) {
            checkoutOverviewPage = new CheckoutOverviewPage(driver);
        }
        return checkoutOverviewPage;
    }

    private CheckoutCompletePage getCheckoutCompletePage() {
        if (checkoutCompletePage == null) {
            checkoutCompletePage = new CheckoutCompletePage(driver);
        }
        return checkoutCompletePage;
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    // STEP DEFINITIONS - Gherkin Steps
    // ═══════════════════════════════════════════════════════════════════════════════

    @Given("the user is logged in to the website")
    public void theUserIsLoggedInToTheWebsite() {
        getProductPage().loginStandardUser();
        Assert.assertTrue("Products title should be visible after login",
            getProductPage().isProductsTitleVisible());
    }

    @When("the user selects {string} sorting")
    public void theUserSelectsSorting(String sortOption) {
        getProductPage().clickSortDropdown();
        getProductPage().selectPriceLowToHigh();
    }

    @When("the user verifies the first item has the lowest price")
    public void theUserVerifiesTheFirstItemHasTheLowestPrice() {
        Assert.assertTrue("First item should have the lowest price",
            getProductPage().verifyFirstItemHasLowestPrice());
    }

    @When("the user adds the first item to cart")
    public void theUserAddsTheFirstItemToCart() {
        getProductPage().storeFirstProductInfo();
        getProductPage().clickFirstItemAddToCart();
        cartCountBeforeAdd = 0;  // Badge didn't exist before clicking
    }

    @Then("the Remove button should be visible")
    public void theRemoveButtonShouldBeVisible() {
        Assert.assertTrue("Remove button should be visible after adding to cart",
            getProductPage().isRemoveButtonVisible());
    }

    @Then("the shopping cart badge should be incremented")
    public void theShoppingCartBadgeShouldBeIncremented() {
        Assert.assertTrue("Shopping cart badge should be incremented",
            getProductPage().verifyCartBadgeIncremented(cartCountBeforeAdd));
    }

    @When("the user opens the shopping cart")
    public void theUserOpensTheShoppingCart() {
        getProductPage().clickShoppingCart();
    }

    @Then("the product should be in the cart")
    public void theProductShouldBeInTheCart() {
        Assert.assertTrue("Product should be visible in cart with correct name and price",
            getCartPage().isProductInCart(getProductPage().getStoredProductName(), getProductPage().getStoredProductPrice()));
    }

    @When("the user proceeds to checkout")
    public void theUserProceedsToCheckout() {
        getCartPage().clickCheckout();
    }

    @When("the user fills shipping information")
    public void theUserFillsShippingInformation() {
        getCheckoutPage().fillCheckoutInformation();
    }

    @When("the user continues to payment review")
    public void theUserContinuesToPaymentReview() {
        getCheckoutPage().clickContinue();
    }

    @Then("the product should be in the overview")
    public void theProductShouldBeInTheOverview() {
        Assert.assertTrue("Product should be in overview with correct name and price",
            getCheckoutOverviewPage().isProductInOverview(getProductPage().getStoredProductName(), getProductPage().getStoredProductPrice()));
    }

    @When("the user completes the order")
    public void theUserCompletesTheOrder() {
        getCheckoutOverviewPage().clickFinish();
    }

    @Then("the order confirmation should be displayed")
    public void theOrderConfirmationShouldBeDisplayed() {
        Assert.assertTrue("Thank you message should be displayed",
            getCheckoutCompletePage().isThankYouMessageVisible());
    }

    @When("the user goes back home")
    public void theUserGoesBackHome() {
        getCheckoutCompletePage().clickBackHome();
    }

    @Then("the user should be on the products page")
    public void theUserShouldBeOnTheProductsPage() {
        Assert.assertTrue("Products title should be visible",
            getProductPage().isProductsTitleVisible());
    }

    @When("the user stores the item at position {int}")
    public void theUserStoresTheItemAtPosition(int position) {
        lastStoredPosition = position;
        getProductPage().storeProductInfoAtPosition(position);
    }

    @When("the user adds the item at position {int} to the cart")
    public void theUserAddsTheItemAtPositionToTheCart(int position) {
        getProductPage().clickAddToCartAtPosition(position);
    }

    @Then("the shopping cart badge should be {string}")
    public void theShoppingCartBadgeShouldBe(String expectedCount) {
        int expected = Integer.parseInt(expectedCount);
        getProductPage().waitForCartBadgeCount(expected);
    }

    @When("the user switches sorting to {string}")
    public void theUserSwitchesSortingTo(String sortOption) {
        getProductPage().clickSortDropdown();
        getProductPage().selectPriceHighToLow();
    }

    @When("the user removes all items from the cart")
    public void theUserRemovesAllItemsFromTheCart() {
        getCartPage().removeAllItems();
    }

    @When("the user logs out")
    public void theUserLogsOut() {
        getProductPage().logout();
    }

    @When("the user logs in again")
    public void theUserLogsInAgain() {
        getProductPage().loginStandardUser();
    }
}
