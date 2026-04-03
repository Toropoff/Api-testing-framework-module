package com.apiframework.tests.smoke.assertions;

import com.apiframework.domains.postmanecho.model.EchoGetResponse;
import com.apiframework.model.ApiResponse;
import com.apiframework.testsupport.assertions.AbstractApiResponseAssert;

/**
 * Domain-specific AssertJ assertions for {@code ApiResponse<EchoGetResponse>}.
 *
 * <p>Extends {@link AbstractApiResponseAssert} to inherit {@code hasStatus}, {@code hasNonEmptyBody},
 * {@code matchesSchema}, and {@code matchesSnapshot}, and adds {@link #hasArgsEntry} for
 * validating individual query-parameter echoes in the {@code args} map.
 *
 * <p>Allure steps are generated automatically by {@code AllureAspectJ} for all public methods —
 * no manual {@code Allure.step()} calls are needed.
 *
 * <p>Usage:
 * <pre>{@code
 * EchoGetApiResponseAssert.assertThat(response)
 *     .hasStatus(200)
 *     .hasArgsEntry("suite", "smoke");
 * }</pre>
 */
@SuppressWarnings("unchecked")
public final class EchoGetApiResponseAssert
        extends AbstractApiResponseAssert<EchoGetApiResponseAssert, EchoGetResponse> {

    private EchoGetApiResponseAssert(ApiResponse<EchoGetResponse> actual) {
        super(actual, (Class) EchoGetApiResponseAssert.class);
    }

    public static EchoGetApiResponseAssert assertThat(ApiResponse<EchoGetResponse> actual) {
        return new EchoGetApiResponseAssert(actual);
    }

    /**
     * Asserts that the {@code args} map contains an entry with the given key and value.
     *
     * <p>Fails with a clear message if {@code args} is null or does not contain the expected entry.
     */
    public EchoGetApiResponseAssert hasArgsEntry(String key, String value) {
        isNotNull();
        var args = actual.body().args();
        if (args == null) {
            failWithMessage("Expected args map to contain <%s=%s> but args was null", key, value);
        }
        if (!args.containsKey(key) || !value.equals(args.get(key))) {
            failWithMessage("Expected args map to contain <%s=%s> but was <%s>", key, value, args);
        }
        return myself;
    }
}
