package com.apiframework.splunk;

import com.apiframework.model.ApiResponse;
import com.apiframework.splunk.config.SplunkConnectionConfig;
import com.apiframework.splunk.model.SplunkSearchResponse;

public final class SplunkSupport {

    private static final SplunkClient CLIENT =
        new SplunkClient(SplunkConnectionConfig.fromSystem());

    private SplunkSupport() {}

    public static SplunkClient client() {
        return CLIENT;
    }

    public static SplunkSearchResponse awaitLogsFor(ApiResponse<?> response, String index) {
        String corrId = response.correlationId();
        String query = SplunkQueries.forCorrelationId(corrId, index);
        return CLIENT.awaitNonEmpty(query);
    }
}
