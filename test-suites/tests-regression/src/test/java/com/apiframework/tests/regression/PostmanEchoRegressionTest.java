package com.apiframework.tests.regression;

import com.apiframework.domains.postmanecho.endpoint.PostmanEchoApi;
import com.apiframework.domains.postmanecho.model.EchoPayload;
import com.apiframework.testsupport.network.NetworkAwareTestSupport;
import com.apiframework.testsupport.base.BaseApiTest;
import com.apiframework.testsupport.retry.RetrySetting;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@RetrySetting(maxRetries = 2, delayMs = 200)
public class PostmanEchoRegressionTest extends BaseApiTest {
    private PostmanEchoApi echoApi;

    @Override
    protected String baseUrl() {
        return PostmanEchoApi.baseUrl();
    }

    @BeforeClass(alwaysRun = true)
    public void init() {
        super.initHttpClient();
        this.echoApi = new PostmanEchoApi(httpClient());
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
            var response = echoApi.postEcho(payload);

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).isNotNull();
            assertThat(response.body().json()).isNotNull();
            assertThat(response.body().json().event()).isEqualTo(payload.event());
            assertThat(response.body().json().amount()).isEqualTo(payload.amount());
            assertThat(response.body().json().active()).isEqualTo(payload.active());
        } catch (Exception ex) {
            NetworkAwareTestSupport.skipOnNetworkFailure(ex);
        }
    }
}
