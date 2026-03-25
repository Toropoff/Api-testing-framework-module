package com.apiframework.config;

/**
 * Lightweight config resolver for dev/local use.
 * Reads all values directly from system properties with sensible defaults.
 * No property files, no secrets providers.
 */
public final class ConfigResolver {
    private ConfigResolver() {
    }

    public static FrameworkRuntimeConfig resolveFromSystem() {
        String env = System.getenv("FRAMEWORK_ENV");
        if (env == null || env.isBlank()) {
            env = System.getProperty("framework.env", "dev");
        }

        return new FrameworkRuntimeConfig(
            System.getProperty("framework.profile", "dev"),
            env,
            Integer.getInteger("http.connectTimeoutMs", 5000),
            Integer.getInteger("http.readTimeoutMs", 15000)
        );
    }
}
