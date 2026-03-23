package com.apiframework.core.config;

import com.apiframework.core.model.HttpRetryPolicy;

public record FrameworkRuntimeConfig(
    String profile,
    int connectTimeoutMs,
    int readTimeoutMs,
    HttpRetryPolicy httpRetryPolicy,
    String basicAuthUsername,
    String basicAuthPassword
) {
}
