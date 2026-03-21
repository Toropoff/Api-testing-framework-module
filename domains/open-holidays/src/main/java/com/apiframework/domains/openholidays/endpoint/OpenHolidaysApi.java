package com.apiframework.domains.openholidays.endpoint;

import com.apiframework.core.config.DomainConfig;
import com.apiframework.core.http.HttpClient;
import com.apiframework.core.model.ApiResponse;
import com.apiframework.domains.openholidays.model.SubdivisionResponse;

import java.util.Map;
import java.util.Objects;

public final class OpenHolidaysApi {
    private static final String BASE_URL = DomainConfig.loadBaseUrl(OpenHolidaysApi.class, "open-holidays.properties");

    public static String baseUrl() {
        return BASE_URL;
    }

    private final HttpClient httpClient;

    public OpenHolidaysApi(HttpClient httpClient) {
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient must not be null");
    }

    public ApiResponse<SubdivisionResponse[]> getSubdivisions(String countryIsoCode, String languageIsoCode) {
        return httpClient.get(
            OpenHolidaysRoute.SUBDIVISIONS.path(),
            Map.of("countryIsoCode", countryIsoCode, "languageIsoCode", languageIsoCode),
            SubdivisionResponse[].class
        );
    }

    public ApiResponse<String> getSubdivisionsRaw() {
        return httpClient.getRaw(OpenHolidaysRoute.SUBDIVISIONS.path());
    }
}
