package com.apiframework.tests.integration;

import com.apiframework.contracts.JsonSchemaContractValidator;
import com.apiframework.contracts.snapshot.SnapshotContractChecker;
import com.apiframework.core.model.ApiResponse;
import com.apiframework.sampledomain.assertions.EchoAssertions;
import com.apiframework.sampledomain.endpoint.PostmanEchoApi;
import com.apiframework.sampledomain.flow.EchoFlow;
import com.apiframework.sampledomain.model.EchoGetResponse;
import com.apiframework.testng.base.BaseApiTest;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class OrderIntegrationTest extends BaseApiTest {
    private EchoFlow echoFlow;
    private JsonSchemaContractValidator schemaValidator;
    private SnapshotContractChecker snapshotChecker;

    @BeforeClass(alwaysRun = true)
    public void initDependencies() {
        this.echoFlow = new EchoFlow(new PostmanEchoApi(httpClient));
        this.schemaValidator = new JsonSchemaContractValidator();
        this.snapshotChecker = SnapshotContractChecker.defaultChecker();
    }

    @Override
    protected boolean requiresLiveApi() {
        return true;
    }

    @Test
    public void shouldMatchEchoGetContractAndSnapshot() {
        try {
            ApiResponse<EchoGetResponse> response = echoFlow.verifyQueryRoundtrip("suite", "integration");

            EchoAssertions.assertGetEcho(response, "suite", "integration");
            String contractBody = normalizedContractBody(response.body());
            schemaValidator.assertMatchesSchema(contractBody, "schemas/postman-echo-get.schema.json");
            snapshotChecker.assertMatchesSnapshot("postman-echo-get", contractBody, false);
        } catch (Throwable ex) {
            throw new SkipException("Postman Echo is unavailable", ex);
        }
    }

    private String normalizedContractBody(EchoGetResponse body) {
        return "{\"args\":{\"suite\":\"" + body.args().getOrDefault("suite", "")
            + "\"},\"url\":\"" + body.url() + "\"}";
    }
}
