package com.apiframework.config;

import com.apiframework.model.HttpRetryPolicy;

public record FrameworkRuntimeConfig(
    String profile,
    String env,
    int connectTimeoutMs,
    int readTimeoutMs,
    HttpRetryPolicy httpRetryPolicy
) {
}
