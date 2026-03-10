package com.apiframework.reporting.allure;

import com.apiframework.core.filter.CorrelationIdFilter;
import com.apiframework.reporting.mask.ReportingMaskingSupport;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.StepResult;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public final class AllureHttpStepFilter implements Filter {
    private static final String UNKNOWN = "<unknown>";

    private final boolean attachmentsEnabled;

    public AllureHttpStepFilter(boolean attachmentsEnabled) {
        this.attachmentsEnabled = attachmentsEnabled;
    }

    @Override
    public Response filter(
        FilterableRequestSpecification requestSpec,
        FilterableResponseSpecification responseSpec,
        FilterContext context
    ) {
        String stepId = UUID.randomUUID().toString();
        String stepName = buildStepName(requestSpec.getMethod(), requestSpec.getURI(), null, null);
        Allure.getLifecycle().startStep(stepId, new StepResult().setName(stepName));

        long start = System.nanoTime();
        try {
            if (attachmentsEnabled) {
                addRequestAttachment(requestSpec);
            }

            Response response = context.next(requestSpec, responseSpec);
            long durationMs = elapsedMs(start);

            Allure.getLifecycle().updateStep(stepId,
                step -> step.setName(buildStepName(requestSpec.getMethod(), requestSpec.getURI(), null, response.getStatusCode()))
            );

            if (attachmentsEnabled) {
                addResponseAttachment(response);
                addMetadataAttachment(requestSpec, response, durationMs);
                addOptionalAttachments(requestSpec, response);
            }

            Allure.getLifecycle().updateStep(stepId, step -> step.setStatus(resolveStatus(response.getStatusCode())));
            return response;
        } catch (Throwable throwable) {
            long durationMs = elapsedMs(start);
            if (attachmentsEnabled) {
                addMetadataAttachment(requestSpec, null, durationMs);
                Allure.addAttachment("Error Summary", throwable.toString());
            }
            Allure.getLifecycle().updateStep(stepId, step -> {
                step.setStatus(Status.BROKEN);
                step.setStatusDetails(new StatusDetails().setMessage(throwable.getMessage()));
            });
            throw throwable;
        } finally {
            Allure.getLifecycle().stopStep(stepId);
        }
    }

    private void addRequestAttachment(FilterableRequestSpecification requestSpec) {
        Map<String, String> requestHeaders = requestSpec.getHeaders().asList().stream()
            .collect(Collectors.toMap(
                header -> header.getName(),
                header -> header.getValue(),
                (first, second) -> second,
                LinkedHashMap::new
            ));

        String requestBody = safeRequestBody(requestSpec);
        Allure.addAttachment(
            "HTTP Request",
            "text/plain",
            formatRequest(requestSpec.getMethod(), requestSpec.getURI(), requestHeaders, requestBody),
            ".txt"
        );
    }

    private void addResponseAttachment(Response response) {
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
    }

    private void addMetadataAttachment(FilterableRequestSpecification requestSpec, Response response, long durationMs) {
        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put("method", requestSpec.getMethod());
        metadata.put("path", pathFromUri(requestSpec.getURI()));
        metadata.put("uri", requestSpec.getURI());
        metadata.put("statusCode", response == null ? UNKNOWN : String.valueOf(response.getStatusCode()));
        metadata.put("durationMs", String.valueOf(durationMs));
        metadata.put("correlationId", String.valueOf(requestSpec.getHeaders().getValue(CorrelationIdFilter.HEADER_NAME)));
        metadata.put("profile", System.getProperty("framework.profile", UNKNOWN));
        metadata.put("environment", System.getProperty("framework.environment", UNKNOWN));

        String content = metadata.entrySet().stream()
            .map(entry -> entry.getKey() + "=" + ReportingMaskingSupport.maskBody(entry.getValue()))
            .collect(Collectors.joining("\n", "", "\n"));

        Allure.addAttachment("HTTP Metadata", "text/plain", content, ".txt");
    }

    private void addOptionalAttachments(FilterableRequestSpecification requestSpec, Response response) {
        Allure.addAttachment("cURL Preview", "text/plain", buildCurlPreview(requestSpec), ".txt");

        String contentType = response.getContentType();
        if (contentType != null && contentType.contains("json")) {
            String prettyBody = response.getBody() == null ? "" : response.getBody().prettyPrint();
            Allure.addAttachment(
                "Pretty JSON Response",
                "application/json",
                ReportingMaskingSupport.maskBody(prettyBody),
                ".json"
            );
        }

        if (response.getStatusCode() >= 400) {
            String summary = "status=" + response.getStatusCode() + "\n"
                + "method=" + requestSpec.getMethod() + "\n"
                + "path=" + pathFromUri(requestSpec.getURI()) + "\n";
            Allure.addAttachment("Error Summary", "text/plain", summary, ".txt");
        }
    }

    private String buildStepName(String method, String uri, String profile, Integer statusCode) {
        String path = pathFromUri(uri);
        String status = statusCode == null ? "..." : String.valueOf(statusCode);
        if (profile == null || profile.isBlank()) {
            return method + " " + path + " -> " + status;
        }
        return method + " " + path + " [" + profile + "] -> " + status;
    }

    private String safeRequestBody(FilterableRequestSpecification requestSpec) {
        try {
            Object body = requestSpec.getBody();
            return body == null ? "" : String.valueOf(body);
        } catch (ClassCastException ex) {
            return "<unavailable>";
        }
    }

    private String formatRequest(String method, String uri, Map<String, String> headers, String body) {
        return "method=" + method + "\n"
            + "path=" + pathFromUri(uri) + "\n"
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

    private String buildCurlPreview(FilterableRequestSpecification requestSpec) {
        StringBuilder builder = new StringBuilder("curl -X ")
            .append(requestSpec.getMethod())
            .append(" '")
            .append(requestSpec.getURI())
            .append("'");

        requestSpec.getHeaders().asList().forEach(header -> builder
            .append(" -H '")
            .append(header.getName())
            .append(": ")
            .append(ReportingMaskingSupport.maskBody(header.getValue()))
            .append("'"));

        String body = safeRequestBody(requestSpec);
        if (!body.isBlank()) {
            builder.append(" --data '").append(ReportingMaskingSupport.maskBody(body)).append("'");
        }

        return builder.append('\n').toString();
    }

    private String pathFromUri(String uri) {
        try {
            URI parsed = new URI(uri);
            String path = parsed.getPath();
            return path == null || path.isBlank() ? "/" : path;
        } catch (URISyntaxException exception) {
            return uri;
        }
    }

    private long elapsedMs(long startedAtNanos) {
        return (System.nanoTime() - startedAtNanos) / 1_000_000;
    }

    private Status resolveStatus(int statusCode) {
        if (statusCode >= 500) {
            return Status.BROKEN;
        }
        if (statusCode >= 400) {
            return Status.FAILED;
        }
        return Status.PASSED;
    }
}
