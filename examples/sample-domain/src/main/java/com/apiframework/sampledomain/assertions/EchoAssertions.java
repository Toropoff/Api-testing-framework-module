package com.apiframework.sampledomain.assertions;

import com.apiframework.core.model.ApiResponse;
import com.apiframework.sampledomain.model.EchoGetResponse;
import com.apiframework.sampledomain.model.EchoPostResponse;

import static org.assertj.core.api.Assertions.assertThat;

public final class EchoAssertions {
    private EchoAssertions() {
    }

    public static void assertGetEcho(ApiResponse<EchoGetResponse> response, String key, String value) {
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotNull();
        assertThat(response.body().args()).containsEntry(key, value);
    }

    public static void assertPostEcho(ApiResponse<EchoPostResponse> response, String event, int amount) {
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotNull();
        assertThat(response.body().json()).isNotNull();
        assertThat(response.body().json().event()).isEqualTo(event);
        assertThat(response.body().json().amount()).isEqualTo(amount);
    }
}
