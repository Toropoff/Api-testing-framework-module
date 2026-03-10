package com.apiframework.reporting.allure;

import com.apiframework.reporting.mask.ReportingMaskingSupport;
import io.qameta.allure.Allure;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class AllureRequestResponseFilter implements Filter {
    @Override
    public Response filter(
        FilterableRequestSpecification requestSpec,
        FilterableResponseSpecification responseSpec,
        FilterContext context
    ) {
        Map<String, String> requestHeaders = requestSpec.getHeaders().asList().stream()
            .collect(Collectors.toMap(
                header -> header.getName(),
                header -> header.getValue(),
                (first, second) -> second,
                LinkedHashMap::new
            ));
        String requestBody = requestSpec.getBody() == null ? "" : String.valueOf(requestSpec.getBody());

        Allure.addAttachment(
            "HTTP Request",
            "text/plain",
            formatRequest(requestSpec.getMethod(), requestSpec.getURI(), requestHeaders, requestBody),
            ".txt"
        );

        Response response = context.next(requestSpec, responseSpec);

        Map<String, String> responseHeaders = response.getHeaders().asList().stream()
            .collect(Collectors.toMap(
                header -> header.getName(),
                header -> header.getValue(),
                (first, second) -> second,
                LinkedHashMap::new
            ));
        String responseBody = response.getBody() == null ? "" : response.getBody().asString();

        Allure.addAttachment(
            "HTTP Response",
            "text/plain",
            formatResponse(response.getStatusCode(), response.time(), responseHeaders, responseBody),
            ".txt"
        );

        return response;
    }

    private String formatRequest(String method, String uri, Map<String, String> headers, String body) {
        return "method=" + method + "\n"
            + "uri=" + uri + "\n"
            + "headers=" + ReportingMaskingSupport.maskHeaders(headers) + "\n"
            + "body=" + ReportingMaskingSupport.maskBody(body) + "\n";
    }

    private String formatResponse(int status, long timingMs, Map<String, String> headers, String body) {
        return "status=" + status + "\n"
            + "timingMs=" + timingMs + "\n"
            + "headers=" + ReportingMaskingSupport.maskHeaders(headers) + "\n"
            + "body=" + ReportingMaskingSupport.maskBody(body) + "\n";
    }
}
