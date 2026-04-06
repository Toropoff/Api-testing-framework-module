package com.apiframework.postmanecho.endpoint;

import com.apiframework.config.DomainConfig;
import com.apiframework.http.HttpClient;
import com.apiframework.model.ApiResponse;
import com.apiframework.postmanecho.model.EchoGetResponse;
import com.apiframework.postmanecho.model.EchoPayload;
import com.apiframework.postmanecho.model.EchoPostResponse;

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

    private static final String API_NAME = DomainConfig.loadApiName(
        PostmanEchoApi.class,
        "postman-echo.properties"
    );

    public static String basePath() {
        return BASE_PATH;
    }

    public static String displayApiName() {
        return API_NAME;
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
