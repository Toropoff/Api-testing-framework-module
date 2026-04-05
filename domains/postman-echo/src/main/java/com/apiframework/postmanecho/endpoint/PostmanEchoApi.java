package com.apiframework.domains.postmanecho.endpoint;

import com.apiframework.config.DomainConfig;
import com.apiframework.http.HttpClient;
import com.apiframework.model.ApiResponse;
import com.apiframework.domains.postmanecho.model.EchoGetResponse;
import com.apiframework.domains.postmanecho.model.EchoPayload;
import com.apiframework.domains.postmanecho.model.EchoPostResponse;

import java.util.Map;
import java.util.Objects;

public final class PostmanEchoApi {
    private static final String BASE_PATH = DomainConfig.loadBasePath(
        PostmanEchoApi.class,
        "postman-echo.properties",
        System.getenv("FRAMEWORK_ENV") != null
            ? System.getenv("FRAMEWORK_ENV")
            : System.getProperty("framework.env", "dev")
    );

    public static String basePath() {
        return BASE_PATH;
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
