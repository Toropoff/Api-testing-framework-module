package com.apiframework.tests.publicapi.assertions;

import com.apiframework.domains.openholidays.model.SubdivisionResponse;
import org.assertj.core.api.AbstractAssert;

/**
 * Domain-specific AssertJ assertions for {@link SubdivisionResponse}.
 *
 * <p>Instantiated inside {@link SubdivisionArrayResponseAssert#firstSubdivision} via a
 * {@code Consumer} lambda — Allure steps are generated automatically for each method call
 * by {@code AllureAspectJ} (which intercepts all public {@code AbstractAssert} subclass methods).
 * No manual {@code Allure.step()} is needed here.
 *
 * <p>Kept intentionally narrow — only the assertions required by the public-api test suite.
 */
public final class SubdivisionAssert extends AbstractAssert<SubdivisionAssert, SubdivisionResponse> {

    public SubdivisionAssert(SubdivisionResponse actual) {
        super(actual, SubdivisionAssert.class);
    }

    /** Asserts that {@code isoCode} is non-null and non-blank. */
    public SubdivisionAssert hasIsoCodeNotBlank() {
        isNotNull();
        if (actual.isoCode() == null || actual.isoCode().isBlank()) {
            failWithMessage("Expected non-blank isoCode but was: %s", actual.isoCode());
        }
        return this;
    }

    /** Asserts that {@code shortName} is non-null and non-blank. */
    public SubdivisionAssert hasShortNameNotBlank() {
        isNotNull();
        if (actual.shortName() == null || actual.shortName().isBlank()) {
            failWithMessage("Expected non-blank shortName but was: %s", actual.shortName());
        }
        return this;
    }

    /** Asserts that the {@code name} list is non-null and non-empty. */
    public SubdivisionAssert hasNameNotEmpty() {
        isNotNull();
        if (actual.name() == null || actual.name().isEmpty()) {
            failWithMessage("Expected non-empty name list but was: %s", actual.name());
        }
        return this;
    }

    /** Asserts that the {@code officialLanguages} list is non-null and non-empty. */
    public SubdivisionAssert hasOfficialLanguagesNotEmpty() {
        isNotNull();
        if (actual.officialLanguages() == null || actual.officialLanguages().isEmpty()) {
            failWithMessage("Expected non-empty officialLanguages but was: %s", actual.officialLanguages());
        }
        return this;
    }
}
