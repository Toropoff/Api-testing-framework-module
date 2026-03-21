package com.apiframework.tests.smoke;

import com.apiframework.domains.postmanecho.endpoint.PostmanEchoApi;
import com.apiframework.testsupport.base.BaseApiTest;
import com.apiframework.testsupport.base.LiveApi;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@LiveApi
public class PostmanEchoSmokeTest extends BaseApiTest {
    private PostmanEchoApi echoApi;

    @Override protected String baseUrl() { return PostmanEchoApi.baseUrl(); }

    @BeforeClass(alwaysRun = true, dependsOnMethods = "initHttpClient")
    public void init() {
        this.echoApi = api(PostmanEchoApi::new);
    }

    @Test(description = "GET /get should echo query parameter")
    public void shouldEchoQueryParameter() {
        var response = echoApi.getEcho("suite", "smoke");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body().args()).containsEntry("suite", "smoke");
    }
}
