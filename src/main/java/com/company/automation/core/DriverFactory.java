package com.company.automation.core;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

public class DriverFactory {
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final ThreadLocal<DevTools> devTools = new ThreadLocal<>();

    public static void initDriver() {
        if (driver.get() == null) {
            // Allow connecting to a remote Selenium server if SELENIUM_REMOTE_URL is set
            String remoteUrl = System.getenv("SELENIUM_REMOTE_URL");

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

            if (remoteUrl != null && !remoteUrl.isEmpty()) {
                // Use RemoteWebDriver
                try {
                    System.out.println("[DriverFactory] Using remote Selenium URL: " + remoteUrl);
                    DesiredCapabilities caps = new DesiredCapabilities();
                    caps.setCapability(ChromeOptions.CAPABILITY, options);
                    RemoteWebDriver remote = new RemoteWebDriver(new URL(remoteUrl), caps);

                    // DevTools isn't available via RemoteWebDriver in general; skip setting it for remote sessions
                    System.out.println("[DriverFactory] RemoteWebDriver session id: " + remote.getSessionId());

                    driver.set(remote);
                } catch (MalformedURLException e) {
                    throw new RuntimeException("Invalid SELENIUM_REMOTE_URL: " + remoteUrl, e);
                }
            } else {
                // Local ChromeDriver via WebDriverManager
                WebDriverManager.chromedriver().setup();
                ChromeDriver chrome = new ChromeDriver(options);

                // Log some helpful info to make CI debugging easier
                try {
                    String browserVersion = (chrome.getCapabilities().getBrowserVersion() != null)
                            ? chrome.getCapabilities().getBrowserVersion()
                            : "unknown";
                    System.out.println("[DriverFactory] Local Chrome launched, browserVersion=" + browserVersion);
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
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    // Return the underlying ChromeDriver when available (local runs). Returns null for remote sessions.
    public static ChromeDriver getChromeDriver() {
        WebDriver wd = driver.get();
        if (wd instanceof ChromeDriver) {
            return (ChromeDriver) wd;
        }
        return null;
    }

    public static DevTools getDevTools() { return devTools.get(); }

    public static void quitDriver() {
        try {
            if (driver.get() != null) {
                try { driver.get().quit(); } catch (Exception ignored) {}
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