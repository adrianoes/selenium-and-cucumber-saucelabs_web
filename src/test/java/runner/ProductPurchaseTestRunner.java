package runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Cucumber Test Runner for Product Purchase tests
 *
 * This class executes tests using Cucumber framework (BDD approach)
 *
 * How to run:
 * 1. Via Maven (all tests): mvn clean test -Dtest=ProductPurchaseTestRunner
 * 2. Via IDE: Right-click this class and select "Run"
 * 3. With tags (PowerShell): mvn clean test -Dtest=ProductPurchaseTestRunner '-Dcucumber.filter.tags=@purchase'
 * 4. Multiple tags: mvn clean test -Dtest=ProductPurchaseTestRunner '-Dcucumber.filter.tags=@purchase and @smoke'
 *
 * Features:
 * - Pretty console output
 * - HTML report generated in target/cucumber-reports
 * - JSON report for CI/CD integration
 * - Rerun file for failed scenarios
 * - Dynamic tag filtering via system property
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/java/features/ProductPurchase.feature",
    glue = {"stepdefinitions.purchase"},
    plugin = {
        "pretty",
        "html:target/cucumber-reports/product-purchase-report.html",
        "json:target/cucumber-reports/product-purchase-report.json",
        "rerun:target/cucumber-reports/rerun.txt"
    },
    monochrome = true
    // tags can be passed via -Dcucumber.filter.tags system property
    // If not provided, all scenarios will run
)
public class ProductPurchaseTestRunner {
    // This class will be empty - Cucumber uses annotations to run tests
}
