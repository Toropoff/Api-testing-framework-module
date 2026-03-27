package com.apiframework.reporting.allure;

import com.apiframework.testsupport.base.BaseApiTest;
import com.apiframework.testsupport.base.TestExecutionContext;
import com.apiframework.testsupport.retry.FrameworkRetryAnalyzer;
import io.qameta.allure.Allure;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

/**
 * Test lifecycle reporting listener for Allure.
 * <p>
 * Responsible for test-level labels, retry metadata and failure stacktrace.
 * <p>
 * Not responsible for per-request HTTP step creation or request/response attachments
 * (these belong to the HTTP reporting filter layer).
 */
public final class AllureTestNgListener implements ITestListener {
    @Override
    public void onStart(ITestContext context) {
        RestAssured.filters(new AllureRestAssured());
    }

    @Override
    public void onTestStart(ITestResult result) {
        attachContextLabels(result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        attachRetryMetadata(result);
        if (result.getThrowable() != null) {
            Allure.addAttachment("Failure stacktrace", "text/plain", stackTraceOf(result.getThrowable()), ".txt");
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

    private void attachContextLabels(ITestResult result) {
        Object contextAttribute = result.getAttribute(BaseApiTest.TEST_CONTEXT_ATTRIBUTE);
        if (!(contextAttribute instanceof TestExecutionContext context)) {
            return;
        }

        Allure.label("testId", context.testId());
        Allure.label("correlationId", context.correlationId());
        Allure.label("startedAt", context.startedAt().toString());

        for (Map.Entry<String, String> tag : context.environmentTags().entrySet()) {
            Allure.label(tag.getKey(), tag.getValue());
        }
    }

    private String stackTraceOf(Throwable throwable) {
        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
