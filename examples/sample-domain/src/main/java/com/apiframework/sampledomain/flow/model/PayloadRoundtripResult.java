package com.apiframework.sampledomain.flow.model;

import com.apiframework.core.model.ApiResponse;
import com.apiframework.sampledomain.model.EchoPayload;
import com.apiframework.sampledomain.model.EchoPostResponse;

public record PayloadRoundtripResult(
    EchoPayload payload,
    ApiResponse<EchoPostResponse> response
) {
}
