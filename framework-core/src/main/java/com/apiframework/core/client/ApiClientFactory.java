package com.apiframework.core.client;

import com.apiframework.core.auth.AuthStrategy;
import com.apiframework.core.config.FrameworkRuntimeConfig;
import com.apiframework.core.filter.HttpFilterPolicy;
import com.apiframework.core.http.HttpClient;
import com.apiframework.core.http.RequestSpecFactory;
import com.apiframework.core.http.RestAssuredHttpClient;
import io.restassured.specification.RequestSpecification;

/**
 * Factory that assembles the HTTP client stack: RequestSpecification (timeouts, filters,
 * content type) + AuthStrategy + RetryPolicy &rarr; RestAssuredHttpClient.
 */
public final class ApiClientFactory {
    private ApiClientFactory() {
    }

    public static HttpClient create(String baseUrl, FrameworkRuntimeConfig config, AuthStrategy authStrategy, HttpFilterPolicy filterPolicy) {
        RequestSpecification requestSpecification = new RequestSpecFactory(baseUrl, config, filterPolicy).createBaseSpec();
        return new RestAssuredHttpClient(requestSpecification, authStrategy, config.httpRetryPolicy());
    }
}
