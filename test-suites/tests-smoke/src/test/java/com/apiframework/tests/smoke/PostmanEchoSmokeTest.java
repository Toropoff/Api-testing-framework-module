package com.apiframework.tests.smoke;

import com.apiframework.domains.postmanecho.endpoint.PostmanEchoApi;
import com.apiframework.testsupport.network.NetworkAwareTestSupport;
import com.apiframework.testsupport.base.BaseApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PostmanEchoSmokeTest extends BaseApiTest {
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

    @Test(description = "GET /get should echo query parameter")
    public void shouldEchoQueryParameter() {
        try {
            var response = echoApi.getEcho("suite", "smoke");

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).isNotNull();
            assertThat(response.body().args()).containsEntry("suite", "smoke");
        } catch (Exception ex) {
            NetworkAwareTestSupport.skipOnNetworkFailure(ex);
        }
    }
}
