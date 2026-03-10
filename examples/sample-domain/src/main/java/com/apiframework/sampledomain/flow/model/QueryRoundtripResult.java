package com.apiframework.sampledomain.flow.model;

import com.apiframework.core.model.ApiResponse;
import com.apiframework.sampledomain.model.EchoGetResponse;

public record QueryRoundtripResult(
    String key,
    String expectedValue,
    ApiResponse<EchoGetResponse> response
) {
}
