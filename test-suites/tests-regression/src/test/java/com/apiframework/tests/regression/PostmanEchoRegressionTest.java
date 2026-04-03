package com.apiframework.tests.regression;

import com.apiframework.domains.postmanecho.endpoint.PostmanEchoApi;
import com.apiframework.domains.postmanecho.model.EchoPayload;
import com.apiframework.testsupport.base.BaseApiTest;
import com.apiframework.testsupport.retry.RetrySetting;
import com.apiframework.tests.regression.assertions.EchoPostApiResponseAssert;
import io.qameta.allure.Description;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@RetrySetting(maxRetries = 2, delayMs = 200)
public class PostmanEchoRegressionTest extends BaseApiTest {
    private PostmanEchoApi echoApi;

    @Override protected String basePath() { return PostmanEchoApi.basePath(); }
    @Override protected String targetApi() { return "postman-echo"; }

    @BeforeClass(alwaysRun = true, dependsOnMethods = "initHttpClient")
    public void init() {
        this.echoApi = api(PostmanEchoApi::new);
    }

    @DataProvider(name = "echoPayloads")
    public Object[][] echoPayloads() {
        return new Object[][]{
            {new EchoPayload("order-regression", 42, true)},
            {new EchoPayload("refund-regression", 7, false)}
        };
    }

    @Description("Verifies that POST /post echoes all JSON payload fields back in the response body for each data variant")
    @Test(dataProvider = "echoPayloads", description = "POST /post should echo json payload")
    public void shouldEchoJsonPayloadOnPost(EchoPayload payload) {
        var response = echoApi.postEcho(payload);

        EchoPostApiResponseAssert.assertThat(response)
                .hasStatus(200)
                .hasJsonEqualTo(payload);
    }
}
