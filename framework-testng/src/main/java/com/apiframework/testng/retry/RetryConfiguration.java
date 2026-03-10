package com.apiframework.testng.retry;

public final class RetryConfiguration {
    private RetryConfiguration() {
    }

    public static RetryRuntimePolicy globalPolicy() {
        int maxAttempts = Integer.parseInt(System.getProperty("test.retry.maxAttempts", "2"));
        long delayMs = Long.parseLong(System.getProperty("test.retry.delayMs", "0"));
        return new RetryRuntimePolicy(maxAttempts, delayMs);
    }
}
