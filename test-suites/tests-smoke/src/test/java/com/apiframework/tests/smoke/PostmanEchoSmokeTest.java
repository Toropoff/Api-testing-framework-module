package com.apiframework.tests.smoke;

import com.apiframework.domains.postmanecho.assertions.EchoAssertions;
import com.apiframework.domains.postmanecho.endpoint.PostmanEchoApi;
import com.apiframework.domains.postmanecho.flow.EchoFlow;
import com.apiframework.domains.postmanecho.model.QueryRoundtripResult;
import com.apiframework.testsupport.network.NetworkAwareTestSupport;
import com.apiframework.testsupport.base.BaseApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PostmanEchoSmokeTest extends BaseApiTest {
    private EchoFlow echoFlow;

    @BeforeClass(alwaysRun = true)
    public void initFlow() {
        super.initHttpClient();
        this.echoFlow = new EchoFlow(new PostmanEchoApi(httpClient()));
    }

    @Override
    protected boolean requiresLiveApi() {
        return true;
    }

    @Test(description = "GET /get should echo query parameter")
    public void shouldEchoQueryParameter() {
        try {
            QueryRoundtripResult roundtrip = echoFlow.verifyQueryRoundtrip("suite", "smoke");
            EchoAssertions.assertQueryRoundtrip(roundtrip);
        } catch (Exception ex) {
            NetworkAwareTestSupport.skipOnNetworkFailure(ex);
        }
    }
}
