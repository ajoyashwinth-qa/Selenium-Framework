package com.company.automation.pages;

import com.company.automation.core.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.WebElement;

public class HomePage extends BasePage {

    @FindBy(tagName = "h1")
    private WebElement heading;

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public String getHeadingText() {
        try {
            return heading.getText();
        } catch (Exception e) {
            return "";
        }
    }
}
