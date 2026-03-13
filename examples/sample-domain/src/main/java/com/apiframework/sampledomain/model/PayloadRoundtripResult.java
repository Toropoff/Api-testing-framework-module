package com.apiframework.sampledomain.model;

import com.apiframework.core.model.ApiResponse;

public record PayloadRoundtripResult(EchoPayload payload, ApiResponse<EchoPostResponse> response) {
}
