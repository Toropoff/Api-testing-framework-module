package com.apiframework.splunk;

import com.apiframework.splunk.model.SplunkSearchResponse;
import com.apiframework.splunk.model.SplunkSearchResult;

// Static assertion helpers for validating Splunk log content in tests.
// All methods throw AssertionError with a diagnostic message on failure.
public final class SplunkLogAssertions {

    private SplunkLogAssertions() {}

    // Fails if the response is null or empty. queryContext is included in the error message.
    public static void assertHasResults(SplunkSearchResponse response, String queryContext) {
        if (response == null || response.isEmpty()) {
            throw new AssertionError(
                "Expected Splunk results for query [" + queryContext + "] but found none"
            );
        }
    }

    // Fails if the response does not contain exactly expectedCount results.
    public static void assertResultCount(SplunkSearchResponse response, int expectedCount) {
        if (response.size() != expectedCount) {
            throw new AssertionError(
                "Expected " + expectedCount + " Splunk result(s), but found " + response.size()
            );
        }
    }

    // Fails if the named field of a single result does not equal expectedValue.
    public static void assertFieldEquals(SplunkSearchResult result, String fieldName, String expectedValue) {
        String actual = result.field(fieldName);
        if (!expectedValue.equals(actual)) {
            throw new AssertionError(
                "Expected field '" + fieldName + "' = '" + expectedValue + "', actual = '" + actual + "'"
            );
        }
    }

    // Fails if the named field does not contain expectedSubstring (or is null).
    public static void assertFieldContains(SplunkSearchResult result, String fieldName, String expectedSubstring) {
        String actual = result.field(fieldName);
        if (actual == null || !actual.contains(expectedSubstring)) {
            throw new AssertionError(
                "Expected field '" + fieldName + "' to contain '" + expectedSubstring
                    + "', actual = '" + actual + "'"
            );
        }
    }

    // Fails if the _raw field does not contain expectedSubstring.
    public static void assertRawContains(SplunkSearchResult result, String expectedSubstring) {
        if (result.raw() == null || !result.raw().contains(expectedSubstring)) {
            throw new AssertionError(
                "Expected _raw to contain '" + expectedSubstring + "', actual: " + result.raw()
            );
        }
    }

    // Fails if the result's source field does not equal expectedSource.
    public static void assertSource(SplunkSearchResult result, String expectedSource) {
        if (!expectedSource.equals(result.source())) {
            throw new AssertionError(
                "Expected source '" + expectedSource + "', actual '" + result.source() + "'"
            );
        }
    }

    // Fails if the result's host field does not equal expectedHost.
    public static void assertHost(SplunkSearchResult result, String expectedHost) {
        if (!expectedHost.equals(result.host())) {
            throw new AssertionError(
                "Expected host '" + expectedHost + "', actual '" + result.host() + "'"
            );
        }
    }

    // Fails if no result in the response has fieldName == expectedValue.
    public static void assertAnyResultHasField(SplunkSearchResponse response, String fieldName, String expectedValue) {
        boolean found = response.results().stream()
            .anyMatch(r -> expectedValue.equals(r.field(fieldName)));
        if (!found) {
            throw new AssertionError(
                "No Splunk result has field '" + fieldName + "' = '" + expectedValue
                    + "' (total results: " + response.size() + ")"
            );
        }
    }
}
