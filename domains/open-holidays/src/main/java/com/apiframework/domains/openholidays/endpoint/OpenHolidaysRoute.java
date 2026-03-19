package com.apiframework.domains.openholidays.endpoint;

enum OpenHolidaysRoute {
    SUBDIVISIONS("/Subdivisions");

    private final String path;

    OpenHolidaysRoute(String path) {
        this.path = path;
    }

    String path() {
        return path;
    }
}
