package com.apiframework.apimodel.dto.order;

public record OrderResponse(Long id, Long userId, String sku, int quantity, String status) {
}
