package com.apiframework.tests.smoke;

import com.apiframework.postmanecho.model.EchoGetResponse;
import com.apiframework.testsupport.assertions.ApiResponseAssert;
import com.apiframework.testsupport.base.BaseApiTest;
import io.qameta.allure.Description;
import org.testng.annotations.Test;

public class PostmanEchoSmokeTest extends BaseApiTest {

    @Override
    protected String domain() { return "postman-echo"; }

    @Description("Verifies that GET /get echoes query parameters back in the response args map")
    @Test(description = "GET /get should echo query parameter")
    public void shouldEchoQueryParameter() {
        var response = call("get-echo", EchoGetResponse.class)
                .query("suite", "smoke")
                .send();

        ApiResponseAssert.assertThat(response)
                .hasStatus(200)
                .body()
                    .field("args.suite").hasValue("smoke");
    }
}
