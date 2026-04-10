package com.apiframework.config;

import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class UrlResolverTest {

    @Test
    public void concatenatesBaseUrlAndRelUrl() {
        String result = UrlResolver.resolve("https://api.company.com", "/Countries", Map.of());
        assertThat(result).isEqualTo("https://api.company.com/Countries");
    }

    @Test
    public void passesAbsoluteRelUrlThroughWhenBaseUrlIsEmpty() {
        String result = UrlResolver.resolve("", "https://openholidaysapi.org/Countries", Map.of());
        assertThat(result).isEqualTo("https://openholidaysapi.org/Countries");
    }

    @Test
    public void passesAbsoluteRelUrlThroughWhenBaseUrlIsNull() {
        String result = UrlResolver.resolve(null, "https://openholidaysapi.org/Countries", Map.of());
        assertThat(result).isEqualTo("https://openholidaysapi.org/Countries");
    }

    @Test
    public void substitutesPathParams() {
        String result = UrlResolver.resolve("https://api.company.com", "/users/{id}/orders",
            Map.of("id", "42"));
        assertThat(result).isEqualTo("https://api.company.com/users/42/orders");
    }

    @Test
    public void noOpWhenPathParamsEmpty() {
        String result = UrlResolver.resolve("https://api.company.com", "/items", Map.of());
        assertThat(result).isEqualTo("https://api.company.com/items");
    }
}
