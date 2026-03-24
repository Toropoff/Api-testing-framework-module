package com.apiframework.client;

import com.apiframework.auth.AuthStrategy;
import com.apiframework.config.FrameworkRuntimeConfig;
import com.apiframework.http.HttpClient;
import com.apiframework.http.RestAssuredHttpClient;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;

import java.util.UUID;

/**
 * Factory that assembles the HTTP client stack: RequestSpecification (timeouts, filters,
 * content type) + AuthStrategy + RetryPolicy &rarr; RestAssuredHttpClient.
 */
public final class ApiClientFactory {
    private ApiClientFactory() {
    }

    public static HttpClient create(String baseUrl, FrameworkRuntimeConfig config, AuthStrategy authStrategy) {
        RestAssuredConfig restAssuredConfig = RestAssuredConfig.config().httpClient(
            HttpClientConfig.httpClientConfig()
                .setParam("http.connection.timeout", config.connectTimeoutMs())
                .setParam("http.socket.timeout", config.readTimeoutMs())
        );

        RequestSpecBuilder specBuilder = new RequestSpecBuilder()
            .setBaseUri(baseUrl)
            .setContentType(ContentType.JSON)
            .setConfig(restAssuredConfig)
            .addHeader("X-Correlation-Id", UUID.randomUUID().toString());

        return new RestAssuredHttpClient(specBuilder.build(), authStrategy, config.httpRetryPolicy());
    }
}
