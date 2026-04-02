package com.apiframework.testsupport.base;

import com.apiframework.client.ApiClientFactory;
import com.apiframework.config.ConfigResolver;
import com.apiframework.config.FrameworkRuntimeConfig;
import com.apiframework.http.CorrelationIdFilter;
import com.apiframework.http.HttpClient;
import com.apiframework.testsupport.network.NetworkAwareMethodListener;
import io.qameta.allure.Allure;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import java.util.UUID;
import java.util.function.Function;

@Listeners({NetworkAwareMethodListener.class})
public abstract class BaseApiTest {
    protected FrameworkRuntimeConfig runtimeConfig;
    protected HttpClient httpClient;
    protected TestExecutionContext testContext;

    // Runs once per test class. Resolves runtime config and builds the HttpClient for this domain.
    @BeforeClass(alwaysRun = true)
    public void initHttpClient() {
        this.runtimeConfig = ConfigResolver.resolveFromSystem();
        this.httpClient = ApiClientFactory.create(basePath(), runtimeConfig);
    }

    // Runs before each @Test. Creates a fresh correlationId, activates it on the HTTP filter, and labels the Allure parentSuite.
    @BeforeMethod(alwaysRun = true)
    public void beforeEach(ITestResult result) {
        this.testContext = new TestExecutionContext(UUID.randomUUID().toString());

        CorrelationIdFilter.set(testContext.correlationId());
        Allure.label("parentSuite", targetApi());
    }

    // Runs after each @Test. Clears the correlationId from the ThreadLocal to prevent leak to the next test.
    @AfterMethod(alwaysRun = true)
    public void afterEach() {
        CorrelationIdFilter.clear();
    }

    protected abstract String basePath();

    // Returns the logical API name shown in Allure parentSuite widget.
    protected abstract String targetApi();

    protected <T> T api(Function<HttpClient, T> apiFactory) {
        return apiFactory.apply(httpClient());
    }

    protected HttpClient httpClient() {
        if (httpClient == null) {
            initHttpClient();
        }
        return httpClient;
    }
}
