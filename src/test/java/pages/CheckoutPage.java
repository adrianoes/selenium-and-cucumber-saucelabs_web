package pages;

import com.github.javafaker.Faker;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for Checkout: Your Information page
 */
public class CheckoutPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final Faker faker;

    private final By firstNameField = By.cssSelector("input[data-test='firstName']");
    private final By lastNameField = By.cssSelector("input[data-test='lastName']");
    private final By postalCodeField = By.cssSelector("input[data-test='postalCode']");
    private final By continueButton = By.cssSelector("input[data-test='continue']");

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.faker = new Faker();
    }

    public void fillCheckoutInformation() {
        try {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String postalCode = faker.number().digits(5);

            WebElement firstNameInput = wait.until(ExpectedConditions.elementToBeClickable(firstNameField));
            firstNameInput.clear();
            firstNameInput.sendKeys(firstName);
            System.out.println("Entered first name: " + firstName);

            WebElement lastNameInput = driver.findElement(lastNameField);
            lastNameInput.clear();
            lastNameInput.sendKeys(lastName);
            System.out.println("Entered last name: " + lastName);

            WebElement postalCodeInput = driver.findElement(postalCodeField);
            postalCodeInput.clear();
            postalCodeInput.sendKeys(postalCode);
            System.out.println("Entered postal code: " + postalCode);
        } catch (Exception e) {
            System.err.println("Error filling checkout information: " + e.getMessage());
            throw new RuntimeException("Failed to fill checkout information", e);
        }
    }

    public void clickContinue() {
        try {
            WebElement button = driver.findElement(continueButton);
            button.click();
            System.out.println("Clicked Continue button");
        } catch (Exception e) {
            System.err.println("Error clicking continue: " + e.getMessage());
            throw new RuntimeException("Failed to click continue", e);
        }
    }
}

