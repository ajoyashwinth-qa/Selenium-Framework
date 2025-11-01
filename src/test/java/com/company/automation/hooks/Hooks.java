package com.company.automation.hooks;

import com.company.automation.core.DriverFactory;
import com.company.automation.utils.AxeHelper;
import com.company.automation.utils.PerformanceHelper;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.testng.Reporter;

import java.util.Map;

public class Hooks {
    @Before
    public void beforeScenario() {
        DriverFactory.initDriver();
    }

    @After
    public void afterScenario() {
        // run accessibility scan
        try {
            AxeHelper.runAxeScan("scenario");
        } catch (Exception e) {
            Reporter.log("Axe scan failed: " + e.getMessage());
        }

        // collect simple perf metrics
        try {
            Map<String, Object> perf = PerformanceHelper.collectPerformanceMetrics();
            Reporter.log("Performance metrics: " + perf.toString());
        } catch (Exception e) {
            Reporter.log("Perf collection failed: " + e.getMessage());
        }

        DriverFactory.quitDriver();
    }
}
