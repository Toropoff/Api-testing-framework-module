package com.apiframework.core.filter;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class RequestResponseLoggingFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    private static final int MAX_LOG_BODY_CHARS = 65_536;

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

        String requestBody = safeRequestBody(requestSpec);
        LOGGER.info("Request: {} {} headers={} body={}",
            requestSpec.getMethod(),
            requestSpec.getURI(),
            requestHeaders,
            truncate(requestBody)
        );

        Response response = context.next(requestSpec, responseSpec);

        String responseBody = extractResponseBody(response);
        Map<String, String> responseHeaders = response.getHeaders().asList().stream()
            .collect(Collectors.toMap(
                header -> header.getName(),
                header -> header.getValue(),
                (first, second) -> second,
                LinkedHashMap::new
            ));

        LOGGER.info("Response: status={} headers={} body={}",
            response.getStatusCode(),
            responseHeaders,
            truncate(responseBody)
        );

        return response;
    }

    private static String extractResponseBody(Response response) {
        if (response.getBody() == null) {
            return "";
        }
        String contentType = response.getContentType();
        if (contentType != null && !isTextBased(contentType)) {
            return "<binary " + contentType + ">";
        }
        return response.getBody().asString();
    }

    private static boolean isTextBased(String contentType) {
        String lower = contentType.toLowerCase();
        return lower.contains("json") || lower.contains("xml")
            || lower.contains("text") || lower.contains("html")
            || lower.contains("form-urlencoded");
    }

    private static String truncate(String value) {
        if (value == null || value.length() <= MAX_LOG_BODY_CHARS) {
            return value;
        }
        return value.substring(0, MAX_LOG_BODY_CHARS) + "...<truncated>";
    }

    private static String safeRequestBody(FilterableRequestSpecification requestSpec) {
        try {
            Object body = requestSpec.getBody();
            return body == null ? "" : String.valueOf(body);
        } catch (ClassCastException ex) {
            LOGGER.warn("Unable to read request body for logging due to incompatible body type", ex);
            return "<unavailable>";
        }
    }
}
