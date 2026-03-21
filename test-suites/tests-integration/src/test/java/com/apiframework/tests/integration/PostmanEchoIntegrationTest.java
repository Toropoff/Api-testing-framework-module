package com.apiframework.tests.integration;

import com.apiframework.contracts.JsonSchemaContractValidator;
import com.apiframework.contracts.snapshot.SnapshotContractChecker;
import com.apiframework.domains.postmanecho.endpoint.PostmanEchoApi;
import com.apiframework.testsupport.network.NetworkAwareTestSupport;
import com.apiframework.testsupport.base.BaseApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PostmanEchoIntegrationTest extends BaseApiTest {
    private PostmanEchoApi echoApi;
    private JsonSchemaContractValidator schemaValidator;
    private SnapshotContractChecker snapshotChecker;

    @Override
    protected String baseUrl() {
        return PostmanEchoApi.baseUrl();
    }

    @BeforeClass(alwaysRun = true)
    public void init() {
        super.initHttpClient();
        this.echoApi = new PostmanEchoApi(httpClient());
        this.schemaValidator = new JsonSchemaContractValidator();
        this.snapshotChecker = SnapshotContractChecker.fromRootDir();
    }

    @Override
    protected boolean requiresLiveApi() {
        return true;
    }

    @Test(description = "GET /get should match schema and snapshot contract")
    public void shouldMatchEchoGetContractAndSnapshot() {
        try {
            var response = echoApi.getEcho("suite", "integration");

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).isNotNull();
            assertThat(response.body().args()).containsEntry("suite", "integration");

            schemaValidator.assertMatchesSchema(response.rawBody(), "schemas/postman-echo-get.schema.json");
            snapshotChecker.assertMatchesSnapshot("postman-echo-get", response.rawBody(), false);
        } catch (Exception ex) {
            NetworkAwareTestSupport.skipOnNetworkFailure(ex);
        }
    }
}
