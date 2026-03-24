package com.apiframework.config;

import com.apiframework.model.HttpRetryPolicy;

import java.time.Duration;
import java.util.Set;

/**
 * Lightweight config resolver for dev/local use.
 * Reads all values directly from system properties with sensible defaults.
 * No property files, no secrets providers.
 */
public final class ConfigResolver {
    private ConfigResolver() {
    }

    public static FrameworkRuntimeConfig resolveFromSystem() {
        return new FrameworkRuntimeConfig(
            System.getProperty("framework.profile", "dev"),
            Integer.getInteger("http.connectTimeoutMs", 5000),
            Integer.getInteger("http.readTimeoutMs", 15000),
            new HttpRetryPolicy(
                Integer.getInteger("http.retry.maxAttempts", 1),
                Duration.ofMillis(Long.getLong("http.retry.delayMs", 0L)),
                Set.of(429, 500, 502, 503, 504)
            ),
            System.getProperty("auth.basic.username", ""),
            System.getProperty("auth.basic.password", "")
        );
    }
}
