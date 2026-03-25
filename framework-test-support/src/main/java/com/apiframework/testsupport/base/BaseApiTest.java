package com.apiframework.testsupport.base;

import com.apiframework.client.ApiClientFactory;
import com.apiframework.config.ConfigResolver;
import com.apiframework.config.FrameworkRuntimeConfig;
import com.apiframework.http.HttpClient;
import com.apiframework.testsupport.network.NetworkAwareMethodListener;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Listeners(NetworkAwareMethodListener.class)
public abstract class BaseApiTest {
    public static final String TEST_CONTEXT_ATTRIBUTE = "framework.test.context";

    protected FrameworkRuntimeConfig runtimeConfig;
    protected HttpClient httpClient;
    protected TestExecutionContext testContext;

    @BeforeSuite(alwaysRun = true)
    public void initRuntimeConfig() {
        this.runtimeConfig = ConfigResolver.resolveFromSystem();
    }

    @BeforeClass(alwaysRun = true)
    public void initHttpClient() {
        if (runtimeConfig == null) {
            initRuntimeConfig();
        }

        this.httpClient = ApiClientFactory.create(baseUrl(), runtimeConfig);
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeEach(ITestResult result) {
        this.testContext = new TestExecutionContext(
            result.getTestClass().getName() + "." + result.getMethod().getMethodName(),
            UUID.randomUUID().toString(),
            Instant.now(),
            environmentTags()
        );

        result.setAttribute(TEST_CONTEXT_ATTRIBUTE, testContext);
        result.setAttribute("X-Correlation-Id", testContext.correlationId());
        Reporter.log("[framework] testContext=" + testContext, true);
    }

    @AfterMethod(alwaysRun = true)
    public void afterEach(ITestResult result) {
        Reporter.log("[framework] completed=" + testContext.testId() + " status=" + result.getStatus(), true);
    }

    protected abstract String baseUrl();

    protected <T> T api(Function<HttpClient, T> apiFactory) {
        return apiFactory.apply(httpClient());
    }

    protected HttpClient httpClient() {
        if (httpClient == null) {
            initHttpClient();
        }
        return httpClient;
    }

    protected Map<String, String> environmentTags() {
        Map<String, String> tags = new LinkedHashMap<>();
        tags.put("profile", runtimeConfig.profile());
        tags.put("env", runtimeConfig.env());
        tags.put("baseUrl", baseUrl());
        return tags;
    }
}
