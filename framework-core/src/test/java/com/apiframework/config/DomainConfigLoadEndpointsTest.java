package com.apiframework.config;

import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DomainConfigLoadEndpointsTest {

    @Test
    public void parsesMultiEndpointPropertiesCorrectly() {
        Map<String, EndpointDefinition> endpoints = DomainConfig.loadEndpoints("test-domain");

        assertThat(endpoints).containsKeys("get-items", "post-item", "delete-item");
        assertThat(endpoints.get("get-items").method()).isEqualTo(HttpVerb.GET);
        assertThat(endpoints.get("get-items").relUrl()).isEqualTo("https://api.example.com/items");
        assertThat(endpoints.get("post-item").method()).isEqualTo(HttpVerb.POST);
        assertThat(endpoints.get("delete-item").method()).isEqualTo(HttpVerb.DELETE);
        assertThat(endpoints.get("delete-item").relUrl()).isEqualTo("https://api.example.com/items/{id}");
    }

    @Test
    public void throwsOnMissingFile() {
        assertThatThrownBy(() -> DomainConfig.loadEndpoints("no-such-domain"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("no-such-domain.properties not found on classpath");
    }

    @Test
    public void throwsOnUnknownVerbString() {
        assertThatThrownBy(() -> DomainConfig.loadEndpoints("test-domain-bad-verb"))
            .isInstanceOf(Exception.class);
    }

    @Test
    public void loadsApiNameByDomainName() {
        String apiName = DomainConfig.loadApiName("test-domain");
        assertThat(apiName).isEqualTo("test-domain");
    }
}
