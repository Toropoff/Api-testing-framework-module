package com.apiframework.domains.postmanecho.endpoint;

import com.apiframework.core.http.HttpClient;
import com.apiframework.core.model.ApiResponse;
import com.apiframework.domains.postmanecho.model.EchoGetResponse;
import com.apiframework.domains.postmanecho.model.EchoPayload;
import com.apiframework.domains.postmanecho.model.EchoPostResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public final class PostmanEchoApi {
    private static final String BASE_URL;

    static {
        Properties props = new Properties();
        try (InputStream in = PostmanEchoApi.class.getClassLoader().getResourceAsStream("postman-echo.properties")) {
            if (in == null) {
                throw new IllegalStateException("postman-echo.properties not found on classpath");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load postman-echo.properties", e);
        }
        BASE_URL = Objects.requireNonNull(props.getProperty("baseUrl"), "baseUrl not set in postman-echo.properties");
    }

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
