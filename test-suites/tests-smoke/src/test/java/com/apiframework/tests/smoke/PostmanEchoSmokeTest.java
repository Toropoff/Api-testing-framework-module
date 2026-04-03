package com.apiframework.tests.smoke;

import com.apiframework.domains.postmanecho.endpoint.PostmanEchoApi;
import com.apiframework.testsupport.base.BaseApiTest;
import com.apiframework.tests.smoke.assertions.EchoGetApiResponseAssert;
import io.qameta.allure.Description;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PostmanEchoSmokeTest extends BaseApiTest {
    private PostmanEchoApi echoApi;

    @Override protected String basePath() { return PostmanEchoApi.basePath(); }
    @Override protected String targetApi() { return "postman-echo"; }

    @BeforeClass(alwaysRun = true, dependsOnMethods = "initHttpClient")
    public void init() {
        this.echoApi = api(PostmanEchoApi::new);
    }

    @Description("Verifies that GET /get echoes query parameters back in the response args map")
    @Test(description = "GET /get should echo query parameter")
    public void shouldEchoQueryParameter() {
        var response = echoApi.getEcho("suite", "smoke");

        EchoGetApiResponseAssert.assertThat(response)
                .hasStatus(200)
                .hasArgsEntry("suite", "smoke");
    }
}
