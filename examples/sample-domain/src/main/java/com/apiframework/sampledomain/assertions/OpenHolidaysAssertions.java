package com.apiframework.sampledomain.assertions;

import com.apiframework.core.model.ApiResponse;
import com.apiframework.reporting.steps.AllureActionExecutor;
import com.apiframework.sampledomain.model.SubdivisionResponse;

import static org.assertj.core.api.Assertions.assertThat;

public final class OpenHolidaysAssertions {
    private static final AllureActionExecutor EXECUTOR = new AllureActionExecutor();

    private OpenHolidaysAssertions() {
    }

    /**
     * Asserts a successful GET /Subdivisions response:
     * HTTP 200, non-empty array, and well-formed first entry
     * (isoCode, shortName, name list, officialLanguages all present).
     */
    public static void assertSubdivisionsReturned(ApiResponse<SubdivisionResponse[]> response) {
        EXECUTOR.assertion("Verify subdivisions response", () -> {
            assertThat(response.statusCode())
                .as("response status")
                .isEqualTo(200);
            assertThat(response.body())
                .as("response body")
                .isNotNull()
                .isNotEmpty();

            SubdivisionResponse first = response.body()[0];
            assertThat(first.isoCode())
                .as("first subdivision isoCode")
                .isNotBlank();
            assertThat(first.shortName())
                .as("first subdivision shortName")
                .isNotBlank();
            assertThat(first.name())
                .as("first subdivision name list")
                .isNotEmpty();
            assertThat(first.officialLanguages())
                .as("first subdivision officialLanguages")
                .isNotEmpty();
        });
    }

    /**
     * Asserts that the response status code is 400 Bad Request.
     */
    public static void assertBadRequest(ApiResponse<String> response) {
        EXECUTOR.assertion("Verify 400 Bad Request status",
            () -> assertThat(response.statusCode()).isEqualTo(400));
    }
}
