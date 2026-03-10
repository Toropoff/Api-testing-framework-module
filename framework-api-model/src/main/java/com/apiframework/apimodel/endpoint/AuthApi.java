package com.apiframework.apimodel.endpoint;

import com.apiframework.apimodel.dto.auth.LoginRequest;
import com.apiframework.apimodel.dto.auth.TokenResponse;
import com.apiframework.core.http.HttpClient;
import com.apiframework.core.model.ApiRequest;
import com.apiframework.core.model.ApiResponse;
import com.apiframework.core.model.HttpMethod;

public final class AuthApi extends BaseApiEndpoint {
    public AuthApi(HttpClient httpClient) {
        super(httpClient);
    }

    public ApiResponse<TokenResponse> login(LoginRequest request) {
        ApiRequest<LoginRequest> apiRequest = ApiRequest.<LoginRequest>builder(HttpMethod.POST, "/api/v1/auth/login")
            .body(request)
            .build();
        return httpClient.execute(apiRequest, TokenResponse.class);
    }
}
