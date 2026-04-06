package com.apiframework.openholidays.model;

public record StatisticsResponse(
        String oldestStartDate,
        String youngestStartDate
) {
}
