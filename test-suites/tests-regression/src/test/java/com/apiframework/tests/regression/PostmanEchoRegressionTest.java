package com.apiframework.tests.regression;

import com.apiframework.domains.postmanecho.endpoint.PostmanEchoApi;
import com.apiframework.domains.postmanecho.model.EchoPayload;
import com.apiframework.testsupport.assertions.ApiResponseAssert;
import com.apiframework.testsupport.base.BaseApiTest;
import com.apiframework.testsupport.retry.RetrySetting;
import io.qameta.allure.Description;
import org.testng.annotations.BeforeClass;
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

    @Description("Verifies that POST /post echoes all JSON payload fields back in the response body")
    @Test(description = "POST /post should echo json payload")
    public void shouldEchoJsonPayload() {
        var response = echoApi.postEcho(new EchoPayload("order-regression", 42, true));

        ApiResponseAssert.assertThat(response)
                .hasStatus(200)
                .body()
                    .field("json.event").isEqualTo("order-regression")
                    .field("json.amount").isEqualTo(42)
                    .field("json.active").isEqualTo(true);
    }
}
