package com.apiframework.domains.postmanecho.model;

import com.apiframework.core.model.ApiResponse;

public record PayloadRoundtripResult(EchoPayload payload, ApiResponse<EchoPostResponse> response) {
}
