package com.apiframework.tests.publicapi.assertions;

import com.apiframework.domains.openholidays.model.HolidayByDateResponse;
import org.assertj.core.api.AbstractAssert;

/**
 * Domain-specific AssertJ assertions for {@link HolidayByDateResponse}.
 *
 * <p>Instantiated inside {@link HolidayArrayResponseAssert#firstHoliday} via a
 * {@code Consumer} lambda — Allure steps are generated automatically for each method call
 * by {@code AllureAspectJ} (which intercepts all public {@code AbstractAssert} subclass methods).
 * No manual {@code Allure.step()} is needed here.
 *
 * <p>Kept intentionally narrow — only the assertions required by the public-api test suite.
 */
public final class HolidayByDateAssert extends AbstractAssert<HolidayByDateAssert, HolidayByDateResponse> {

    public HolidayByDateAssert(HolidayByDateResponse actual) {
        super(actual, HolidayByDateAssert.class);
    }

    /** Asserts that the holiday type equals {@code expected} (e.g. {@code "Public"}, {@code "School"}). */
    public HolidayByDateAssert hasType(String expected) {
        isNotNull();
        if (!expected.equals(actual.type())) {
            failWithMessage("Expected holiday type <%s> but was <%s>", expected, actual.type());
        }
        return this;
    }

    /** Asserts that the {@code country} field is non-null. */
    public HolidayByDateAssert hasCountry() {
        isNotNull();
        if (actual.country() == null) {
            failWithMessage("Expected country to be non-null");
        }
        return this;
    }

    /** Asserts that {@code country.isoCode} is non-null and non-blank. */
    public HolidayByDateAssert hasCountryIsoCodeNotBlank() {
        isNotNull();
        if (actual.country() == null || actual.country().isoCode() == null || actual.country().isoCode().isBlank()) {
            failWithMessage("Expected non-blank country ISO code but was: %s",
                    actual.country() == null ? "null country" : actual.country().isoCode());
        }
        return this;
    }
}
