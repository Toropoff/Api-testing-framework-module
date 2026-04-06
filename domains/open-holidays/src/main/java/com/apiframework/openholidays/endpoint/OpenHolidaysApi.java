package com.apiframework.openholidays.endpoint;

import com.apiframework.config.DomainConfig;
import com.apiframework.http.HttpClient;
import com.apiframework.model.ApiResponse;
import com.apiframework.openholidays.model.HolidayByDateResponse;
import com.apiframework.openholidays.model.StatisticsResponse;
import com.apiframework.openholidays.model.SubdivisionResponse;

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

    private static final String API_NAME = DomainConfig.loadApiName(
        OpenHolidaysApi.class,
        "open-holidays.properties"
    );

    public static String basePath() {
        return BASE_PATH;
    }

    public static String displayApiName() {
        return API_NAME;
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

    public ApiResponse<HolidayByDateResponse[]> getSchoolHolidaysByDate(String date, String languageIsoCode) {
        return httpClient.get(
            OpenHolidaysRoute.SCHOOL_HOLIDAYS_BY_DATE.path(),
            Map.of("date", date, "languageIsoCode", languageIsoCode),
            HolidayByDateResponse[].class
        );
    }

    public ApiResponse<String> getSchoolHolidaysByDateRaw() {
        return httpClient.getRaw(OpenHolidaysRoute.SCHOOL_HOLIDAYS_BY_DATE.path());
    }

    public ApiResponse<HolidayByDateResponse[]> getPublicHolidaysByDate(String date, String languageIsoCode) {
        return httpClient.get(
            OpenHolidaysRoute.PUBLIC_HOLIDAYS_BY_DATE.path(),
            Map.of("date", date, "languageIsoCode", languageIsoCode),
            HolidayByDateResponse[].class
        );
    }

    public ApiResponse<String> getPublicHolidaysByDateRaw() {
        return httpClient.getRaw(OpenHolidaysRoute.PUBLIC_HOLIDAYS_BY_DATE.path());
    }

    public ApiResponse<StatisticsResponse> getStatisticsSchoolHolidays(String countryIsoCode) {
        return httpClient.get(
            OpenHolidaysRoute.STATISTICS_SCHOOL_HOLIDAYS.path(),
            Map.of("countryIsoCode", countryIsoCode),
            StatisticsResponse.class
        );
    }

    public ApiResponse<String> getStatisticsSchoolHolidaysRaw() {
        return httpClient.getRaw(OpenHolidaysRoute.STATISTICS_SCHOOL_HOLIDAYS.path());
    }

    public ApiResponse<StatisticsResponse> getStatisticsPublicHolidays(String countryIsoCode) {
        return httpClient.get(
            OpenHolidaysRoute.STATISTICS_PUBLIC_HOLIDAYS.path(),
            Map.of("countryIsoCode", countryIsoCode),
            StatisticsResponse.class
        );
    }

    public ApiResponse<String> getStatisticsPublicHolidaysRaw() {
        return httpClient.getRaw(OpenHolidaysRoute.STATISTICS_PUBLIC_HOLIDAYS.path());
    }
}
