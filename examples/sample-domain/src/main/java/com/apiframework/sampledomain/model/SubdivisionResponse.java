package com.apiframework.sampledomain.model;

import java.util.List;

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
