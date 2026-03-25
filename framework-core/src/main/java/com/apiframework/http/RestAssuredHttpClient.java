package com.apiframework.http;

import com.apiframework.json.JacksonProvider;
import com.apiframework.model.ApiResponse;
import com.apiframework.model.HttpRetryPolicy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Core HTTP client implementation backed by REST Assured.
 * REST Assured is a non-replaceable foundation of this framework.
 */
public final class RestAssuredHttpClient implements HttpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestAssuredHttpClient.class);

    private final RequestSpecification baseSpec;
    private final HttpRetryPolicy defaultRetryPolicy;
    private final ObjectMapper objectMapper;

    public RestAssuredHttpClient(RequestSpecification baseSpec, HttpRetryPolicy defaultRetryPolicy) {
        this(baseSpec, defaultRetryPolicy, JacksonProvider.defaultMapper());
    }

    public RestAssuredHttpClient(
        RequestSpecification baseSpec,
        HttpRetryPolicy defaultRetryPolicy,
        ObjectMapper objectMapper
    ) {
        this.baseSpec = baseSpec;
        this.defaultRetryPolicy = defaultRetryPolicy;
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> ApiResponse<T> get(String path, Class<T> responseType) {
        return get(path, Map.of(), responseType);
    }

    @Override
    public <T> ApiResponse<T> get(String path, Map<String, ?> queryParams, Class<T> responseType) {
        return executeWithRetry(path, "GET", null, queryParams, responseType);
    }

    @Override
    public <T> ApiResponse<T> post(String path, Object body, Class<T> responseType) {
        return executeWithRetry(path, "POST", body, Map.of(), responseType);
    }

    @Override
    public <T> ApiResponse<T> put(String path, Object body, Class<T> responseType) {
        return executeWithRetry(path, "PUT", body, Map.of(), responseType);
    }

    @Override
    public <T> ApiResponse<T> patch(String path, Object body, Class<T> responseType) {
        return executeWithRetry(path, "PATCH", body, Map.of(), responseType);
    }

    @Override
    public <T> ApiResponse<T> delete(String path, Class<T> responseType) {
        return executeWithRetry(path, "DELETE", null, Map.of(), responseType);
    }

    @Override
    public ApiResponse<String> getRaw(String path) {
        return getRaw(path, Map.of());
    }

    @Override
    public ApiResponse<String> getRaw(String path, Map<String, ?> queryParams) {
        return executeWithRetry(path, "GET", null, queryParams, String.class);
    }

    // --- internals ---

    private <T> ApiResponse<T> executeWithRetry(
        String path, String method, Object body, Map<String, ?> queryParams, Class<T> responseType
    ) {
        for (int attempt = 1; attempt <= defaultRetryPolicy.maxAttempts(); attempt++) {
            Response response = executeOnce(path, method, body, queryParams);
            String rawBody = response.getBody() == null ? "" : response.getBody().asString();

            boolean shouldRetry = attempt < defaultRetryPolicy.maxAttempts()
                && defaultRetryPolicy.retryableStatusCodes().contains(response.statusCode());

            if (shouldRetry) {
                LOGGER.warn("HTTP retry attempt {}/{} due to status {}", attempt, defaultRetryPolicy.maxAttempts(), response.statusCode());
                sleepQuietly(defaultRetryPolicy.delayBetweenAttempts().toMillis());
                continue;
            }

            return toApiResponse(response, rawBody, responseType);
        }

        throw new IllegalStateException("Exhausted HTTP retries without a final response");
    }

    private Response executeOnce(String path, String method, Object body, Map<String, ?> queryParams) {
        Objects.requireNonNull(method, "HTTP method must not be null");
        Objects.requireNonNull(path, "Request path must not be null");

        RequestSpecification spec = RestAssured
            .given()
            .spec(new RequestSpecBuilder().addRequestSpecification(baseSpec).build());

        if (queryParams != null && !queryParams.isEmpty()) {
            spec.queryParams(queryParams);
        }

        if (body != null) {
            spec.body(body);
        }

        return switch (method) {
            case "GET" -> spec.get(path);
            case "POST" -> spec.post(path);
            case "PUT" -> spec.put(path);
            case "PATCH" -> spec.patch(path);
            case "DELETE" -> spec.delete(path);
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        };
    }

    private <T> ApiResponse<T> toApiResponse(Response response, String rawBody, Class<T> responseType) {
        T body = deserializeBody(rawBody, responseType);
        Map<String, String> headers = new LinkedHashMap<>();
        response.getHeaders().asList().forEach(header -> headers.put(header.getName(), header.getValue()));
        String correlationId = headers.get("X-Correlation-Id");

        return new ApiResponse<>(
            response.statusCode(),
            headers,
            body,
            response.time(),
            correlationId,
            rawBody
        );
    }

    private <T> T deserializeBody(String rawBody, Class<T> responseType) {
        if (responseType == String.class) {
            return responseType.cast(rawBody);
        }
        if (rawBody == null || rawBody.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(rawBody, responseType);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to deserialize response body", exception);
        }
    }

    private void sleepQuietly(long millis) {
        if (millis <= 0) {
            return;
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Retry sleep interrupted", interruptedException);
        }
    }
}
