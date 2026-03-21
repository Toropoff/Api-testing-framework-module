package com.apiframework.domains.postmanecho.endpoint;

import com.apiframework.core.config.DomainConfig;
import com.apiframework.core.http.HttpClient;
import com.apiframework.core.model.ApiResponse;
import com.apiframework.domains.postmanecho.model.EchoGetResponse;
import com.apiframework.domains.postmanecho.model.EchoPayload;
import com.apiframework.domains.postmanecho.model.EchoPostResponse;

import java.util.Map;
import java.util.Objects;

public final class PostmanEchoApi {
    private static final String BASE_URL = DomainConfig.loadBaseUrl(PostmanEchoApi.class, "postman-echo.properties");

    public static String baseUrl() {
        return BASE_URL;
    }

    private final HttpClient httpClient;

    public PostmanEchoApi(HttpClient httpClient) {
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient must not be null");
    }

    public ApiResponse<EchoGetResponse> getEcho(String key, String value) {
        return httpClient.get(PostmanEchoRoute.GET_ECHO.path(), Map.of(key, value), EchoGetResponse.class);
    }

    public ApiResponse<EchoPostResponse> postEcho(EchoPayload payload) {
        return httpClient.post(PostmanEchoRoute.POST_ECHO.path(), payload, EchoPostResponse.class);
    }
}
