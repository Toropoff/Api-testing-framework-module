package com.apiframework.sampledomain.flow;

import com.apiframework.core.model.ApiResponse;
import com.apiframework.sampledomain.endpoint.PostmanEchoApi;
import com.apiframework.sampledomain.model.EchoGetResponse;
import com.apiframework.sampledomain.model.EchoPayload;
import com.apiframework.sampledomain.model.EchoPostResponse;

public final class EchoFlow {
    private final PostmanEchoApi echoApi;

    public EchoFlow(PostmanEchoApi echoApi) {
        this.echoApi = echoApi;
    }

    public ApiResponse<EchoGetResponse> verifyQueryRoundtrip(String key, String value) {
        return echoApi.getEcho(key, value);
    }

    public ApiResponse<EchoPostResponse> sendPayload(EchoPayload payload) {
        return echoApi.postEcho(payload);
    }
}
