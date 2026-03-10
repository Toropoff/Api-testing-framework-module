package com.apiframework.reporting.allure;

import com.apiframework.testng.retry.FrameworkRetryAnalyzer;
import io.qameta.allure.Allure;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public final class AllureTestNgListener implements ITestListener {
    @Override
    public void onTestSuccess(ITestResult result) {
        attachRetryMetadata(result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        attachRetryMetadata(result);
        if (result.getThrowable() != null) {
            Allure.addAttachment("Failure stacktrace", result.getThrowable().toString());
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        Allure.addAttachment(
            "Test run summary",
            "text/plain",
            "passed=" + context.getPassedTests().size()
                + ", failed=" + context.getFailedTests().size()
                + ", skipped=" + context.getSkippedTests().size(),
            ".txt"
        );
    }

    private void attachRetryMetadata(ITestResult result) {
        Object retry = result.getAttribute(FrameworkRetryAnalyzer.RETRY_ATTEMPT_ATTRIBUTE);
        if (retry != null) {
            Allure.addAttachment("Retry metadata", "retryAttempt=" + retry);
        }
    }
}
