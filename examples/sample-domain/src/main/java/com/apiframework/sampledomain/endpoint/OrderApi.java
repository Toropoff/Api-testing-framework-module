package com.apiframework.sampledomain.endpoint;

import com.apiframework.apimodel.dto.order.CreateOrderRequest;
import com.apiframework.apimodel.dto.order.OrderResponse;
import com.apiframework.core.http.HttpClient;
import com.apiframework.core.model.ApiRequest;
import com.apiframework.core.model.ApiResponse;
import com.apiframework.core.model.HttpMethod;

public final class OrderApi extends BaseApiEndpoint {
    public OrderApi(HttpClient httpClient) {
        super(httpClient);
    }

    public ApiResponse<OrderResponse> createOrder(CreateOrderRequest request) {
        ApiRequest<CreateOrderRequest> apiRequest = ApiRequest.<CreateOrderRequest>builder(HttpMethod.POST, "/api/v1/orders")
            .body(request)
            .build();
        return httpClient.execute(apiRequest, OrderResponse.class);
    }

    public ApiResponse<OrderResponse> getOrderById(long orderId) {
        ApiRequest<Void> apiRequest = ApiRequest.<Void>builder(HttpMethod.GET, "/api/v1/orders/" + orderId)
            .build();
        return httpClient.execute(apiRequest, OrderResponse.class);
    }
}
