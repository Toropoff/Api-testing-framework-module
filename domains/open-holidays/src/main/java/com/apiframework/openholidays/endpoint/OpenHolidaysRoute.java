package com.apiframework.openholidays.endpoint;

enum OpenHolidaysRoute {
    SUBDIVISIONS("/Subdivisions"),
    SCHOOL_HOLIDAYS_BY_DATE("/SchoolHolidaysByDate"),
    PUBLIC_HOLIDAYS_BY_DATE("/PublicHolidaysByDate"),
    STATISTICS_SCHOOL_HOLIDAYS("/Statistics/SchoolHolidays"),
    STATISTICS_PUBLIC_HOLIDAYS("/Statistics/PublicHolidays");

    private final String path;

    OpenHolidaysRoute(String path) {
        this.path = path;
    }

    String path() {
        return path;
    }
}
