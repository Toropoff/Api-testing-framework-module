package com.apiframework.tests.integration;

import com.apiframework.postmanecho.model.EchoGetResponse;
import com.apiframework.testsupport.assertions.ApiResponseAssert;
import com.apiframework.testsupport.base.BaseApiTest;
import io.qameta.allure.Description;
import org.testng.annotations.Test;

public class PostmanEchoIntegrationTest extends BaseApiTest {

    @Override
    protected String domain() { return "postman-echo"; }

    @Description("Verifies that GET /get response conforms to the JSON schema and matches the golden-file snapshot")
    @Test(description = "GET /get should match schema and snapshot contract")
    public void shouldMatchEchoGetContractAndSnapshot() {
        var response = call("get-echo", EchoGetResponse.class)
                .query("suite", "integration")
                .send();

        ApiResponseAssert.assertThat(response)
                .hasStatus(200)
                .body()
                    .field("args.suite").hasValue("integration")
                .matchesSchema("schemas/postman-echo-get.schema.json")
                .matchesSnapshot("postman-echo-get");
    }
}
