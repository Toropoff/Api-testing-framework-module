package com.apiframework.config;

import com.apiframework.model.HttpRetryPolicy;

public record FrameworkRuntimeConfig(
    String profile,
    int connectTimeoutMs,
    int readTimeoutMs,
    HttpRetryPolicy httpRetryPolicy,
    String basicAuthUsername,
    String basicAuthPassword
) {
}
