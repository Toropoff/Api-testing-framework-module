package com.apiframework.domains.openholidays.model;

public record StatisticsResponse(
        String oldestStartDate,
        String youngestStartDate
) {
}
