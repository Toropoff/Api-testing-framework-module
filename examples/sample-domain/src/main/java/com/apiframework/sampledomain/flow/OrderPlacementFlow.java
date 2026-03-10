package com.apiframework.sampledomain.flow;

import com.apiframework.apimodel.dto.order.CreateOrderRequest;
import com.apiframework.apimodel.dto.order.OrderResponse;
import com.apiframework.sampledomain.endpoint.OrderApi;
import com.apiframework.sampledomain.endpoint.UserApi;
import com.apiframework.apimodel.dto.user.UserResponse;
import com.apiframework.core.model.ApiResponse;

public final class OrderPlacementFlow {
    private final UserApi userApi;
    private final OrderApi orderApi;

    public OrderPlacementFlow(UserApi userApi, OrderApi orderApi) {
        this.userApi = userApi;
        this.orderApi = orderApi;
    }

    public ApiResponse<OrderResponse> placeOrderForExistingUser(long userId, CreateOrderRequest request) {
        ApiResponse<UserResponse> userResponse = userApi.getUserById(userId);
        if (userResponse.statusCode() >= 400) {
            throw new IllegalStateException("User is not available for order placement");
        }
        return orderApi.createOrder(request);
    }
}
