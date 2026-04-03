package com.apiframework.tests.smoke;

import com.apiframework.domains.postmanecho.endpoint.PostmanEchoApi;
import com.apiframework.testsupport.assertions.ApiResponseAssert;
import com.apiframework.testsupport.base.BaseApiTest;
import io.qameta.allure.Description;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PostmanEchoSmokeTest extends BaseApiTest {
    private PostmanEchoApi echoApi;

    @Override protected String basePath() { return PostmanEchoApi.basePath(); }
    @Override protected String targetApi() { return "postman-echo"; }

    @BeforeClass(alwaysRun = true, dependsOnMethods = "initHttpClient")
    public void init() {
        this.echoApi = api(PostmanEchoApi::new);
    }

    // TODO: Placeholder for the test scenario description
    @Description("Verifies that GET /get echoes query parameters back in the response args map")
    @Test(description = "GET /get should echo query parameter")
    public void shouldEchoQueryParameter() {
        var response = echoApi.getEcho("suite", "smoke");

        // DSL hasStatus() instead of assertThat(response.statusCode()).isEqualTo(200) — consistent
        // with integration/public-api suites; produces named step "hasStatus '200'" in Allure.
        // Body-field check stays as plain assertj: no generic DSL method for args map entries.
        ApiResponseAssert.assertThat(response).hasStatus(200);
        assertThat(response.body().args()).containsEntry("suite", "smoke");
    }
}
