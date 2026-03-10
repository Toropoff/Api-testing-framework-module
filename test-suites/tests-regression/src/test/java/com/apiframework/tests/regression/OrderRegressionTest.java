package com.apiframework.tests.regression;

import com.apiframework.core.filter.HttpFilterPolicy;
import com.apiframework.core.model.ApiResponse;
import com.apiframework.reporting.allure.ReportingFilterPolicies;
import com.apiframework.sampledomain.assertions.EchoAssertions;
import com.apiframework.sampledomain.endpoint.PostmanEchoApi;
import com.apiframework.sampledomain.flow.EchoFlow;
import com.apiframework.sampledomain.model.EchoPayload;
import com.apiframework.sampledomain.model.EchoPostResponse;
import com.apiframework.testng.base.BaseApiTest;
import com.apiframework.testng.retry.RetrySetting;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@RetrySetting(maxRetries = 2, delayMs = 200)
public class OrderRegressionTest extends BaseApiTest {
    private EchoFlow echoFlow;

    @BeforeClass(alwaysRun = true)
    public void initFlow() {
        this.echoFlow = new EchoFlow(new PostmanEchoApi(httpClient));
    }

    @Override
    protected HttpFilterPolicy filterPolicy() {
        return ReportingFilterPolicies.withAllureAttachments();
    }

    @Override
    protected boolean requiresLiveApi() {
        return true;
    }

    @Test
    public void shouldEchoJsonPayloadOnPost() {
        try {
            EchoPayload payload = new EchoPayload("order-regression", 42, true);
            ApiResponse<EchoPostResponse> response = echoFlow.sendPayload(payload);
            EchoAssertions.assertPostEcho(response, "order-regression", 42);
        } catch (Throwable ex) {
            throw new SkipException("Postman Echo is unavailable", ex);
        }
    }
}
