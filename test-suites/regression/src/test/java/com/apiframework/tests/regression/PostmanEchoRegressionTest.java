package com.apiframework.tests.regression;

import com.apiframework.testsupport.assertions.ApiResponseAssert;
import com.apiframework.testsupport.base.BaseApiTest;
import com.apiframework.testsupport.retry.RetrySetting;
import io.qameta.allure.Description;
import org.testng.annotations.Test;

@RetrySetting(maxRetries = 2, delayMs = 200)
public class PostmanEchoRegressionTest extends BaseApiTest {

    @Override
    protected String domain() { return "postman-echo"; }

    @Description("Verifies that POST /post echoes all JSON payload fields back in the response body")
    @Test(description = "POST /post should echo json payload")
    public void shouldEchoJsonPayload() {
        var response = call("post-echo", String.class)
                .bodyField("event", "order-regression")
                .bodyField("amount", 42)
                .bodyField("active", true)
                .send();

        ApiResponseAssert.assertThat(response)
                .hasStatus(200)
                .body()
                    .field("json.event").hasValue("order-regression")
                    .field("json.amount").hasValue(42)
                    .field("json.active").hasValue(true);
    }
}
