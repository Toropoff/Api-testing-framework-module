package com.apiframework.sampledomain.openholidays.model;

/**
 * A localized text entry returned by the OpenHolidays API.
 * The {@code language} field holds an ISO-639-1 code (e.g. "EN", "DE").
 */
public record LocalizedText(String language, String text) {
}
