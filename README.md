# selenium-and-cucumber-saucelabs_web

UI testing in web application using [saucelabs demo website](https://www.saucedemo.com/). This project contains basic examples on how to use Selenium WebDriver, Cucumber, Java and Gherkin to test UI in web applications. Good practices such as hooks, custom commands, Page Object Model and tags, among others, are used. All the necessary support documentation to develop this project is placed here.

# Pre-requirements:

| Requirement                             | Version       | Note                                                            |
|:----------------------------------------|:--------------| :-------------------------------------------------------------- |
| IntelliJ IDEA Community Edition         | 2025.3.2      | -                                                               |
| JDK                                     | 17.0.12       | -                                                               |
| Maven                                   | 3.9.12        | -                                                               |
| Google Chrome                           | Latest        | Compatible with ChromeDriver                                    |
| ChromeDriver                            | Latest        | Managed automatically by Selenium Manager                       |
| Selenium Java maven dependency          | 4.40.0        | -                                                               |
| Cucumber JVM: Java Maven Repository     | 7.33.0        | -                                                               |
| Cucumber JVM: Core Maven Repository     | 7.33.0        | -                                                               |
| Cucumber JVM: JUnit 4 Maven Repository  | 7.33.0        | -                                                               |
| Jackson Databind Maven Repository       | 2.21.0        | -                                                               |
| Java Faker Maven Repository             | 1.0.2         | -                                                               |
| JSON In Java                            | 20251224      | -                                                               |
| Gherkin plugin for IntelliJ             | 253.28294.218 | -                                                               |
| Cucumber for JAVA plugin for IntelliJ   | 253.30387.2   | -                                                               |

# Installation:

- See [IntelliJ IDEA Community Edition download page](https://www.jetbrains.com/idea/download/?section=windows), download and install IntelliJ IDEA Community Edition. Keep all the prefereced options as they are until you reach Instalation Options page. Then, check the checkboxes below:
    - :white_check_mark: **IntelliJ IDEA Community Edition** on Create Desktop Shortcut frame;
    - :white_check_mark: **Add "Open Folder as Project"** in Update Context Menu frame;
    - :white_check_mark: **Add "bin" Folder to the PATH** in Update PATH Variable (restart needed) frame;
    - :white_check_mark: **.java** in Create Associations frame;
    - :white_check_mark: **.gradle** in Create Associations frame;
    - :white_check_mark: **.groovy** in Create Associations frame;
    - :white_check_mark: **.kt** in Create Associations frame;
    - :white_check_mark: **.kts** in Create Associations frame;
    - :white_check_mark: **.pom** in Create Associations frame;
    - Hit :point_right: **Next**, :point_right: **Install**, :radio_button: **I want to manually reboot later** and :point_right: **Finish**. Save your stuff and reboot the computer.
- See [Java SE 17 Archive Downloads](https://www.oracle.com/br/java/technologies/javase/jdk17-archive-downloads.html), download the proper version for your OS and install it by keeping the preferenced options.
    - Right click :point_right: **My Computer** and select :point_right: **Properties**. On the :point_right: **Advanced** tab, select :point_right: **Environment Variables**, :point_right: **New** in System Variables frame and create a variable called JAVA_HOME containing the path that leads to where the JDK software is located (e.g. C:\Program Files\Java\jdk-17).
    - Right click :point_right: **My Computer** and select :point_right: **Properties**. On the :point_right: **Advanced** tab, select :point_right: **Environment Variables**, and then edit Path system variable with the new %JAVA_HOME%\bin entry.
- See [Maven download page](https://maven.apache.org/download.cgi), download the xxxBinary zip archive and unzip it in a place of your preference (e.g. C:\Program Files\Maven\apache-maven-3.9.12).
    - Right click :point_right: **My Computer** and select :point_right: **Properties**. On the :point_right: **Advanced** tab, select :point_right: **Environment Variables**, :point_right: **New** in System Variables frame and create a variable called MAVEN_HOME containing the path that leads to where the apache-maven software is located (e.g. C:\Program Files\Maven\apache-maven-3.9.12).
    - Right click :point_right: **My Computer** and select :point_right: **Properties**. On the :point_right: **Advanced** tab, select :point_right: **Environment Variables**, and then edit Path system variable with the new %MAVEN_HOME%\bin entry.
- See [Google Chrome download page](https://www.google.com/chrome/) and install the latest version of Google Chrome browser. Keep all the preferenced options as they are.
- **Note:** ChromeDriver is automatically managed by Selenium Manager (included in Selenium 4.6+), so manual installation is not required.
- Open IntelliJ IDEA, hit :point_right: **New Project**, hit :point_right: **Java** in New Project frame, hit :point_right: **Maven** as Build system option and check the checkboxes below:
    - :white_check_mark: **Add sample code**,
      Hit :point_right: **Create**.
- See [Selenium Java](https://mvnrepository.com/artifact/org.seleniumhq.selenium/selenium-java/4.40.0), copy the maven dependency code. Open a dependencies tag in the pom.xml file right below the properties tag and paste the maven dependency copied code there.
- See [Cucumber JVM: Java](https://mvnrepository.com/artifact/io.cucumber/cucumber-java/7.33.0), copy the maven dependency code and paste it in the dependency tag.
- See [Cucumber JVM: Core](https://mvnrepository.com/artifact/io.cucumber/cucumber-core/7.33.0), copy the maven dependency code and paste it in the dependency tag.
- See [Cucumber JVM: JUnit 4](https://mvnrepository.com/artifact/io.cucumber/cucumber-junit/7.33.0), copy the maven dependency code and paste it in the dependency tag.
- See [Jackson Databind](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind/2.21.0), copy the maven dependency code and paste it in the dependency tag.
- See [Java Faker](https://mvnrepository.com/artifact/com.github.javafaker/javafaker/1.0.2), copy the maven dependency code and paste it in the dependency tag.
- See [JSON In Java](https://mvnrepository.com/artifact/org.json/json/20251224), copy the maven dependency code and paste it in the dependency tag. Hit :point_right: **Sync maven changes**. Your dependency tag in the pom.xml file, now, should be something like:

  ```
    <dependencies>
        <!-- Source: https://mvnrepository.com/artifact/org.seleniumhq.selenium/selenium-java -->
        <dependency>
            <groupId>org.seleniumhq.selenium</groupId>
            <artifactId>selenium-java</artifactId>
            <version>4.40.0</version>
            <scope>compile</scope>
        </dependency>

        <!-- Source: https://mvnrepository.com/artifact/io.cucumber/cucumber-java -->
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-java</artifactId>
            <version>7.33.0</version>
            <scope>compile</scope>
        </dependency>

        <!-- Source: https://mvnrepository.com/artifact/io.cucumber/cucumber-core -->
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-core</artifactId>
            <version>7.33.0</version>
            <scope>compile</scope>
        </dependency>

        <!-- Source: https://mvnrepository.com/artifact/io.cucumber/cucumber-junit -->
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-junit</artifactId>
            <version>7.33.0</version>
            <scope>test</scope>
        </dependency>

        <!-- Source: https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.21.0</version>
            <scope>compile</scope>
        </dependency>

        <!-- Source: https://mvnrepository.com/artifact/com.github.javafaker/javafaker -->
        <dependency>
            <groupId>com.github.javafaker</groupId>
            <artifactId>javafaker</artifactId>
            <version>1.0.2</version>
            <scope>compile</scope>
        </dependency>

        <!-- Source: https://mvnrepository.com/artifact/org.json/json -->
        <dependency>
            <groupId>org.json</groupId>
            <artifactId>json</artifactId>
            <version>20251224</version>
            <scope>compile</scope>
        </dependency>

    </dependencies>
  ``` 
- Look for Gherkin in IntelliJ market place and install the one from JetBrains s.r.o.
- Look for Cucumber for JAVA in IntelliJ market place and install the one from JetBrains s.r.o.

# Tests:

- `mvn clean test` - Run all JUnit tests (without Cucumber).
- `mvn test "-Dtest=LoginTestRunner,ProductPurchaseTestRunner"` - Run all Cucumber tests (7 scenarios total).
- `mvn test -Dtest=LoginTestRunner` - Run Login tests with Cucumber (3 scenarios).
- `mvn test -Dtest=ProductPurchaseTestRunner` - Run Product Purchase tests with Cucumber (4 scenarios).
- `mvn test -Dtest=LoginTest` - Run Login tests without Cucumber (JUnit only).
- `mvn test -Dtest=ProductPurchaseTest` - Run Product Purchase tests without Cucumber (JUnit only).
- `mvn test '-Dcucumber.filter.tags=@purchase'` - Run only scenarios tagged with @purchase (PowerShell syntax with single quotes).
- `mvn test '-Dcucumber.filter.tags=@smoke'` - Run only smoke scenarios (PowerShell syntax with single quotes).

# JIRA Integration (Automatic Bug Ticket Creation):

This project includes automatic JIRA issue creation when tests fail. For detailed setup instructions, see **[JIRA_GUIDE_EN.md](JIRA_GUIDE_EN.md)**.

**Quick Start:**
1. Create a `.env` file based on `example.env`
2. Fill in your JIRA credentials (Base URL, Email, API Token, Project Key, Issue Type)
3. Run: `mvn -Pjira-report verify`

**What it does:**
- ✅ Runs all tests (JUnit + Cucumber)
- ✅ Generates HTML reports (Surefire + Cucumber)
- ✅ Creates JIRA issues automatically for each failed test
- ✅ Attaches reports, logs, and screenshots to issues

**Note:** The `jira-report` profile automatically configures Maven to continue even when tests fail, allowing the JIRA reporter to run and create issues.

**Note for PowerShell users:** Use **single quotes** around `-Dcucumber.filter.tags=` options to prevent parsing errors.

# Support:

- [Selenium WebDriver Documentation](https://www.selenium.dev/documentation/webdriver/)
- [Selenium Locating Elements](https://www.selenium.dev/documentation/webdriver/elements/locators/)
- [Selenium Waits](https://www.selenium.dev/documentation/webdriver/waits/)
- [Chrome DevTools Protocol](https://chromedevtools.github.io/devtools-protocol/)
- [ChromeDriver Capabilities](https://chromedriver.chromium.org/capabilities)
- [Maven repositories](https://mvnrepository.com/)
- [Gherkin](https://plugins.jetbrains.com/plugin/9164-gherkin)
- [Page Object Model Pattern](https://www.selenium.dev/documentation/test_practices/encouraged/page_object_models/)
- [Package com.github.javafaker](https://javadoc.io/static/com.github.javafaker/javafaker/1.0.2/com/github/javafaker/package-summary.html)
- [ChatGPT](https://chatgpt.com/)
- [Tag Expressions](https://github.com/cucumber/tag-expressions)
- [10-minute tutorial](https://cucumber.io/docs/guides/10-minute-tutorial)
- [Expected condition failed: waiting for element to be clickable in Selenium](https://stackoverflow.com/a/57069767)
- [org.openqa.selenium.TimeoutException: Expected condition failed: waiting for all conditions to be valid](https://stackoverflow.com/a/62832984)

# Tips:

- When needed, open pom.xml directory and execute ```mvn clean install```. It removes previous build files to ensure a clean environment, while compiles the source code and runs tests to compile the automation again.
- Make sure to have all same number versions for Cucumber JVM: Java, Cucumber JVM: Core and Cucumber JVM: JUnit 4 Maven dependencies.
- **Element Locator Priority for Selenium WebDriver (Use in this order):**
  1. **ID** - Fastest and most reliable. Use `By.id("element-id")` whenever possible.
  2. **CSS Selector** - Fast and flexible. Use `By.cssSelector("selector")` for complex queries.
  3. **Name** - Good for form elements. Use `By.name("element-name")`.
  4. **Data Attributes** - Stable and semantic. Use `By.cssSelector("[data-test='value']")`.
  5. **Class Name** - Returns multiple elements, not unique, use only for lists or collections.
  6. **XPath** - Slower than CSS, but powerful. Use `By.xpath("//tag[@attribute='value']")` only when necessary.
  
  **Best Practices:** 
  - Always prioritize ID → CSS Selector → Data Attributes → XPath
  - Avoid absolute XPath with indexes (fragile)
  - Use explicit waits (WebDriverWait) instead of Thread.sleep()
  - Create Page Objects to encapsulate locators
  - Use Chrome DevTools to inspect elements and test selectors
  - Prefer `data-test` attributes for automation-specific element identification
- **Chrome Options for Test Automation:**
  - Use `--incognito` mode to avoid saved passwords and cookies
  - Add `--disable-save-password-bubble` to prevent password save popups
  - Use `--start-maximized` to ensure consistent viewport
  - Consider `--headless` for CI/CD environments
- To check Maven version, run `mvn -version`.
- To compile the project without running tests, use `mvn clean compile`.
