package com.apiframework.config;

public record FrameworkRuntimeConfig(
    String profile,
    String env,
    String baseUrl,
    int connectTimeoutMs,
    int readTimeoutMs
) {
}
