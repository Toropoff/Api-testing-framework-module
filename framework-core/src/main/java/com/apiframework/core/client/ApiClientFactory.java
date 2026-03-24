package com.apiframework.core.client;

import com.apiframework.core.auth.AuthStrategy;
import com.apiframework.core.config.FrameworkRuntimeConfig;
import com.apiframework.core.filter.HttpFilterPolicy;
import com.apiframework.core.http.HttpClient;
import com.apiframework.core.http.RestAssuredHttpClient;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.Filter;
import io.restassured.http.ContentType;

/**
 * Factory that assembles the HTTP client stack: RequestSpecification (timeouts, filters,
 * content type) + AuthStrategy + RetryPolicy &rarr; RestAssuredHttpClient.
 */
public final class ApiClientFactory {
    private ApiClientFactory() {
    }

    public static HttpClient create(String baseUrl, FrameworkRuntimeConfig config, AuthStrategy authStrategy, HttpFilterPolicy filterPolicy) {
        RestAssuredConfig restAssuredConfig = RestAssuredConfig.config().httpClient(
            HttpClientConfig.httpClientConfig()
                .setParam("http.connection.timeout", config.connectTimeoutMs())
                .setParam("http.socket.timeout", config.readTimeoutMs())
        );

        RequestSpecBuilder specBuilder = new RequestSpecBuilder()
            .setBaseUri(baseUrl)
            .setContentType(ContentType.JSON)
            .setConfig(restAssuredConfig);

        for (Filter filter : filterPolicy.filters()) {
            specBuilder.addFilter(filter);
        }

        return new RestAssuredHttpClient(specBuilder.build(), authStrategy, config.httpRetryPolicy());
    }
}
