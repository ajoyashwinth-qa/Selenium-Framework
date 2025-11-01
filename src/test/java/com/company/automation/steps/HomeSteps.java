package com.company.automation.steps;

import com.company.automation.core.DriverFactory;
import com.company.automation.pages.HomePage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import org.openqa.selenium.WebDriver;
import org.testng.Reporter;

public class HomeSteps {
    private WebDriver driver;
    private HomePage homePage;

    @Given("I open the homepage")
    public void i_open_the_homepage() {
        driver = DriverFactory.getDriver();
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
        String heading = homePage.getHeadingText();
        Assert.assertTrue(heading != null && !heading.isEmpty(), "Heading should not be empty");
    }
}