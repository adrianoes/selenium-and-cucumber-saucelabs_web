package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.WebDriver;
import pages.LoginPage;
import support.BaseTest;
import com.github.javafaker.Faker;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Login Test Class
 * Tests 3 scenarios: Happy path (valid credentials), Invalid password, Invalid username
 */
@DisplayName("Login Tests - Authentication Scenarios")
public class LoginTest extends BaseTest {

    private LoginPage loginPage;
    private Faker faker;
    private boolean testFailed = false;


    /**
     * Executed BEFORE each test
     */
    @BeforeEach
    public void setUp() {
        System.out.println("\n========== TEST STARTED ==========");
        testFailed = false;

        // Setup WebDriver using BaseTest (uses DriverFactory for CI/CD compatibility)
        BaseTest.setupDriver();
        loginPage = new LoginPage(driver);
        faker = new Faker();

        System.out.println("✓ WebDriver initialized");
    }

    /**
     * Executed AFTER each test
     */
    @AfterEach
    public void tearDown(TestInfo testInfo) {
        // Capture screenshot on failure
        if (testFailed && driver != null) {
            String testName = testInfo.getDisplayName();
            String screenshotPath = BaseTest.takeScreenshot(testName);
            if (screenshotPath != null) {
                System.out.println("📸 Screenshot saved: " + screenshotPath);
            } else {
                System.out.println("⚠️ Failed to capture screenshot");
            }
        }

        BaseTest.teardownDriver();
        System.out.println("========== TEST COMPLETED ==========\n");
    }

    // TEST 1: HAPPY PATH

    /**
     * Test: Happy path - Login with valid credentials
     */
    @Test
    @DisplayName("Should successfully login with valid credentials")
    public void testLoginWithValidCredentials() {
        try {
            runTestLoginWithValidCredentials();
        } catch (AssertionError | Exception e) {
            testFailed = true;
            throw e;
        }
    }

    private void runTestLoginWithValidCredentials() {
        System.out.println("\n--- Starting Login Test: Valid Credentials ---\n");

        // Arrange
        String username = "standard_user5";
        String password = "secret_sauce";

        System.out.println("Step 1: Navigate to login page");
        loginPage.navigateToLoginPage();

        System.out.println("\nStep 2: Enter username");
        loginPage.enterUsername(username);

        System.out.println("\nStep 3: Enter password");
        loginPage.enterPassword(password);

        System.out.println("\nStep 4: Click login button");
        loginPage.clickLoginButton();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\nStep 5: Verify successful login");
        String currentUrl = driver.getCurrentUrl();
        assertTrue(
            currentUrl.contains("inventory"),
            "ERROR: Should be redirected to inventory page"
        );
        System.out.println("✓ Successfully logged in and redirected to inventory page");

        System.out.println("\n--- Login Test: Valid Credentials PASSED ---\n");
    }

    // TEST 2: INVALID PASSWORD

    /**
     * Test: Negative scenario - Login with invalid password
     */
    @Test
    @DisplayName("Should fail login with invalid password")
    public void testLoginWithInvalidPassword() {
        try {
            runTestLoginWithInvalidPassword();
        } catch (AssertionError | Exception e) {
            testFailed = true;
            throw e;
        }
    }

