package com.apiframework.tests.publicapi;

import com.apiframework.domains.openholidays.endpoint.OpenHolidaysApi;
import com.apiframework.domains.openholidays.model.SubdivisionResponse;
import com.apiframework.testsupport.network.NetworkAwareTestSupport;
import com.apiframework.testsupport.base.BaseApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OpenHolidaysPublicApiTest extends BaseApiTest {
    private OpenHolidaysApi openHolidaysApi;

    @Override
    protected String baseUrl() {
        return OpenHolidaysApi.baseUrl();
    }

    @BeforeClass(alwaysRun = true)
    public void init() {
        super.initHttpClient();
        this.openHolidaysApi = new OpenHolidaysApi(httpClient());
    }

    @Override
    protected boolean requiresLiveApi() {
        return true;
    }

    @Test(description = "GET /Subdivisions should return a non-empty list for a valid countryIsoCode")
    public void shouldReturnSubdivisionsForValidCountry() {
        try {
            var response = openHolidaysApi.getSubdivisions("DE", "EN");

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).isNotNull().isNotEmpty();

            SubdivisionResponse first = response.body()[0];
            assertThat(first.isoCode()).isNotBlank();
            assertThat(first.shortName()).isNotBlank();
            assertThat(first.name()).isNotEmpty();
            assertThat(first.officialLanguages()).isNotEmpty();
        } catch (Exception ex) {
            NetworkAwareTestSupport.skipOnNetworkFailure(ex);
        }
    }

    @Test(description = "GET /Subdivisions without countryIsoCode should return 400 Bad Request")
    public void shouldReturn400WhenCountryIsoCodeIsMissing() {
        try {
            var response = openHolidaysApi.getSubdivisionsRaw();

            assertThat(response.statusCode()).isEqualTo(400);
        } catch (Exception ex) {
            NetworkAwareTestSupport.skipOnNetworkFailure(ex);
        }
    }
}
