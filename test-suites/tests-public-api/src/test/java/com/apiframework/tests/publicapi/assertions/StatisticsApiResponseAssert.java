package com.apiframework.tests.publicapi.assertions;

import com.apiframework.domains.openholidays.model.StatisticsResponse;
import com.apiframework.model.ApiResponse;
import com.apiframework.testsupport.assertions.AbstractApiResponseAssert;

/**
 * Domain-specific AssertJ assertions for {@code ApiResponse<StatisticsResponse>}.
 *
 * <p>Extends {@link AbstractApiResponseAssert} to inherit {@code hasStatus}, {@code hasNonEmptyBody},
 * {@code matchesSchema}, and {@code matchesSnapshot}, and adds body-field assertions for
 * {@code oldestStartDate} and {@code youngestStartDate}.
 *
 * <p>Allure steps are generated automatically by {@code AllureAspectJ} for all public methods —
 * no manual {@code Allure.step()} calls are needed.
 *
 * <p>Usage:
 * <pre>{@code
 * StatisticsApiResponseAssert.assertThat(response)
 *     .hasStatus(200)
 *     .hasNonEmptyBody()
 *     .hasNonBlankOldestStartDate()
 *     .hasNonBlankYoungestStartDate()
 *     .matchesSchema("schemas/statistics-public-holidays.schema.json")
 *     .matchesSnapshot("statistics-public-holidays");
 * }</pre>
 */
@SuppressWarnings("unchecked")
public final class StatisticsApiResponseAssert
        extends AbstractApiResponseAssert<StatisticsApiResponseAssert, StatisticsResponse> {

    private StatisticsApiResponseAssert(ApiResponse<StatisticsResponse> actual) {
        super(actual, (Class) StatisticsApiResponseAssert.class);
    }

    public static StatisticsApiResponseAssert assertThat(ApiResponse<StatisticsResponse> actual) {
        return new StatisticsApiResponseAssert(actual);
    }

    /** Asserts that {@code oldestStartDate} is non-null and non-blank. */
    public StatisticsApiResponseAssert hasNonBlankOldestStartDate() {
        isNotNull();
        String date = actual.body().oldestStartDate();
        if (date == null || date.isBlank()) {
            failWithMessage("Expected non-blank oldestStartDate but was: %s", date);
        }
        return myself;
    }

    /** Asserts that {@code youngestStartDate} is non-null and non-blank. */
    public StatisticsApiResponseAssert hasNonBlankYoungestStartDate() {
        isNotNull();
        String date = actual.body().youngestStartDate();
        if (date == null || date.isBlank()) {
            failWithMessage("Expected non-blank youngestStartDate but was: %s", date);
        }
        return myself;
    }
}
