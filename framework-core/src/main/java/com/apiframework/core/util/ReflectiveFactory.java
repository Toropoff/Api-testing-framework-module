package com.apiframework.core.util;

import java.util.function.Supplier;

/**
 * Utility for instantiating classes by name from system properties,
 * with a type-safe fallback. Used by retry and reporting configuration
 * to support pluggable strategies.
 */
public final class ReflectiveFactory {

    private ReflectiveFactory() {
    }

    /**
     * Instantiates a class by name if the property is set; otherwise returns the fallback.
     *
     * @param className    fully-qualified class name (nullable — returns fallback if null/blank)
     * @param expectedType the interface or superclass the loaded class must implement
     * @param fallback     supplier for the default instance when className is absent
     * @param <T>          the expected type
     * @return an instance of the configured or fallback class
     */
    public static <T> T instantiateOrDefault(String className, Class<T> expectedType, Supplier<T> fallback) {
        if (className == null || className.isBlank()) {
            return fallback.get();
        }
        try {
            Class<?> loadedClass = Class.forName(className);
            if (!expectedType.isAssignableFrom(loadedClass)) {
                throw new IllegalArgumentException(
                    "Class " + className + " must implement " + expectedType.getName());
            }
            Object instance = loadedClass.getDeclaredConstructor().newInstance();
            return expectedType.cast(instance);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to instantiate: " + className, e);
        }
    }
}
