package com.apiframework.reporting.allure;

import com.apiframework.core.filter.HttpFilterPolicy;

public final class ReportingFilterPolicies {
    private ReportingFilterPolicies() {
    }

    public static HttpFilterPolicy withAllureAttachments() {
        return HttpFilterPolicy.defaultPolicy().withAdditionalFilter(new AllureRequestResponseFilter());
    }
}
