package com.apiframework.sampledomain.openholidays.model;

import java.util.List;

/**
 * Represents a single subdivision entry from GET /Subdivisions.
 * All optional/nullable fields are mapped to {@code null} when absent in the JSON response.
 * Unknown fields are silently ignored by the global Jackson ObjectMapper configuration.
 */
public record SubdivisionResponse(
    List<LocalizedText> category,
    String code,
    List<LocalizedText> name,
    String shortName,
    List<String> officialLanguages,
    String isoCode,
    List<SubdivisionResponse> children,
    List<Object> groups,
    List<LocalizedText> comment
) {
}
