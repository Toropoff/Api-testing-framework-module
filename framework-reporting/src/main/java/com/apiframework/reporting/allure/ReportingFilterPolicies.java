package com.apiframework.reporting.allure;

import com.apiframework.core.filter.HttpFilterPolicy;
import io.qameta.allure.restassured.AllureRestAssured;

public final class ReportingFilterPolicies {
    public static final String HTTP_STEPS_ENABLED_PROPERTY = "framework.reporting.httpSteps.enabled";

    private ReportingFilterPolicies() {
    }

    public static HttpFilterPolicy withAllureReporting() {
        if (!isEnabled(HTTP_STEPS_ENABLED_PROPERTY, true)) {
            return HttpFilterPolicy.defaultPolicy();
        }

        return HttpFilterPolicy.defaultPolicy()
            .withAdditionalFilter(new AllureRestAssured());
    }

    private static boolean isEnabled(String propertyName, boolean defaultValue) {
        return Boolean.parseBoolean(System.getProperty(propertyName, String.valueOf(defaultValue)));
    }
}
