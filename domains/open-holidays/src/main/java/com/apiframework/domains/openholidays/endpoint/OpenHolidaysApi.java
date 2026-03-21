package com.apiframework.domains.openholidays.endpoint;

import com.apiframework.core.http.HttpClient;
import com.apiframework.core.model.ApiResponse;
import com.apiframework.domains.openholidays.model.SubdivisionResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public final class OpenHolidaysApi {
    private static final String BASE_URL;

    static {
        Properties props = new Properties();
        try (InputStream in = OpenHolidaysApi.class.getClassLoader().getResourceAsStream("open-holidays.properties")) {
            if (in == null) {
                throw new IllegalStateException("open-holidays.properties not found on classpath");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load open-holidays.properties", e);
        }
        BASE_URL = Objects.requireNonNull(props.getProperty("baseUrl"), "baseUrl not set in open-holidays.properties");
    }

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
