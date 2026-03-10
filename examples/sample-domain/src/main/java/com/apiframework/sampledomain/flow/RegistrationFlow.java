package com.apiframework.sampledomain.flow;

import com.apiframework.apimodel.dto.auth.LoginRequest;
import com.apiframework.apimodel.dto.auth.TokenResponse;
import com.apiframework.apimodel.dto.user.CreateUserRequest;
import com.apiframework.apimodel.dto.user.UserResponse;
import com.apiframework.sampledomain.endpoint.AuthApi;
import com.apiframework.sampledomain.endpoint.UserApi;
import com.apiframework.core.model.ApiResponse;

public final class RegistrationFlow {
    private final AuthApi authApi;
    private final UserApi userApi;

    public RegistrationFlow(AuthApi authApi, UserApi userApi) {
        this.authApi = authApi;
        this.userApi = userApi;
    }

    public ApiResponse<UserResponse> registerUser(LoginRequest loginRequest, CreateUserRequest createUserRequest) {
        ApiResponse<TokenResponse> tokenResponse = authApi.login(loginRequest);
        if (tokenResponse.statusCode() >= 400) {
            throw new IllegalStateException("Unable to authenticate for registration flow");
        }
        return userApi.createUser(createUserRequest);
    }
}
