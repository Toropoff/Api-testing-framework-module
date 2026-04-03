package com.apiframework.testsupport.assertions;

import com.apiframework.model.ApiResponse;
import com.apiframework.testsupport.contracts.JsonSchemaContractValidator;
import com.apiframework.testsupport.contracts.SnapshotContractValidator;
import org.assertj.core.api.AbstractAssert;

/**
 * Generic AssertJ base class for {@link ApiResponse} assertions.
 *
 * <p>Allure steps are generated automatically — {@code AllureAspectJ} intercepts every public
 * method on {@code AbstractAssert} subclasses via LTW, so each fluent call produces a named step
 * in the Allure report without any manual {@code Allure.step()} annotation in this class.
 *
 * <p>Validators are held as static singletons rather than instance fields: they are stateless
 * (no mutable state, thread-safe), so a single shared instance per assertion class is sufficient
 * and avoids repeated construction on every assertion chain.
 *
 * @param <SELF> the concrete assertion type (for fluent chaining)
 * @param <T>    the response body type
 */
public abstract class AbstractApiResponseAssert<SELF extends AbstractApiResponseAssert<SELF, T>, T>
        extends AbstractAssert<SELF, ApiResponse<T>> {

    protected static final SnapshotContractValidator SNAPSHOT_VALIDATOR = new SnapshotContractValidator();
    protected static final JsonSchemaContractValidator SCHEMA_VALIDATOR = new JsonSchemaContractValidator();

    protected AbstractApiResponseAssert(ApiResponse<T> actual, Class<SELF> selfType) {
        super(actual, selfType);
    }

    /**
     * Asserts that the HTTP status code equals {@code expected}.
     */
    public SELF hasStatus(int expected) {
        isNotNull();
        if (actual.statusCode() != expected) {
            failWithMessage("Expected status <%d> but was <%d>", expected, actual.statusCode());
        }
        return myself;
    }

    /**
     * Asserts that the deserialized response body is non-null, and non-empty when it is an array.
     *
     * <p>For non-array body types the method only checks non-null; emptiness semantics are
     * type-specific and should be verified via domain-level assertion methods.
     */
    public SELF hasNonEmptyBody() {
        isNotNull();
        T body = actual.body();
        if (body == null) {
            failWithMessage("Expected non-null body but was null");
        }
        if (body instanceof Object[] arr && arr.length == 0) {
            failWithMessage("Expected non-empty body but was empty");
        }
        return myself;
    }

    /**
     * Validates the raw JSON body against the JSON Schema at {@code classpathSchemaPath}.
     *
     * <p>Delegates to {@link JsonSchemaContractValidator}; the step name in Allure comes from
     * AspectJ intercepting this method call, not from an explicit {@code Allure.step()}.
     */
    public SELF matchesSchema(String classpathSchemaPath) {
        SCHEMA_VALIDATOR.assertMatchesSchema(actual.rawBody(), classpathSchemaPath);
        return myself;
    }

    /**
     * Validates the raw JSON body against a golden-file snapshot named {@code snapshotName}.
     *
     * <p>Delegates to {@link SnapshotContractValidator}; the step name in Allure comes from
     * AspectJ intercepting this method call, not from an explicit {@code Allure.step()}.
     */
    public SELF matchesSnapshot(String snapshotName) {
        SNAPSHOT_VALIDATOR.assertMatchesSnapshot(snapshotName, actual.rawBody());
        return myself;
    }
}
