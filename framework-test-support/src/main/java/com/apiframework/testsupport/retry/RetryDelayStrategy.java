package com.apiframework.testsupport.retry;

@FunctionalInterface
public interface RetryDelayStrategy {
    long delayBeforeNextAttemptMs(int retryAttempt, RetryRuntimePolicy policy);
}
