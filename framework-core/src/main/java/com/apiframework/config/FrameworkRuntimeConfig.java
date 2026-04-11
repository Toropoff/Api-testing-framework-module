package com.apiframework.config;

public record FrameworkRuntimeConfig(
    // TODO: profile is unused — no caller reads .profile(); remove field and the System.getProperty("framework.profile") resolution in ConfigResolver
    String profile,
    String env,
    String baseUrl,
    int connectTimeoutMs,
    int readTimeoutMs
) {
}
