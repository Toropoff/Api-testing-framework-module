package com.apiframework.sampledomain.endpoint;

import com.apiframework.apimodel.dto.user.CreateUserRequest;
import com.apiframework.apimodel.dto.user.UserResponse;
import com.apiframework.core.http.HttpClient;
import com.apiframework.core.model.ApiRequest;
import com.apiframework.core.model.ApiResponse;
import com.apiframework.core.model.HttpMethod;

public final class UserApi extends BaseApiEndpoint {
    public UserApi(HttpClient httpClient) {
        super(httpClient);
    }

    public ApiResponse<UserResponse> createUser(CreateUserRequest request) {
        ApiRequest<CreateUserRequest> apiRequest = ApiRequest.<CreateUserRequest>builder(HttpMethod.POST, "/api/v1/users")
            .body(request)
            .build();
        return httpClient.execute(apiRequest, UserResponse.class);
    }

    public ApiResponse<UserResponse> getUserById(long userId) {
        ApiRequest<Void> apiRequest = ApiRequest.<Void>builder(HttpMethod.GET, "/api/v1/users/" + userId)
            .build();
        return httpClient.execute(apiRequest, UserResponse.class);
    }
}
