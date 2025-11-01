package com.company.automation.steps;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.Reporter;

import com.company.automation.core.DriverFactory;
import com.company.automation.pages.HomePage;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class HomeSteps {
    private WebDriver driver;
    private HomePage homePage;

    @Given("I open the homepage")
    public void i_open_the_homepage() {
        // Ensure driver is initialized (helps when running steps directly from an IDE)
        if (DriverFactory.getDriver() == null) {
            Reporter.log("Driver not initialized by hooks; initializing DriverFactory now.", true);
            DriverFactory.initDriver();
        }

        driver = DriverFactory.getDriver();
        if (driver == null) {
            throw new IllegalStateException("WebDriver is not available");
        }

        String baseUrl = System.getProperty("baseUrl", "https://www.example.com");
        Reporter.log("Opening baseUrl=" + baseUrl, true);
        driver.get(baseUrl);
        homePage = new HomePage(driver);
    }

    @When("I read the main heading")
    public void i_read_the_main_heading() {
        // no operation here; reading is validated in the next step
    }

    @Then("I should see a non-empty heading")
    public void i_should_see_a_non_empty_heading() {
        if (homePage == null) {
            // If homePage wasn't initialized for some reason, try to initialize it
            driver = DriverFactory.getDriver();
            if (driver == null) {
                throw new IllegalStateException("WebDriver is not available for validation step");
            }
            homePage = new HomePage(driver);
        }

        String heading = homePage.getHeadingText();
        Assert.assertTrue(heading != null && !heading.isEmpty(), "Heading should not be empty");
    }
}