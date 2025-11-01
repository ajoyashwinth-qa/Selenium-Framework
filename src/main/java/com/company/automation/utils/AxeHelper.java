package com.company.automation.utils;

import com.company.automation.core.DriverFactory;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class AxeHelper {

    // CDN fallback version (adjustable)
    private static final String AXE_CDN = "https://cdnjs.cloudflare.com/ajax/libs/axe-core/4.6.5/axe.min.js";

    public static void runAxeScan(String name) {
        WebDriver driver = DriverFactory.getDriver();
        if (driver == null) return;
        try {
            // Load axe script from classpath (src/main/resources/axe.min.js)
            String axeSource = null;
            InputStream is = AxeHelper.class.getResourceAsStream("/axe.min.js");
            if (is != null) {
                try (Scanner s = new Scanner(is, StandardCharsets.UTF_8.name())) {
                    s.useDelimiter("\\A");
                    axeSource = s.hasNext() ? s.next() : "";
                }
            }

            // If not bundled, try to download from CDN
            if (axeSource == null || axeSource.isEmpty()) {
                try {
                    URL url = new URL(AXE_CDN);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(10000);
                    try (InputStream in = conn.getInputStream(); Scanner s = new Scanner(in, StandardCharsets.UTF_8.name())) {
                        s.useDelimiter("\\A");
                        axeSource = s.hasNext() ? s.next() : "";
                    }
                    System.out.println("Downloaded axe.js from CDN: " + AXE_CDN);
                } catch (Exception ex) {
                    System.err.println("Failed to download axe.min.js from CDN: " + ex.getMessage());
                }
            }

            if (axeSource == null || axeSource.isEmpty()) {
                System.err.println("axe.min.js not available; skipping axe scan");
                return;
            }

            ChromeDriver chrome = (ChromeDriver) driver;
            // Inject axe source first (synchronously)
            chrome.executeScript(axeSource);

            // Run axe.run() as an async script and pass results to the Selenium async callback
            String asyncScript = "var callback = arguments[arguments.length - 1];"
                    + "if(!window.axe){ callback(JSON.stringify({error: 'axe not available'})); } else {"
                    + " window.axe.run().then(function(results){ callback(JSON.stringify(results)); }).catch(function(err){ callback(JSON.stringify({error: err.toString()})); }); }";

            Object raw = chrome.executeAsyncScript(asyncScript);
            JSONObject results;
            if (raw instanceof String) {
                try {
                    results = new JSONObject((String) raw);
                } catch (Exception ex) {
                    // raw may already be a map-like object
                    results = new JSONObject();
                    results.put("result", String.valueOf(raw));
                }
            } else {
                results = new JSONObject(raw);
            }

            File report = new File("target/axe-report-" + name + ".json");
            report.getParentFile().mkdirs();
            try (FileWriter fw = new FileWriter(report)) {
                fw.write(results.toString(2));
            }
            System.out.println("Axe results written to: " + report.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed to run axe scan: " + e.getMessage());
        }
    }
}