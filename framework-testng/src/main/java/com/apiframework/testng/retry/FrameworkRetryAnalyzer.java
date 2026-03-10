package com.apiframework.testng.retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.lang.reflect.Method;

public final class FrameworkRetryAnalyzer implements IRetryAnalyzer {
    public static final String RETRY_ATTEMPT_ATTRIBUTE = "framework.retry.attempt";

    private static final Logger LOGGER = LoggerFactory.getLogger(FrameworkRetryAnalyzer.class);

    private int failedAttemptCount;

    @Override
    public boolean retry(ITestResult result) {
        RetryRuntimePolicy policy = resolvePolicy(result);
        failedAttemptCount++;

        if (failedAttemptCount < policy.maxAttempts()) {
            int nextRunNumber = failedAttemptCount + 1;
            result.setAttribute(RETRY_ATTEMPT_ATTRIBUTE, failedAttemptCount);
            LOGGER.warn(
                "Retrying test {}. Attempt {} of {}",
                result.getName(),
                nextRunNumber,
                policy.maxAttempts()
            );
            sleepQuietly(policy.delayMs());
            return true;
        }

        LOGGER.error("Retry limit reached for test {}. Attempts: {}", result.getName(), policy.maxAttempts());
        return false;
    }

    private RetryRuntimePolicy resolvePolicy(ITestResult result) {
        RetryRuntimePolicy global = RetryConfiguration.globalPolicy();

        Method method = result.getMethod().getConstructorOrMethod().getMethod();
        if (method != null) {
            RetrySetting methodPolicy = method.getAnnotation(RetrySetting.class);
            if (methodPolicy != null) {
                return merge(global, methodPolicy);
            }
        }

        RetrySetting classPolicy = result.getTestClass().getRealClass().getAnnotation(RetrySetting.class);
        if (classPolicy != null) {
            return merge(global, classPolicy);
        }

        return global;
    }

    private RetryRuntimePolicy merge(RetryRuntimePolicy global, RetrySetting override) {
        int maxAttempts = override.maxAttempts() > 0 ? override.maxAttempts() : global.maxAttempts();
        long delay = override.delayMs() >= 0 ? override.delayMs() : global.delayMs();
        return new RetryRuntimePolicy(maxAttempts, delay);
    }

    private void sleepQuietly(long delayMs) {
        if (delayMs <= 0) {
            return;
        }
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Retry delay interrupted", interruptedException);
        }
    }
}
