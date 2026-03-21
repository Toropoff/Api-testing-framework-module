package com.apiframework.testsupport.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a test class as requiring a live API.
 * Tests in annotated classes are skipped unless {@code -Dframework.runLiveTests=true}.
 * Replaces the {@code requiresLiveApi()} override in {@link BaseApiTest}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LiveApi {
}
