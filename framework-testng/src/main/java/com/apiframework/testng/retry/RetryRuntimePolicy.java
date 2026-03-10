package com.apiframework.testng.retry;

public record RetryRuntimePolicy(int maxAttempts, long delayMs) {
    public RetryRuntimePolicy {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be >= 1");
        }
        if (delayMs < 0) {
            throw new IllegalArgumentException("delayMs must be >= 0");
        }
    }
}
