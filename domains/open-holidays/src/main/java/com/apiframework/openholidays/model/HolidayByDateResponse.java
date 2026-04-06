package com.apiframework.openholidays.model;

import java.util.List;

public record HolidayByDateResponse(
        String id,
        List<LocalizedText> name,
        CountryReference country,
        String type,
        boolean nationwide,
        String startDate,
        String endDate,
        List<Object> subdivisions,
        List<Object> groups,
        String regionalScope,
        String temporalScope,
        List<LocalizedText> comment,
        List<String> tags
) {
}
