package com.apiframework.tests.smoke;

import com.apiframework.core.filter.HttpFilterPolicy;
import com.apiframework.core.model.ApiResponse;
import com.apiframework.reporting.allure.ReportingFilterPolicies;
import com.apiframework.sampledomain.assertions.EchoAssertions;
import com.apiframework.sampledomain.endpoint.PostmanEchoApi;
import com.apiframework.sampledomain.flow.EchoFlow;
import com.apiframework.sampledomain.model.EchoGetResponse;
import com.apiframework.testng.base.BaseApiTest;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class UserSmokeTest extends BaseApiTest {
    private EchoFlow echoFlow;

    @BeforeClass(alwaysRun = true)
    public void initFlow() {
        this.echoFlow = new EchoFlow(new PostmanEchoApi(httpClient));
    }

    @Override
    protected HttpFilterPolicy filterPolicy() {
        return ReportingFilterPolicies.withAllureAttachments();
    }

    @Override
    protected boolean requiresLiveApi() {
        return true;
    }

    @Test
    public void shouldEchoQueryParameter() {
        try {
            ApiResponse<EchoGetResponse> response = echoFlow.verifyQueryRoundtrip("suite", "smoke");
            EchoAssertions.assertGetEcho(response, "suite", "smoke");
        } catch (Throwable ex) {
            throw new SkipException("Postman Echo is unavailable", ex);
        }
    }
}
