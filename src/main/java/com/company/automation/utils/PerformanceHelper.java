package com.company.automation.utils;

import com.company.automation.core.DriverFactory;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;

import java.util.HashMap;
import java.util.Map;

public class PerformanceHelper {
    @SuppressWarnings("unchecked")
    public static Map<String, Object> collectPerformanceMetrics() {
        ChromeDriver driver = DriverFactory.getChromeDriver();
        Map<String, Object> result = new HashMap<>();
        if (driver == null) return result;
        try {
            DevTools dt = DriverFactory.getDevTools();
            if (dt != null) {
                try {
                    Map<String, Object> metrics = driver.executeCdpCommand("Performance.getMetrics", new HashMap<>());
                    result.putAll(metrics);
                } catch (Exception e) {
                    result.put("cdp_error", e.getMessage());
                }
            }
            // fallback: collect Navigation Timing / performance entries via JS
            try {
                Object timings = driver.executeScript("return (window.performance && (window.performance.timing || window.performance.toJSON)) ? (window.performance.timing ? window.performance.timing : window.performance.toJSON()) : {};");
                result.put("timing", timings);

                Object entries = driver.executeScript("return (window.performance && window.performance.getEntries) ? window.performance.getEntries() : [];");
                result.put("entries", entries);
            } catch (Exception e) {
                result.put("timing_error", e.getMessage());
            }
        } catch (Exception e) {
            result.put("error", e.getMessage());
        }
        return result;
    }
}