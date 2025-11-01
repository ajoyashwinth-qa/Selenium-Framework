# Selenium + Cucumber + TestNG Boilerplate

A minimal, pragmatic scaffold for UI testing with Selenium 4, Cucumber (TestNG), and WebDriverManager. Includes optional accessibility (axe-core) and basic performance collection.

Tech stack
- Java 11+
- Maven (via Maven Wrapper: mvnw/mvnw.cmd)
- Selenium 4, WebDriverManager, Cucumber 7 (TestNG runner)
- axe-core injected at runtime (src/main/resources/axe.min.js)
- Basic performance metrics via Chrome DevTools Protocol (optional)

Project layout
- pom.xml
- testng.xml
- src/
  - main/java/com/company/automation/
    - core/ (DriverFactory, BasePage)
    - pages/ (example: HomePage)
    - utils/ (AxeHelper, PerformanceHelper)
  - main/resources/axe.min.js
  - test/java/com/company/automation/
    - hooks/ (global Cucumber hooks)
    - steps/ (step definitions)
    - runners/ (Cucumber TestNG runner)
  - test/resources/
    - features/ (feature files)
    - cucumber.properties (Cucumber config)

Quick start (Windows cmd.exe)
1) Ensure Java 11+ is installed and on PATH:

```bat
java -version
```

2) Run the tests from the project root using the Maven Wrapper:

```bat
cd "C:\Users\balac\Desktop\Work\selenium-cucumber-testng-boilerplate"
mvnw.cmd -B test
```

- Headed (visible) Chrome run:

```bat
set HEADLESS=false & mvnw.cmd -B test
```

- Use a custom Chrome binary (optional):

```bat
set CHROME_BINARY=C:\Path\To\chrome.exe & mvnw.cmd -B test
```

Cross-platform
- macOS/Linux: replace mvnw.cmd with ./mvnw

```sh
./mvnw -B test
```

What runs
- Cucumber feature: src/test/resources/features/home.feature
- Steps use Chrome in headless mode by default and navigate to https://www.example.com
- After each scenario, accessibility results are written to target/axe-report-scenario.json
- Cucumber HTML report is at target/cucumber-reports.html
- TestNG/Surefire reports under target/surefire-reports/

Running from Eclipse
- Import as Maven project
- Right-click TestRunner.java → Run As → TestNG Test
- Or run the suite using testng.xml in the project root

Configuration toggles
- HEADLESS: default true; set to false to see the browser
- CHROME_BINARY: set to a Chrome/Chromium executable path when needed
- baseUrl: system property to control the site under test (default https://www.example.com)

Examples (Windows cmd.exe):

```bat
REM Headless default
mvnw.cmd -B -DbaseUrl=https://www.example.com test

REM Visible browser
set HEADLESS=false & mvnw.cmd -B -DbaseUrl=https://www.example.com test
```

Examples (macOS/Linux):

```sh
./mvnw -B -DbaseUrl=https://www.example.com test
HEADLESS=false ./mvnw -B -DbaseUrl=https://www.example.com test
```

Selective runs with tags
- Scenarios can be tagged (e.g., `@smoke`, `@regression`). This project tags the sample scenario with both.
- Use `-Dcucumber.filter.tags` to run a subset.

Examples (Windows cmd.exe):

```bat
REM Run only @smoke
mvnw.cmd -B -Dcucumber.filter.tags="@smoke" test

REM Run anything tagged @regression but not @wip
mvnw.cmd -B -Dcucumber.filter.tags="@regression and not @wip" test
```

Examples (macOS/Linux):

```sh
./mvnw -B -Dcucumber.filter.tags='@smoke' test
./mvnw -B -Dcucumber.filter.tags='@regression and not @wip' test
```

Reports
- Cucumber HTML: `target/cucumber-reports.html`
- Cucumber JSON: `target/cucumber-report.json`
- JUnit XML (for CI aggregation): `target/junit-reports/Cucumber.xml`
- Axe accessibility JSON: `target/axe-report-scenario.json`

Running from IDEs
- Import as a Maven project.
- Run `TestRunner` as a TestNG test.
- Or run the suite using `testng.xml` in the project root.

Notes about DevTools/Performance
- If you see a warning about Selenium DevTools (CDP) version mismatch, it's safe to ignore unless you rely on PerformanceHelper. To enable CDP features explicitly, add a dependency matching your browser major version and Selenium's version, for example:

```xml
<dependency>
  <groupId>org.seleniumhq.selenium</groupId>
  <artifactId>selenium-devtools-vNN</artifactId>
  <version>4.11.0</version>
</dependency>
```

Replace NN with your Chrome major version (e.g., v114). PerformanceHelper already falls back to window.performance if CDP isn’t available.

Troubleshooting
- "'mvn' is not recognized": use the Maven Wrapper (mvnw.cmd) as shown above, or install Maven and ensure it’s on PATH.
- WebDriverManager blocked by proxy: configure your proxy (e.g., HTTPS_PROXY env var) or pre-provision drivers. See WebDriverManager docs for corporate environments.
- Headless issues on certain environments: try headed mode (HEADLESS=false) to diagnose, or keep headless and add options like --disable-gpu as needed (edit DriverFactory).

What I updated in this repo
- Added src/test/resources/cucumber.properties to silence the Cucumber publish banner.
- Added the recommended TestNG DOCTYPE to testng.xml to remove the runtime warning.
- Updated CI to use the Maven Wrapper with Temurin JDK 11 and Maven cache.
- Parameterized base URL via `-DbaseUrl` and wired it into steps.
- Added scenario tags and documented selective runs via `-Dcucumber.filter.tags`.
- Enabled JSON and JUnit outputs from Cucumber for report aggregation.
- Verified the project builds and tests pass locally using mvnw.cmd -B test; reports are generated under target/.

Next steps (optional)
- Expand reports (JSON/HTML) or integrate with reporting dashboards.

CI trigger: 2025-11-01T21:55:00Z