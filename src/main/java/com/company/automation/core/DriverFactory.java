package com.company.automation.core;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;

public class DriverFactory {
    private static final ThreadLocal<ChromeDriver> driver = new ThreadLocal<>();
    private static final ThreadLocal<DevTools> devTools = new ThreadLocal<>();

    public static void initDriver() {
        if (driver.get() == null) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();

            // Allow disabling headless via environment variable HEADLESS=false (or 0/no)
            String headlessEnv = System.getenv("HEADLESS");
            boolean useHeadless = true;
            if (headlessEnv != null) {
                if (headlessEnv.equalsIgnoreCase("false") || headlessEnv.equals("0") || headlessEnv.equalsIgnoreCase("no")) {
                    useHeadless = false;
                }
            }

            if (useHeadless) {
                // Use new headless when supported
                options.addArguments("--headless=new");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                // Additional flags to increase reliability in CI containers
                options.addArguments("--disable-gpu");
                options.addArguments("--disable-software-rasterizer");
                options.addArguments("--remote-allow-origins=*");
            } else {
                // Useful defaults for visible runs
                options.addArguments("--start-maximized");
            }

            // Allow specifying a custom Chrome binary via CHROME_BINARY env var
            String chromeBinary = System.getenv("CHROME_BINARY");
            if (chromeBinary != null && !chromeBinary.isEmpty()) {
                options.setBinary(chromeBinary);
            }

            ChromeDriver chrome = new ChromeDriver(options);

            // Log some helpful info to make CI debugging easier
            try {
                String browserVersion = (chrome.getCapabilities().getBrowserVersion() != null)
                        ? chrome.getCapabilities().getBrowserVersion()
                        : "unknown";
                System.out.println("[DriverFactory] Chrome binary: " + (chromeBinary != null ? chromeBinary : "(default)") + ", browserVersion=" + browserVersion);
            } catch (Exception e) {
                System.out.println("[DriverFactory] Unable to determine browser version: " + e.getMessage());
            }

            // Try to create a DevTools session, but don't fail the driver if it isn't available
            try {
                DevTools dt = chrome.getDevTools();
                dt.createSession();
                devTools.set(dt);
            } catch (Exception e) {
                System.err.println("DevTools/CDP session not available: " + e.getMessage());
                // leave devTools as null; tests will continue without CDP
            }
            driver.set(chrome);
        }
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static ChromeDriver getChromeDriver() {
        return driver.get();
    }

    public static DevTools getDevTools() { return devTools.get(); }

    public static void quitDriver() {
        try {
            if (driver.get() != null) {
                driver.get().quit();
                driver.remove();
            }
            if (devTools.get() != null) {
                devTools.remove();
            }
        } catch (Exception e) {
            // ignore
        }
    }
}