package com.apiframework.client;

import com.apiframework.config.FrameworkRuntimeConfig;
import com.apiframework.http.HttpClient;
import com.apiframework.http.RestAssuredHttpClient;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;

import java.util.UUID;

import static io.restassured.RestAssured.preemptive;

/**
 * Factory that assembles the HTTP client stack: RequestSpecification (timeouts, filters,
 * content type, credentials) + RetryPolicy &rarr; RestAssuredHttpClient.
 * Credentials are injected from FRAMEWORK_CLIENT_NAME / FRAMEWORK_CLIENT_SECRET env vars.
 */
public final class ApiClientFactory {
    private ApiClientFactory() {
    }

    public static HttpClient create(String baseUrl, FrameworkRuntimeConfig config) {
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

        String clientName = System.getenv("FRAMEWORK_CLIENT_NAME");
        String clientSecret = System.getenv("FRAMEWORK_CLIENT_SECRET");
        if (clientName != null && !clientName.isBlank()
                && clientSecret != null && !clientSecret.isBlank()) {
            specBuilder.setAuth(preemptive().basic(clientName, clientSecret));
        }

        return new RestAssuredHttpClient(specBuilder.build(), config.httpRetryPolicy());
    }
}
