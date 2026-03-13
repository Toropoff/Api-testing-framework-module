package com.apiframework.sampledomain.model;

import com.apiframework.core.model.ApiResponse;

public record QueryRoundtripResult(String key, String expectedValue, ApiResponse<EchoGetResponse> response) {
}
