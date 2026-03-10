package com.apiframework.tests.smoke;

import com.apiframework.apimodel.assertions.UserAssertions;
import com.apiframework.apimodel.dto.auth.LoginRequest;
import com.apiframework.apimodel.dto.user.CreateUserRequest;
import com.apiframework.apimodel.endpoint.AuthApi;
import com.apiframework.apimodel.endpoint.UserApi;
import com.apiframework.apimodel.flow.RegistrationFlow;
import com.apiframework.core.auth.AuthStrategy;
import com.apiframework.core.auth.BasicAuthStrategy;
import com.apiframework.core.filter.HttpFilterPolicy;
import com.apiframework.core.model.ApiResponse;
import com.apiframework.apimodel.dto.user.UserResponse;
import com.apiframework.reporting.allure.ReportingFilterPolicies;
import com.apiframework.testng.base.BaseApiTest;
import com.apiframework.testng.dataprovider.FrameworkDataProviders;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class UserSmokeTest extends BaseApiTest {
    private RegistrationFlow registrationFlow;

    @BeforeClass(alwaysRun = true)
    public void initFlow() {
        AuthApi authApi = new AuthApi(httpClient);
        UserApi userApi = new UserApi(httpClient);
        this.registrationFlow = new RegistrationFlow(authApi, userApi);
    }

    @Override
    protected AuthStrategy authStrategy() {
        return new BasicAuthStrategy(runtimeConfig.basicAuth().username(), runtimeConfig.basicAuth().password());
    }

    @Override
    protected HttpFilterPolicy filterPolicy() {
        return ReportingFilterPolicies.withAllureAttachments();
    }

    @Test(dataProvider = "sample-users", dataProviderClass = FrameworkDataProviders.class)
    public void shouldCreateUserViaRegistrationFlow(String email, String firstName, String lastName) {
        LoginRequest loginRequest = new LoginRequest("smoke-user", "smoke-password");
        CreateUserRequest createUserRequest = new CreateUserRequest(email, firstName, lastName);

        ApiResponse<UserResponse> response = registrationFlow.registerUser(loginRequest, createUserRequest);
        UserAssertions.assertUserCreated(response, email);
    }
}
