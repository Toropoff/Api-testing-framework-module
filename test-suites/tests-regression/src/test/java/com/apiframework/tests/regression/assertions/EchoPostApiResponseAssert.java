package com.apiframework.tests.regression.assertions;

import com.apiframework.domains.postmanecho.model.EchoPayload;
import com.apiframework.domains.postmanecho.model.EchoPostResponse;
import com.apiframework.model.ApiResponse;
import com.apiframework.testsupport.assertions.AbstractApiResponseAssert;

/**
 * Domain-specific AssertJ assertions for {@code ApiResponse<EchoPostResponse>}.
 *
 * <p>Extends {@link AbstractApiResponseAssert} to inherit {@code hasStatus}, {@code hasNonEmptyBody},
 * {@code matchesSchema}, and {@code matchesSnapshot}, and adds {@link #hasJsonEqualTo} for
 * asserting that all echoed JSON payload fields match the original request values.
 *
 * <p>Allure steps are generated automatically by {@code AllureAspectJ} for all public methods —
 * no manual {@code Allure.step()} calls are needed.
 *
 * <p>Usage:
 * <pre>{@code
 * EchoPostApiResponseAssert.assertThat(response)
 *     .hasStatus(200)
 *     .hasJsonEqualTo(payload);
 * }</pre>
 */
@SuppressWarnings("unchecked")
public final class EchoPostApiResponseAssert
        extends AbstractApiResponseAssert<EchoPostApiResponseAssert, EchoPostResponse> {

    private EchoPostApiResponseAssert(ApiResponse<EchoPostResponse> actual) {
        super(actual, (Class) EchoPostApiResponseAssert.class);
    }

    public static EchoPostApiResponseAssert assertThat(ApiResponse<EchoPostResponse> actual) {
        return new EchoPostApiResponseAssert(actual);
    }

    /**
     * Asserts that the echoed {@code json} body matches all fields of the given {@link EchoPayload}.
     *
     * <p>Checks {@code event}, {@code amount}, and {@code active} in sequence, failing with a
     * field-specific message on the first mismatch. Fails early if {@code json} is null.
     */
    public EchoPostApiResponseAssert hasJsonEqualTo(EchoPayload expected) {
        isNotNull();
        EchoPayload json = actual.body().json();
        if (json == null) {
            failWithMessage("Expected non-null json body but was null");
        }
        if (!expected.event().equals(json.event())) {
            failWithMessage("Expected json.event <%s> but was <%s>", expected.event(), json.event());
        }
        if (expected.amount() != json.amount()) {
            failWithMessage("Expected json.amount <%d> but was <%d>", expected.amount(), json.amount());
        }
        if (expected.active() != json.active()) {
            failWithMessage("Expected json.active <%b> but was <%b>", expected.active(), json.active());
        }
        return myself;
    }
}
