package com.apiframework.tests.publicapi.assertions;

import com.apiframework.domains.openholidays.model.SubdivisionResponse;
import com.apiframework.model.ApiResponse;
import com.apiframework.testsupport.assertions.AbstractApiResponseAssert;

import java.util.function.Consumer;

/**
 * Domain-specific AssertJ assertions for {@code ApiResponse<SubdivisionResponse[]>}.
 *
 * <p>Extends {@link AbstractApiResponseAssert} to inherit {@code hasStatus}, {@code hasNonEmptyBody},
 * {@code matchesSchema}, and {@code matchesSnapshot}, and adds {@link #firstSubdivision} for
 * element-level assertions without breaking the fluent chain.
 *
 * <p>Allure steps are generated automatically by {@code AllureAspectJ} for all public methods —
 * no manual {@code Allure.step()} calls are needed.
 *
 * <p>Usage:
 * <pre>{@code
 * SubdivisionArrayResponseAssert.assertThat(response)
 *     .hasStatus(200)
 *     .hasNonEmptyBody()
 *     .firstSubdivision(s -> s
 *         .hasIsoCodeNotBlank()
 *         .hasShortNameNotBlank()
 *         .hasNameNotEmpty()
 *         .hasOfficialLanguagesNotEmpty());
 * }</pre>
 */
@SuppressWarnings("unchecked")
public final class SubdivisionArrayResponseAssert
        extends AbstractApiResponseAssert<SubdivisionArrayResponseAssert, SubdivisionResponse[]> {

    private SubdivisionArrayResponseAssert(ApiResponse<SubdivisionResponse[]> actual) {
        super(actual, (Class) SubdivisionArrayResponseAssert.class);
    }

    public static SubdivisionArrayResponseAssert assertThat(ApiResponse<SubdivisionResponse[]> actual) {
        return new SubdivisionArrayResponseAssert(actual);
    }

    /**
     * Runs domain-level assertions on the first element of the response body array.
     *
     * <p>Calls {@link #hasNonEmptyBody()} before accessing the element to surface a clear failure
     * message rather than an {@code ArrayIndexOutOfBoundsException}. The consumer receives a
     * {@link SubdivisionAssert} — its methods generate nested Allure sub-steps automatically.
     */
    public SubdivisionArrayResponseAssert firstSubdivision(Consumer<SubdivisionAssert> consumer) {
        hasNonEmptyBody();
        consumer.accept(new SubdivisionAssert(actual.body()[0]));
        return myself;
    }
}
