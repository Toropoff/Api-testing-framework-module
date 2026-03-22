package com.apiframework.core.config;

import com.apiframework.core.model.HttpRetryPolicy;

public record FrameworkRuntimeConfig(
    EnvironmentProfile profile,
    int connectTimeoutMs,
    int readTimeoutMs,
    HttpRetryPolicy httpRetryPolicy,
    String basicAuthUsername,
    String basicAuthPassword
) {
}