    private void runTestLoginWithInvalidPassword() {
        System.out.println("\n--- Starting Login Test: Invalid Password ---\n");

        // Arrange
        String username = "standard_user";
        String invalidPassword = faker.internet().password();

        System.out.println("Step 1: Navigate to login page");
        loginPage.navigateToLoginPage();

        System.out.println("\nStep 2: Enter username");
        loginPage.enterUsername(username);

        System.out.println("\nStep 3: Enter invalid password (generated with Faker)");
        loginPage.enterPassword(invalidPassword);

        System.out.println("\nStep 4: Click login button");
        loginPage.clickLoginButton();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\nStep 5: Verify error message is displayed");
        assertTrue(
            loginPage.isErrorMessageDisplayed(),
            "ERROR: Error message should be displayed!"
        );
        String errorMessage = loginPage.getErrorMessage();
        System.out.println("✓ Error message is displayed");
        System.out.println("✓ Error message captured: " + errorMessage);

        System.out.println("\nStep 6: Verify error message contains 'Epic sadface'");
        assertTrue(
            errorMessage.contains("Epic sadface"),
            "ERROR: Message should contain 'Epic sadface'. Actual: " + errorMessage
        );
        System.out.println("✓ Error message contains 'Epic sadface'");

        System.out.println("\nStep 7: Verify error icons are visible");
        assertTrue(
            loginPage.areErrorIconsDisplayed(),
            "ERROR: Error icons should be visible!"
        );
        int errorIconCount = 2; // Username and password fields
        System.out.println("✓ Error icons found: " + errorIconCount);

        System.out.println("\nStep 8: Verify username field has error class");
        assertTrue(
            loginPage.doesUsernameFieldHaveErrorClass(),
            "ERROR: Username field should have error class"
        );
        System.out.println("✓ Username field has error class: " + loginPage.doesUsernameFieldHaveErrorClass());

        System.out.println("\nStep 9: Verify password field has error class");
        assertTrue(
            loginPage.doesPasswordFieldHaveErrorClass(),
            "ERROR: Password field should have error class"
        );
        System.out.println("✓ Password field has error class: " + loginPage.doesPasswordFieldHaveErrorClass());

        System.out.println("\n--- Login Test: Invalid Password PASSED ---\n");
    }

    // TEST 3: INVALID USERNAME

    /**
     * Test: Negative scenario - Login with invalid username
     */
    @Test
    @DisplayName("Should fail login with invalid username")
    public void testLoginWithInvalidUsername() {
        try {
            runTestLoginWithInvalidUsername();
        } catch (AssertionError | Exception e) {
            testFailed = true;
            throw e;
        }
    }

    private void runTestLoginWithInvalidUsername() {
        System.out.println("\n--- Starting Login Test: Invalid Username ---\n");

        // Arrange
        String invalidUsername = faker.name().username();
        String password = "secret_sauce";

        System.out.println("Step 1: Navigate to login page");
        loginPage.navigateToLoginPage();

        System.out.println("\nStep 2: Enter invalid username (generated with Faker)");
        loginPage.enterUsername(invalidUsername);

        System.out.println("\nStep 3: Enter password");
        loginPage.enterPassword(password);

        System.out.println("\nStep 4: Click login button");
        loginPage.clickLoginButton();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\nStep 5: Verify error message is displayed");
        assertTrue(
            loginPage.isErrorMessageDisplayed(),
            "ERROR: Error message should be displayed!"
        );
        String errorMessage = loginPage.getErrorMessage();
        System.out.println("✓ Error message is displayed");
        System.out.println("✓ Error message captured: " + errorMessage);

        System.out.println("\nStep 6: Verify error message matches expected text");
        String expectedMessage = "Epic sadface: Username and password do not match any user in this service";
        assertEquals(
            expectedMessage,
            errorMessage,
            "ERROR: Error message should match exactly"
        );
        System.out.println("✓ Error message matches expected text");

        System.out.println("\nStep 7: Verify both fields have error styling");
        assertTrue(
            loginPage.doesUsernameFieldHaveErrorClass() && loginPage.doesPasswordFieldHaveErrorClass(),
            "ERROR: Both fields should have error class"
        );
        System.out.println("✓ Username field has error class: " + loginPage.doesUsernameFieldHaveErrorClass());
        System.out.println("✓ Password field has error class: " + loginPage.doesPasswordFieldHaveErrorClass());

        System.out.println("\nStep 8: Verify error icons are visible");
        assertTrue(
            loginPage.areErrorIconsDisplayed(),
            "ERROR: Error icons should be visible!"
        );
        int errorIconCount = 2; // Username and password fields
        System.out.println("✓ Error icons found: " + errorIconCount);

        System.out.println("\nStep 9: Verify user remains on login page");
        String currentUrl = driver.getCurrentUrl();
        assertFalse(
            currentUrl.contains("inventory"),
            "ERROR: Should NOT be redirected to inventory page"
        );
        System.out.println("✓ User remains on login page (not redirected to inventory)");

        System.out.println("\n--- Login Test: Invalid Username PASSED ---\n");
    }
}

