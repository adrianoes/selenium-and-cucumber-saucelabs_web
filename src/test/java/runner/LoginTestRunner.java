package runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Cucumber Test Runner for Login tests
 *
 * This class executes tests using Cucumber framework (BDD approach)
 *
 * How to run:
 * 1. Via Maven (all tests): mvn clean test -Dtest=LoginTestRunner
 * 2. Via IDE: Right-click this class and select "Run"
 * 3. With tags (PowerShell): mvn clean test -Dtest=LoginTestRunner '-Dcucumber.filter.tags=@login'
 * 4. Multiple tags: mvn clean test -Dtest=LoginTestRunner '-Dcucumber.filter.tags=@login and @happy-path'
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
    features = "src/test/java/features/Login.feature",
    glue = {"stepdefinitions.login"},
    plugin = {
        "pretty",
        "html:target/cucumber-reports/login-report.html",
        "json:target/cucumber-reports/login-report.json",
        "rerun:target/cucumber-reports/login-rerun.txt"
    },
    monochrome = true
)
public class LoginTestRunner {
    // Cucumber uses annotations to run tests
}
