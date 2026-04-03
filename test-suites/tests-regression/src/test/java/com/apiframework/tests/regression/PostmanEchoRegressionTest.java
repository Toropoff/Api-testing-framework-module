package com.apiframework.tests.regression;

import com.apiframework.domains.postmanecho.endpoint.PostmanEchoApi;
import com.apiframework.domains.postmanecho.model.EchoPayload;
import com.apiframework.testsupport.assertions.ApiResponseAssert;
import com.apiframework.testsupport.base.BaseApiTest;
import com.apiframework.testsupport.retry.RetrySetting;
import io.qameta.allure.Description;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

    // TODO: Placeholder for the test scenario description
    @Description("Verifies that POST /post echoes all JSON payload fields back in the response body for each data variant")
    @Test(dataProvider = "echoPayloads", description = "POST /post should echo json payload")
    public void shouldEchoJsonPayloadOnPost(EchoPayload payload) {
        var response = echoApi.postEcho(payload);

        // DSL hasStatus() for consistency with other suites; produces named step in Allure report.
        // Remaining assertions stay as plain assertj: body-field checks are payload-specific
        // to this test's data variants — no generic DSL equivalent without a domain assert class.
        ApiResponseAssert.assertThat(response).hasStatus(200);
        assertThat(response.body().json()).isNotNull();
        assertThat(response.body().json().event()).isEqualTo(payload.event());
        assertThat(response.body().json().amount()).isEqualTo(payload.amount());
        assertThat(response.body().json().active()).isEqualTo(payload.active());
    }
}
