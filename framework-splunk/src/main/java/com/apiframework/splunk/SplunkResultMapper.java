package com.apiframework.splunk;

import com.apiframework.json.JacksonProvider;
import com.apiframework.splunk.model.SplunkSearchResult;
import com.fasterxml.jackson.databind.ObjectMapper;

// Deserializes Splunk result content into typed Java objects via Jackson.
// Wraps JacksonProvider.defaultMapper() and provides consistent error wrapping.
public final class SplunkResultMapper {

    private final ObjectMapper objectMapper;

    public SplunkResultMapper() {
        this.objectMapper = JacksonProvider.defaultMapper();
    }

    // Parses the _raw field of a result as JSON into targetClass.
    // Use when _raw contains a structured JSON log payload.
    public <T> T mapRaw(SplunkSearchResult result, Class<T> targetClass) {
        String raw = result.raw();
        if (raw == null || raw.isBlank()) {
            throw new IllegalStateException("Splunk result _raw field is empty, cannot map to "
                + targetClass.getSimpleName());
        }
        try {
            return objectMapper.readValue(raw, targetClass);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to map Splunk _raw field to "
                + targetClass.getSimpleName(), e);
        }
    }

    // Parses the string value of a named field from the result's fields map into targetClass.
    public <T> T mapField(SplunkSearchResult result, String fieldName, Class<T> targetClass) {
        String value = result.field(fieldName);
        if (value == null) {
            throw new IllegalStateException("Field '" + fieldName + "' not found in Splunk result");
        }
        try {
            return objectMapper.readValue(value, targetClass);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to map Splunk field '" + fieldName + "' to "
                + targetClass.getSimpleName(), e);
        }
    }

    // Parses an arbitrary JSON string into targetClass. Direct Jackson wrapper for consistency.
    public <T> T map(String json, Class<T> targetClass) {
        try {
            return objectMapper.readValue(json, targetClass);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to map JSON to " + targetClass.getSimpleName(), e);
        }
    }
}
