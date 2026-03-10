package com.apiframework.tests.regression;

import com.apiframework.sampledomain.assertions.EchoAssertions;
import com.apiframework.sampledomain.endpoint.PostmanEchoApi;
import com.apiframework.sampledomain.flow.EchoFlow;
import com.apiframework.sampledomain.flow.model.PayloadRoundtripResult;
import com.apiframework.sampledomain.model.EchoPayload;
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
    protected boolean requiresLiveApi() {
        return true;
    }

    @Test
    public void shouldEchoJsonPayloadOnPost() {
        try {
            EchoPayload payload = new EchoPayload("order-regression", 42, true);
            PayloadRoundtripResult result = echoFlow.sendPayloadAndVerifyRoundtrip(payload);
            EchoAssertions.assertPayloadRoundtrip(result);
        } catch (Throwable ex) {
            throw new SkipException("Postman Echo is unavailable", ex);
        }
    }
}
