package com.apiframework.testsupport.base;

import com.apiframework.client.ApiClientFactory;
import com.apiframework.config.ConfigResolver;
import com.apiframework.config.DomainConfig;
import com.apiframework.config.EndpointDefinition;
import com.apiframework.http.CorrelationIdFilter;
import com.apiframework.http.HttpClient;
import com.apiframework.testsupport.client.ApiRequestBuilder;
import com.apiframework.testsupport.network.NetworkAwareMethodListener;
import io.qameta.allure.Allure;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;

@Listeners({NetworkAwareMethodListener.class})
public abstract class BaseApiTest {

    private HttpClient                     httpClient;
    private String                         baseUrl;
    private Map<String, EndpointDefinition> endpoints;
    private String                         apiName;

    /** The only domain binding a test must provide. Matches the {domain}.properties filename (without extension). */
    protected abstract String domain();

    // Runs once per test class. Resolves runtime config and loads endpoint catalog for this domain.
    @BeforeClass(alwaysRun = true)
    public void initHttpClient() {
        this.endpoints  = DomainConfig.loadEndpoints(domain());
        this.apiName    = DomainConfig.loadApiName(domain());
        this.baseUrl    = ConfigResolver.resolveFromSystem().baseUrl();
        this.httpClient = ApiClientFactory.build();
    }

    // Runs before each @Test. Creates a fresh correlationId, activates it on the HTTP filter, and labels the Allure parentSuite.
    @BeforeMethod(alwaysRun = true)
    public void beforeEach(ITestResult result) {
        TestExecutionContext testContext = new TestExecutionContext(UUID.randomUUID().toString());
        CorrelationIdFilter.set(testContext.correlationId());
        Allure.label("parentSuite", apiName);
    }

    // Runs after each @Test. Clears the correlationId from the ThreadLocal to prevent leak to the next test.
    @AfterMethod(alwaysRun = true)
    public void afterEach() {
        CorrelationIdFilter.clear();
    }

    /**
     * Fluent request entry. Looks up the endpoint in the loaded catalog and returns a builder
     * pre-wired with verb + relUrl + baseUrl.
     *
     * @throws IllegalStateException if {@code endpointKey} is not found in this domain's catalog
     */
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
