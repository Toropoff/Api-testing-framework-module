package com.apiframework.testsupport.base;

import com.apiframework.client.ApiClientFactory;
import com.apiframework.config.ConfigResolver;
import com.apiframework.config.FrameworkRuntimeConfig;
import com.apiframework.http.HttpClient;
import com.apiframework.testsupport.allure.AllureEnvironmentWriter;
import com.apiframework.testsupport.network.NetworkAwareMethodListener;
import io.qameta.allure.Allure;
import org.testng.ITestResult;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Listeners({NetworkAwareMethodListener.class, AllureEnvironmentWriter.class})
public abstract class BaseApiTest {
    public static final String TEST_CONTEXT_ATTRIBUTE = "framework.test.context";

    protected FrameworkRuntimeConfig runtimeConfig;
    protected HttpClient httpClient;
    protected TestExecutionContext testContext;

    @BeforeClass(alwaysRun = true)
    public void initHttpClient() {
        this.runtimeConfig = ConfigResolver.resolveFromSystem();
        this.httpClient = ApiClientFactory.create(basePath(), runtimeConfig);
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
        Allure.label("parentSuite", targetApi());
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

    protected Map<String, String> environmentTags() {
        Map<String, String> tags = new LinkedHashMap<>();
        tags.put("profile", runtimeConfig.profile());
        tags.put("env", runtimeConfig.env());
        tags.put("baseUrl", basePath());
        return tags;
    }
}
