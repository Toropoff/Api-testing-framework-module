package com.apiframework.client;

import com.apiframework.config.ConfigResolver;
import com.apiframework.config.FrameworkRuntimeConfig;
import com.apiframework.http.CorrelationIdFilter;
import com.apiframework.http.HttpClient;
import com.apiframework.http.RestAssuredHttpClient;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.preemptive;

/**
 * Factory that assembles the HTTP client stack: RequestSpecification (timeouts, filters,
 * content type, credentials) → RestAssuredHttpClient.
 * URL composition is handled by UrlResolver at request send time — this factory does not bake
 * any URL into the client.
 */
public final class ApiClientFactory {
    private ApiClientFactory() {
    }

    public static HttpClient build() {
        FrameworkRuntimeConfig config = ConfigResolver.resolveFromSystem();

        RestAssuredConfig restAssuredConfig = RestAssuredConfig.config().httpClient(
            HttpClientConfig.httpClientConfig()
                .setParam("http.connection.timeout", config.connectTimeoutMs())
                .setParam("http.socket.timeout", config.readTimeoutMs())
        );

        RequestSpecBuilder specBuilder = new RequestSpecBuilder()
            .setContentType(ContentType.JSON)
            .setConfig(restAssuredConfig)
            .addFilter(new CorrelationIdFilter());

        String clientName = System.getenv("FRAMEWORK_CLIENT_NAME");
        String clientSecret = System.getenv("FRAMEWORK_CLIENT_SECRET");
        if (clientName != null && !clientName.isBlank()
                && clientSecret != null && !clientSecret.isBlank()) {
            specBuilder.setAuth(preemptive().basic(clientName, clientSecret));
        }

        return new RestAssuredHttpClient(specBuilder.build());
    }
}
