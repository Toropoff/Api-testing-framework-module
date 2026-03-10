package com.apiframework.apimodel.assertions;

import com.apiframework.apimodel.dto.user.UserResponse;
import com.apiframework.core.model.ApiResponse;

import static org.assertj.core.api.Assertions.assertThat;

public final class UserAssertions {
    private UserAssertions() {
    }

    public static void assertUserCreated(ApiResponse<UserResponse> response, String expectedEmail) {
        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.body()).isNotNull();
        assertThat(response.body().id()).isNotNull();
        assertThat(response.body().email()).isEqualTo(expectedEmail);
        assertThat(response.body().status()).isEqualToIgnoringCase("ACTIVE");
    }
}
