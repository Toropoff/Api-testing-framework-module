package com.apiframework.tests.publicapi;

import com.apiframework.domains.openholidays.endpoint.OpenHolidaysApi;
import com.apiframework.domains.openholidays.model.SubdivisionResponse;
import com.apiframework.testsupport.base.BaseApiTest;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OpenHolidaysPublicApiTest extends BaseApiTest {
    private OpenHolidaysApi openHolidaysApi;

    @Override protected String basePath() { return OpenHolidaysApi.basePath(); }
    @Override protected String targetApi() { return "open-holidays"; }

    @BeforeClass(alwaysRun = true, dependsOnMethods = "initHttpClient")
    public void init() {
        this.openHolidaysApi = api(OpenHolidaysApi::new);
    }

    // TODO: Placeholder for the test scenario description
    @Description("Verifies that GET /Subdivisions returns a non-empty list with valid isoCode, shortName, name, and officialLanguages for a known country code")
    @Test(description = "GET /Subdivisions should return a non-empty list for a valid countryIsoCode")
    public void shouldReturnSubdivisionsForValidCountry() {
        var response = openHolidaysApi.getSubdivisions("DE", "EN");

        Allure.step("Validate status 200 and non-empty subdivisions list with valid fields", () -> {
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).isNotNull().isNotEmpty();
            SubdivisionResponse first = response.body()[0];
            assertThat(first.isoCode()).isNotBlank();
            assertThat(first.shortName()).isNotBlank();
            assertThat(first.name()).isNotEmpty();
            assertThat(first.officialLanguages()).isNotEmpty();
        });
    }

    // TODO: Placeholder for the test scenario description
    @Description("Verifies that GET /Subdivisions without a countryIsoCode query parameter returns 400 Bad Request")
    @Test(description = "GET /Subdivisions without countryIsoCode should return 400 Bad Request")
    public void shouldReturn400WhenCountryIsoCodeIsMissing() {
        var response = openHolidaysApi.getSubdivisionsRaw();

        Allure.step("Validate 400 Bad Request response status", () ->
            assertThat(response.statusCode()).isEqualTo(400)
        );
    }
}
