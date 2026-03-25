package com.apiframework.domains.openholidays.endpoint;

import com.apiframework.config.DomainConfig;
import com.apiframework.http.HttpClient;
import com.apiframework.model.ApiResponse;
import com.apiframework.domains.openholidays.model.SubdivisionResponse;

import java.util.Map;
import java.util.Objects;

public final class OpenHolidaysApi {
    private static final String BASE_PATH = DomainConfig.loadBasePath(
        OpenHolidaysApi.class,
        "open-holidays.properties",
        System.getenv("FRAMEWORK_ENV") != null
            ? System.getenv("FRAMEWORK_ENV")
            : System.getProperty("framework.env", "dev")
    );

    public static String basePath() {
        return BASE_PATH;
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
