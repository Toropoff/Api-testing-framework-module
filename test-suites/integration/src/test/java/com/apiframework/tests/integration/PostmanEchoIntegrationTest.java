package com.apiframework.tests.integration;

import com.apiframework.postmanecho.endpoint.PostmanEchoApi;
import com.apiframework.testsupport.assertions.ApiResponseAssert;
import com.apiframework.testsupport.base.BaseApiTest;
import io.qameta.allure.Description;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PostmanEchoIntegrationTest extends BaseApiTest {
    private PostmanEchoApi echoApi;

    @Override protected String basePath() { return PostmanEchoApi.basePath(); }
    @Override protected String targetApi() { return "postman-echo"; }

    @BeforeClass(alwaysRun = true, dependsOnMethods = "initHttpClient")
    public void init() {
        this.echoApi = api(PostmanEchoApi::new);
    }

    @Description("Verifies that GET /get response conforms to the JSON schema and matches the golden-file snapshot")
    @Test(description = "GET /get should match schema and snapshot contract")
    public void shouldMatchEchoGetContractAndSnapshot() {
        var response = echoApi.getEcho("suite", "integration");

        ApiResponseAssert.assertThat(response)
                .hasStatus(200)
                .body()
                    .field("args.suite").hasValue("integration")
                .matchesSchema("schemas/postman-echo-get.schema.json")
                .matchesSnapshot("postman-echo-get");
    }
}
