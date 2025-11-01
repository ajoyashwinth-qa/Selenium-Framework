package com.company.automation.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.company.automation"},
        // Default to @smoke; can be overridden with -Dcucumber.filter.tags
        tags = "@smoke",
        plugin = {
                "pretty",
                "html:target/cucumber-reports.html",
                "json:target/cucumber-report.json",
                "junit:target/junit-reports/Cucumber.xml"
        }
        // Note: Use -Dcucumber.filter.tags="@regression" to run tagged scenarios
)
public class TestRunner extends AbstractTestNGCucumberTests {
}