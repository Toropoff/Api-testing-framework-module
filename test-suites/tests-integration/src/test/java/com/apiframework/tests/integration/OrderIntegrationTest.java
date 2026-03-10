package com.apiframework.tests.integration;

import com.apiframework.apimodel.dto.order.OrderResponse;
import com.apiframework.sampledomain.endpoint.OrderApi;
import com.apiframework.contracts.JsonSchemaContractValidator;
import com.apiframework.contracts.snapshot.SnapshotContractChecker;
import com.apiframework.core.model.ApiResponse;
import com.apiframework.testng.base.BaseApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class OrderIntegrationTest extends BaseApiTest {
    private OrderApi orderApi;
    private JsonSchemaContractValidator schemaValidator;
    private SnapshotContractChecker snapshotChecker;

    @BeforeClass(alwaysRun = true)
    public void initDependencies() {
        this.orderApi = new OrderApi(httpClient);
        this.schemaValidator = new JsonSchemaContractValidator();
        this.snapshotChecker = SnapshotContractChecker.defaultChecker();
    }

    @Test
    public void shouldMatchOrderContractAndSnapshot() {
        ApiResponse<OrderResponse> response = orderApi.getOrderById(1001L);

        schemaValidator.assertMatchesSchema(response.rawBody(), "schemas/order-response.schema.json");
        snapshotChecker.assertMatchesSnapshot("order-response", response.rawBody(), false);
    }
}
