package com.apiframework.sampledomain.openholidays.flow;

import com.apiframework.core.model.ApiResponse;
import com.apiframework.reporting.steps.AllureActionExecutor;
import com.apiframework.sampledomain.openholidays.endpoint.OpenHolidaysApi;
import com.apiframework.sampledomain.openholidays.model.SubdivisionResponse;

public final class OpenHolidaysFlow {
    private final OpenHolidaysApi openHolidaysApi;
    private final AllureActionExecutor stepExecutor;

    public OpenHolidaysFlow(OpenHolidaysApi openHolidaysApi) {
        this(openHolidaysApi, new AllureActionExecutor());
    }

    public OpenHolidaysFlow(OpenHolidaysApi openHolidaysApi, AllureActionExecutor stepExecutor) {
        this.openHolidaysApi = openHolidaysApi;
        this.stepExecutor = stepExecutor;
    }

    /**
     * Fetches subdivisions for the given country. Returns the raw response —
     * callers are responsible for asserting the result via {@code OpenHolidaysAssertions}.
     */
    public ApiResponse<SubdivisionResponse[]> fetchSubdivisions(String countryIsoCode, String languageIsoCode) {
        return stepExecutor.composite("Fetch subdivisions for country " + countryIsoCode, () ->
            stepExecutor.action(
                "GET /Subdivisions?countryIsoCode=" + countryIsoCode + "&languageIsoCode=" + languageIsoCode,
                () -> openHolidaysApi.getSubdivisions(countryIsoCode, languageIsoCode)
            )
        );
    }

    /**
     * Sends GET /Subdivisions without the required {@code countryIsoCode} parameter.
     * Returns the raw response — callers are responsible for asserting the error status.
     */
    public ApiResponse<String> fetchSubdivisionsWithoutCountry() {
        return stepExecutor.action(
            "GET /Subdivisions without required countryIsoCode",
            () -> openHolidaysApi.getSubdivisionsRaw()
        );
    }
}
