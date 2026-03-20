package com.apiframework.domains.openholidays.endpoint;

import com.apiframework.core.http.HttpClient;
import com.apiframework.core.model.ApiRequest;
import com.apiframework.core.model.ApiResponse;
import com.apiframework.core.model.HttpMethod;
import com.apiframework.domains.openholidays.model.SubdivisionResponse;

import java.io.IOException;
import java.io.InputStream;
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

    /**
     * GET /Subdivisions — returns subdivisions for a given country.
     *
     * @param countryIsoCode  ISO 3166-1 code of the country (required, e.g. "DE")
     * @param languageIsoCode ISO 639-1 language code for localized names (optional, e.g. "EN")
     */
    public ApiResponse<SubdivisionResponse[]> getSubdivisions(String countryIsoCode, String languageIsoCode) {
        ApiRequest<Void> request = ApiRequest.<Void>builder(HttpMethod.GET, OpenHolidaysRoute.SUBDIVISIONS.path())
            .queryParam("countryIsoCode", countryIsoCode)
            .queryParam("languageIsoCode", languageIsoCode)
            .build();
        return httpClient.execute(request, SubdivisionResponse[].class);
    }

    /**
     * GET /Subdivisions with no query parameters — used to trigger a 400 Bad Request
     * by omitting the required {@code countryIsoCode} parameter.
     * Returns the raw response body as a String to avoid deserialization of the error payload.
     */
    public ApiResponse<String> getSubdivisionsRaw() {
        ApiRequest<Void> request = ApiRequest.<Void>builder(HttpMethod.GET, OpenHolidaysRoute.SUBDIVISIONS.path())
            .build();
        return httpClient.executeRaw(request);
    }
}
