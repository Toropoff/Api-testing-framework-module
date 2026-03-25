package com.apiframework.config;

public record FrameworkRuntimeConfig(
    String profile,
    String env,
    String rootUrl,
    int connectTimeoutMs,
    int readTimeoutMs
) {
}
