package com.apiframework.reporting.allure;

import com.apiframework.config.ConfigResolver;
import com.apiframework.testsupport.retry.FrameworkRetryAnalyzer;
import io.qameta.allure.Allure;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Not responsible for per-request HTTP step creation or request/response attachments
 * (these belong to the HTTP reporting filter layer).
 */
public final class AllureTestNgListener implements ITestListener, ISuiteListener {

    // Registers AllureRestAssured filter globally for REST Assured — captures HTTP request/response as Allure steps for every test in the suite.
    @Override
    public void onStart(ITestContext context) {
        RestAssured.filters(new AllureRestAssured());
    }

    // On test failure: attaches retry metadata (attempt count + reason) and full stacktrace as Allure attachments.
    @Override
    public void onTestFailure(ITestResult result) {
        attachRetryMetadata(result);
        if (result.getThrowable() != null) {
            Allure.addAttachment("Failure stacktrace", "text/plain", stackTraceOf(result.getThrowable()), ".txt");
        }
    }

    // Writes environment.properties and (for local runs) executor.json to the Allure results directory
    // after the suite finishes — populates the Allure Environment and Executors widgets.
    @Override
    public void onFinish(ISuite suite) {
        // outputDir hoisted — shared by both write blocks; each has its own silent catch
        String outputDir = System.getProperty("allure.env.dir", "allure-results");
        try {
            String env = ConfigResolver.resolveFromSystem().env();
            Properties props = new Properties();
            props.setProperty("Environment", env);
            Files.createDirectories(Paths.get(outputDir));
            try (FileWriter writer = new FileWriter(outputDir + "/environment.properties")) {
                props.store(writer, null);
            }
        } catch (Exception e) {
            // silent — environment.properties is non-critical
        }
        if (System.getenv("CI") == null) {
            try {
                Files.createDirectories(Paths.get(outputDir));
                Files.writeString(Paths.get(outputDir, "executor.json"),
                    "{\"name\":\"Local\",\"type\":\"manual\",\"buildName\":\"Local run\"}");
            } catch (Exception e) {
                // silent — executor.json is non-critical
            }
        }
    }

    private void attachRetryMetadata(ITestResult result) {
        Object retry = result.getAttribute(FrameworkRetryAnalyzer.RETRY_ATTEMPT_ATTRIBUTE);
        Object reason = result.getAttribute(FrameworkRetryAnalyzer.RETRY_REASON_ATTRIBUTE);
        if (retry != null || reason != null) {
            Allure.addAttachment("Retry metadata",
                "retryAttempt=" + retry + ", retryReason=" + reason);
        }
    }

    private String stackTraceOf(Throwable throwable) {
        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
