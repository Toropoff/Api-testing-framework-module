package com.apiframework.tests.publicapi.assertions;

import com.apiframework.domains.openholidays.model.HolidayByDateResponse;
import com.apiframework.model.ApiResponse;
import com.apiframework.testsupport.assertions.AbstractApiResponseAssert;

import java.util.function.Consumer;

/**
 * Domain-specific AssertJ assertions for {@code ApiResponse<HolidayByDateResponse[]>}.
 *
 * <p>Extends {@link AbstractApiResponseAssert} to inherit {@code hasStatus}, {@code hasNonEmptyBody},
 * {@code matchesSchema}, and {@code matchesSnapshot}, and adds {@link #firstHoliday} for
 * element-level assertions without breaking the fluent chain.
 *
 * <p>Allure steps are generated automatically by {@code AllureAspectJ} for all public methods —
 * no manual {@code Allure.step()} calls are needed.
 *
 * <p>Usage:
 * <pre>{@code
 * HolidayArrayResponseAssert.assertThat(response)
 *     .hasStatus(200)
 *     .hasNonEmptyBody()
 *     .firstHoliday(h -> h
 *         .hasType("Public")
 *         .hasCountry()
 *         .hasCountryIsoCodeNotBlank())
 *     .matchesSchema("schemas/public-holidays-by-date.schema.json")
 *     .matchesSnapshot("public-holidays-by-date");
 * }</pre>
 */
@SuppressWarnings("unchecked")
public final class HolidayArrayResponseAssert
        extends AbstractApiResponseAssert<HolidayArrayResponseAssert, HolidayByDateResponse[]> {

    private HolidayArrayResponseAssert(ApiResponse<HolidayByDateResponse[]> actual) {
        // Raw cast mirrors the pattern in ApiResponseAssert — safe because SELF is always this type.
        super(actual, (Class) HolidayArrayResponseAssert.class);
    }

    public static HolidayArrayResponseAssert assertThat(ApiResponse<HolidayByDateResponse[]> actual) {
        return new HolidayArrayResponseAssert(actual);
    }

    /**
     * Runs domain-level assertions on the first element of the response body array.
     *
     * <p>Calls {@link #hasNonEmptyBody()} before accessing the element to surface a clear failure
     * message rather than an {@code ArrayIndexOutOfBoundsException}. The consumer receives a
     * {@link HolidayByDateAssert} — its methods generate nested Allure sub-steps automatically.
     */
    public HolidayArrayResponseAssert firstHoliday(Consumer<HolidayByDateAssert> consumer) {
        hasNonEmptyBody();
        consumer.accept(new HolidayByDateAssert(actual.body()[0]));
        return myself;
    }
}
