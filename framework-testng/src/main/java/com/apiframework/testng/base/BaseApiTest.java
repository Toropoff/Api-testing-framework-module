package com.apiframework.testng.base;

import com.apiframework.core.auth.AuthStrategy;
import com.apiframework.core.client.ApiClientFactory;
import com.apiframework.core.config.ConfigResolver;
import com.apiframework.core.config.FrameworkRuntimeConfig;
import com.apiframework.core.filter.HttpFilterPolicy;
import com.apiframework.core.http.HttpClient;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

public abstract class BaseApiTest {
    protected FrameworkRuntimeConfig runtimeConfig;
    protected HttpClient httpClient;

    @BeforeSuite(alwaysRun = true)
    public void initRuntimeConfig() {
        this.runtimeConfig = ConfigResolver.resolveFromSystem();
    }

    @BeforeClass(alwaysRun = true)
    public void initHttpClient() {
        if (requiresLiveApi() && !Boolean.parseBoolean(System.getProperty("framework.runLiveTests", "false"))) {
            throw new SkipException("Live API tests are disabled. Set -Dframework.runLiveTests=true");
        }

        this.httpClient = ApiClientFactory.create(runtimeConfig, authStrategy(), filterPolicy());
    }

    protected AuthStrategy authStrategy() {
        return AuthStrategy.none();
    }

    protected HttpFilterPolicy filterPolicy() {
        return HttpFilterPolicy.defaultPolicy();
    }

    protected boolean requiresLiveApi() {
        return true;
    }
}
