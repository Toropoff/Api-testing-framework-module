package com.apiframework.tests.regression;

import com.apiframework.sampledomain.assertions.OrderAssertions;
import com.apiframework.apimodel.dto.order.CreateOrderRequest;
import com.apiframework.apimodel.dto.order.OrderResponse;
import com.apiframework.sampledomain.endpoint.OrderApi;
import com.apiframework.sampledomain.endpoint.UserApi;
import com.apiframework.sampledomain.flow.OrderPlacementFlow;
import com.apiframework.core.auth.AuthStrategy;
import com.apiframework.core.auth.OAuth2ClientCredentialsStrategy;
import com.apiframework.core.filter.HttpFilterPolicy;
import com.apiframework.core.model.ApiResponse;
import com.apiframework.reporting.allure.ReportingFilterPolicies;
import com.apiframework.testng.base.BaseApiTest;
import com.apiframework.testng.retry.RetrySetting;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@RetrySetting(maxRetries = 2, delayMs = 500)
public class OrderRegressionTest extends BaseApiTest {
    private OrderPlacementFlow orderFlow;

    @BeforeClass(alwaysRun = true)
    public void initFlow() {
        this.orderFlow = new OrderPlacementFlow(new UserApi(httpClient), new OrderApi(httpClient));
    }

    @Override
    protected AuthStrategy authStrategy() {
        return new OAuth2ClientCredentialsStrategy(
            runtimeConfig.oauth2().tokenUrl(),
            runtimeConfig.oauth2().clientId(),
            runtimeConfig.oauth2().clientSecret(),
            runtimeConfig.oauth2().scope(),
            runtimeConfig.oauth2().refreshSkew()
        );
    }

    @Override
    protected HttpFilterPolicy filterPolicy() {
        return ReportingFilterPolicies.withAllureAttachments();
    }

    @Test
    @RetrySetting(maxRetries = 3, delayMs = 1000)
    public void shouldPlaceOrderForExistingUser() {
        ApiResponse<OrderResponse> response = orderFlow.placeOrderForExistingUser(
            1001L,
            new CreateOrderRequest(1001L, "SKU-100500", 1)
        );

        OrderAssertions.assertOrderStatus(response, "CREATED");
    }
}
