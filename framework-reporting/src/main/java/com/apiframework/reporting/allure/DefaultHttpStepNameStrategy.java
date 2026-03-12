package com.apiframework.reporting.allure;

import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;

public final class DefaultHttpStepNameStrategy implements HttpStepNameStrategy {
    @Override
    public String beforeCall(FilterableRequestSpecification requestSpec) {
        return requestSpec.getMethod() + " " + UriUtils.pathFromUri(requestSpec.getURI()) + " -> ...";
    }

    @Override
    public String afterCall(FilterableRequestSpecification requestSpec, Response response) {
        return requestSpec.getMethod() + " " + UriUtils.pathFromUri(requestSpec.getURI()) + " -> " + response.getStatusCode();
    }

}
