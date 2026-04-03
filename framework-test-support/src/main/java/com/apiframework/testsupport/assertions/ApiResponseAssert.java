package com.apiframework.testsupport.assertions;

import com.apiframework.model.ApiResponse;

/**
 * Generic {@link ApiResponse} assertion for use when no domain-specific sub-class exists.
 *
 * <p>Domain modules that need richer assertions (e.g. per-body-field checks or collection
 * traversal) should extend {@link AbstractApiResponseAssert} directly and add domain methods,
 * rather than adding them here.
 *
 * <p>Usage:
 * <pre>{@code
 * ApiResponseAssert.assertThat(response)
 *     .hasStatus(200)
 *     .hasNonEmptyBody()
 *     .matchesSchema("schemas/my-schema.json")
 *     .matchesSnapshot("my-snapshot");
 * }</pre>
 *
 * @param <T> the response body type
 */
@SuppressWarnings("unchecked")
public final class ApiResponseAssert<T>
        extends AbstractApiResponseAssert<ApiResponseAssert<T>, T> {

    private ApiResponseAssert(ApiResponse<T> actual) {
        // Raw cast is intentional: AssertJ's fluent chain requires the concrete type parameter.
        // The cast is safe because SELF is always ApiResponseAssert<T> here.
        super(actual, (Class) ApiResponseAssert.class);
    }

    public static <T> ApiResponseAssert<T> assertThat(ApiResponse<T> actual) {
        return new ApiResponseAssert<>(actual);
    }
}
