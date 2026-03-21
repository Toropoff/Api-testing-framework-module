package com.apiframework.testsupport.retry;

import com.apiframework.core.util.ReflectiveFactory;

public final class RetryConfiguration {
    private static final String PREDICATE_CLASS_PROPERTY = "test.retry.predicateClass";
    private static final String DELAY_STRATEGY_CLASS_PROPERTY = "test.retry.delayStrategyClass";

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
        return ReflectiveFactory.instantiateOrDefault(
            System.getProperty(PREDICATE_CLASS_PROPERTY),
            RetryPredicate.class,
            DefaultRetryPredicate::new
        );
    }

    public static RetryDelayStrategy retryDelayStrategy() {
        return ReflectiveFactory.instantiateOrDefault(
            System.getProperty(DELAY_STRATEGY_CLASS_PROPERTY),
            RetryDelayStrategy.class,
            FixedRetryDelayStrategy::new
        );
    }
}
