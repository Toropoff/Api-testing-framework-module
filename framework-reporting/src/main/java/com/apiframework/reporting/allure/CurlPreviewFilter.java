package com.apiframework.reporting.allure;

import io.qameta.allure.Allure;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/**
 * REST Assured filter that attaches a cURL command preview to Allure reports.
 * Complements the native {@code AllureRestAssured} filter with a reproducible
 * command-line representation of each HTTP request.
 */
public final class CurlPreviewFilter implements Filter {

    @Override
    public Response filter(
        FilterableRequestSpecification requestSpec,
        FilterableResponseSpecification responseSpec,
        FilterContext context
    ) {
        Allure.addAttachment("cURL Preview", "text/plain", buildCurl(requestSpec), ".txt");
        return context.next(requestSpec, responseSpec);
    }

    private static String buildCurl(FilterableRequestSpecification requestSpec) {
        StringBuilder builder = new StringBuilder("curl -X ")
            .append(requestSpec.getMethod())
            .append(" '")
            .append(requestSpec.getURI())
            .append("'");
        requestSpec.getHeaders().asList().forEach(header ->
            builder.append(" -H '")
                .append(header.getName())
                .append(": ")
                .append(header.getValue())
                .append("'"));
        Object body = requestSpec.getBody();
        if (body != null && !String.valueOf(body).isBlank()) {
            builder.append(" --data '").append(body).append("'");
        }
        return builder.append('\n').toString();
    }
}
