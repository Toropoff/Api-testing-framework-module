package com.apiframework.sampledomain.assertions;

import com.apiframework.apimodel.dto.order.OrderResponse;
import com.apiframework.core.model.ApiResponse;

import static org.assertj.core.api.Assertions.assertThat;

public final class OrderAssertions {
    private OrderAssertions() {
    }

    public static void assertOrderStatus(ApiResponse<OrderResponse> response, String expectedStatus) {
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotNull();
        assertThat(response.body().status()).isEqualToIgnoringCase(expectedStatus);
    }
}
