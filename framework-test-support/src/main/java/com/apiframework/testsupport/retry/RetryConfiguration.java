package com.apiframework.testsupport.retry;

public final class RetryConfiguration {
    private RetryConfiguration() {
    }

    public static RetryRuntimePolicy globalPolicy() {
        int maxRetries = Integer.parseInt(
            System.getProperty("test.retry.maxRetries", System.getProperty("test.retry.maxAttempts", "1"))
        );
        long delayMs = Long.parseLong(System.getProperty("test.retry.delayMs", "0"));
        return new RetryRuntimePolicy(maxRetries, delayMs);
    }

    public static RetryPredicate retryPredicate() {
        return new DefaultRetryPredicate();
    }

    public static RetryDelayStrategy retryDelayStrategy() {
        return new FixedRetryDelayStrategy();
    }
}
