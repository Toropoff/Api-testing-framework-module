package com.apiframework.tests.publicapi;

import com.apiframework.core.model.ApiResponse;
import com.apiframework.domains.openholidays.assertions.OpenHolidaysAssertions;
import com.apiframework.domains.openholidays.endpoint.OpenHolidaysApi;
import com.apiframework.domains.openholidays.flow.OpenHolidaysFlow;
import com.apiframework.domains.openholidays.model.SubdivisionResponse;
import com.apiframework.testsupport.network.NetworkAwareTestSupport;
import com.apiframework.testsupport.base.BaseApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class OpenHolidaysPublicApiTest extends BaseApiTest {

    private OpenHolidaysFlow openHolidaysFlow;

    @Override
    protected String baseUrl() {
        return OpenHolidaysApi.baseUrl();
    }

    @BeforeClass(alwaysRun = true)
    public void initFlow() {
        super.initHttpClient();
        this.openHolidaysFlow = new OpenHolidaysFlow(new OpenHolidaysApi(httpClient()));
    }

    @Override
    protected boolean requiresLiveApi() {
        return true;
    }

    @Test(description = "GET /Subdivisions should return a non-empty list for a valid countryIsoCode")
    public void shouldReturnSubdivisionsForValidCountry() {
        try {
            ApiResponse<SubdivisionResponse[]> response =
                openHolidaysFlow.fetchSubdivisions("DE", "EN");
            OpenHolidaysAssertions.assertSubdivisionsReturned(response);
        } catch (Exception ex) {
            NetworkAwareTestSupport.skipOnNetworkFailure(ex);
        }
    }

    @Test(description = "GET /Subdivisions without countryIsoCode should return 400 Bad Request")
    public void shouldReturn400WhenCountryIsoCodeIsMissing() {
        try {
            ApiResponse<String> response =
                openHolidaysFlow.fetchSubdivisionsWithoutCountry();
            OpenHolidaysAssertions.assertBadRequest(response);
        } catch (Exception ex) {
            NetworkAwareTestSupport.skipOnNetworkFailure(ex);
        }
    }
}
