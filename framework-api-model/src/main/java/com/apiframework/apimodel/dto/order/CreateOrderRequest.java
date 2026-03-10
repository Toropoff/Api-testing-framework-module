package com.apiframework.apimodel.dto.order;

public record CreateOrderRequest(Long userId, String sku, int quantity) {
}
