package com.apiframework.tests.regression;

import com.apiframework.sampledomain.assertions.EchoAssertions;
import com.apiframework.sampledomain.endpoint.PostmanEchoApi;
import com.apiframework.sampledomain.flow.EchoFlow;
import com.apiframework.sampledomain.flow.model.PayloadRoundtripResult;
import com.apiframework.sampledomain.model.EchoPayload;
import com.apiframework.support.NetworkAwareTestSupport;
import com.apiframework.testng.base.BaseApiTest;
import com.apiframework.testng.retry.RetrySetting;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@RetrySetting(maxRetries = 2, delayMs = 200)
public class PostmanEchoRegressionTest extends BaseApiTest {
    private EchoFlow echoFlow;

    @BeforeClass(alwaysRun = true)
    public void initFlow() {
        super.initHttpClient();
        this.echoFlow = new EchoFlow(new PostmanEchoApi(httpClient()));
    }

    @Override
    protected boolean requiresLiveApi() {
        return true;
    }

    @DataProvider(name = "echoPayloads")
    public Object[][] echoPayloads() {
        return new Object[][]{
            {new EchoPayload("order-regression", 42, true)},
            {new EchoPayload("refund-regression", 7, false)}
        };
    }

    @Test(dataProvider = "echoPayloads", description = "POST /post should echo json payload")
    public void shouldEchoJsonPayloadOnPost(EchoPayload payload) {
        try {
            PayloadRoundtripResult result = echoFlow.sendPayloadAndVerifyRoundtrip(payload);
            EchoAssertions.assertPayloadRoundtrip(result);
        } catch (Exception ex) {
            NetworkAwareTestSupport.skipOnNetworkFailure(ex);
        }
    }
}
