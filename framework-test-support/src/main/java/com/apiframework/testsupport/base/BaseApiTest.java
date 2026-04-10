package com.apiframework.testsupport.base;

import com.apiframework.config.ConfigResolver;
import com.apiframework.config.DomainConfig;
import com.apiframework.config.EndpointDefinition;
import com.apiframework.config.FrameworkRuntimeConfig;
import com.apiframework.http.CorrelationIdFilter;
import com.apiframework.http.HttpClient;
import com.apiframework.http.RestAssuredHttpClient;
import com.apiframework.testsupport.client.ApiRequestBuilder;
import com.apiframework.testsupport.network.NetworkAwareMethodListener;
import io.qameta.allure.Allure;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;

import static io.restassured.RestAssured.preemptive;

@Listeners({NetworkAwareMethodListener.class})
public abstract class BaseApiTest {

    private HttpClient                      httpClient;
    private String                          baseUrl;
    private Map<String, EndpointDefinition> endpoints;
    private String                          apiName;

    /** The only domain binding a test must provide. Matches the {domain}.properties filename (without extension). */
    protected abstract String domain();

    @BeforeClass(alwaysRun = true)
    public void initHttpClient() {
        FrameworkRuntimeConfig config = ConfigResolver.resolveFromSystem();

        this.endpoints = DomainConfig.loadEndpoints(domain());
        this.apiName   = DomainConfig.loadApiName(domain());
        this.baseUrl   = config.baseUrl();

        RestAssuredConfig restAssuredConfig = RestAssuredConfig.config().httpClient(
            HttpClientConfig.httpClientConfig()
                .setParam("http.connection.timeout", config.connectTimeoutMs())
                .setParam("http.socket.timeout",     config.readTimeoutMs())
        );

        RequestSpecBuilder specBuilder = new RequestSpecBuilder()
            .setContentType(ContentType.JSON)
            .setConfig(restAssuredConfig)
            .addFilter(new CorrelationIdFilter());

        String clientName   = System.getenv("FRAMEWORK_CLIENT_NAME");
        String clientSecret = System.getenv("FRAMEWORK_CLIENT_SECRET");
        if (clientName != null && !clientName.isBlank()
                && clientSecret != null && !clientSecret.isBlank()) {
            specBuilder.setAuth(preemptive().basic(clientName, clientSecret));
        }

        this.httpClient = new RestAssuredHttpClient(specBuilder.build());
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeEach(ITestResult result) {
        TestExecutionContext testContext = new TestExecutionContext(UUID.randomUUID().toString());
        CorrelationIdFilter.set(testContext.correlationId());
        Allure.label("parentSuite", apiName);
    }

    @AfterMethod(alwaysRun = true)
    public void afterEach() {
        CorrelationIdFilter.clear();
    }

    protected <T> ApiRequestBuilder<T> call(String endpointKey, Class<T> responseType) {
        EndpointDefinition def = endpoints.get(endpointKey);
        if (def == null) {
            throw new IllegalStateException(
                "Unknown endpoint key '" + endpointKey + "' in domain '" + domain() + "'. " +
                "Known keys: " + new TreeSet<>(endpoints.keySet()));
        }
        return new ApiRequestBuilder<>(httpClient(), baseUrl, def, responseType);
    }

    /** Used by listeners and reporting for display metadata. */
    public final String apiName() { return apiName; }

    protected HttpClient httpClient() {
        if (httpClient == null) {
            throw new IllegalStateException(
                "httpClient is null — initHttpClient() did not run. Check @BeforeClass lifecycle setup.");
        }
        return httpClient;
    }
}
