package com.apiframework.tests.integration;

import com.apiframework.testsupport.contracts.JsonSchemaContractValidator;
import com.apiframework.testsupport.contracts.SnapshotContractValidator;
import com.apiframework.domains.postmanecho.endpoint.PostmanEchoApi;
import com.apiframework.testsupport.base.BaseApiTest;
import io.qameta.allure.Description;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PostmanEchoIntegrationTest extends BaseApiTest {
    private PostmanEchoApi echoApi;
    private JsonSchemaContractValidator schemaValidator;
    private SnapshotContractValidator snapshotValidator;

    @Override protected String basePath() { return PostmanEchoApi.basePath(); }
    @Override protected String targetApi() { return "postman-echo"; }

    @BeforeClass(alwaysRun = true, dependsOnMethods = "initHttpClient")
    public void init() {
        this.echoApi = api(PostmanEchoApi::new);
        this.schemaValidator = new JsonSchemaContractValidator();
        this.snapshotValidator = new SnapshotContractValidator();
    }

    // TODO: Placeholder for the test scenario description
    @Description("Verifies that GET /get response conforms to the JSON schema and matches the golden-file snapshot")
    @Test(description = "GET /get should match schema and snapshot contract")
    public void shouldMatchEchoGetContractAndSnapshot() {
        var response = echoApi.getEcho("suite", "integration");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body().args()).containsEntry("suite", "integration");
        schemaValidator.assertMatchesSchema(response.rawBody(), "schemas/postman-echo-get.schema.json");
        snapshotValidator.assertMatchesSnapshot("postman-echo-get", response.rawBody());
    }
}
