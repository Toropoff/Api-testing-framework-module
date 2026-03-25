package com.apiframework.config;

public record FrameworkRuntimeConfig(
    String profile,
    String env,
    int connectTimeoutMs,
    int readTimeoutMs
) {
}
