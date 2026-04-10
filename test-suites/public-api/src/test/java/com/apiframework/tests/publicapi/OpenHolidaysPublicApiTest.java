package com.apiframework.tests.publicapi;

import com.apiframework.testsupport.assertions.ApiResponseAssert;
import com.apiframework.testsupport.base.BaseApiTest;
import io.qameta.allure.Description;
import org.testng.annotations.Test;

public class OpenHolidaysPublicApiTest extends BaseApiTest {
    private static final String TEST_DATE     = "2024-12-25";
    private static final String TEST_LANGUAGE = "EN";
    private static final String TEST_COUNTRY  = "DE";

    @Override
    protected String domain() { return "open-holidays"; }

    @Description("Verifies that GET /Subdivisions returns a non-empty list with valid isoCode, shortName, name, and officialLanguages for a known country code")
    @Test(description = "GET /Subdivisions should return a non-empty list for a valid countryIsoCode")
    public void shouldReturnSubdivisionsForValidCountry() {
        var response = call("get-subdivisions", String.class)
                .query("countryIsoCode", TEST_COUNTRY)
                .query("languageIsoCode", TEST_LANGUAGE)
                .send();

        ApiResponseAssert.assertThat(response)
                .hasStatus(200)
                .body()
                    .isNotEmpty()
                    .first()
                    .field("isoCode").isNotBlank()
                    .field("shortName").isNotBlank()
                    .field("name").isNotEmpty()
                    .field("officialLanguages").isNotEmpty();
    }

    @Description("Verifies that GET /Subdivisions without a countryIsoCode query parameter returns 400 Bad Request")
    @Test(description = "GET /Subdivisions without countryIsoCode should return 400 Bad Request")
    public void shouldReturn400WhenCountryIsoCodeIsMissing() {
        var response = call("get-subdivisions", String.class).send();

        ApiResponseAssert.assertThat(response).hasStatus(400);
    }

    @Description("Verifies that GET /SchoolHolidaysByDate returns 200 with a non-empty array of school holidays for a known date, conforming to schema and matching golden-file snapshot")
    @Test(description = "GET /SchoolHolidaysByDate should return school holidays for a valid date")
    public void shouldReturnSchoolHolidaysByDate() {
        var response = call("get-school-holidays-by-date", String.class)
                .query("date", TEST_DATE)
                .query("languageIsoCode", TEST_LANGUAGE)
                .send();

        ApiResponseAssert.assertThat(response)
                .hasStatus(200)
                .body()
                    .isNotEmpty()
                    .first()
                    .field("type").hasValue("School")
                    .field("country.isoCode").isNotBlank()
                .matchesSchema("schemas/school-holidays-by-date.schema.json")
                .matchesSnapshot("school-holidays-by-date");
    }

    @Description("Verifies that GET /SchoolHolidaysByDate without the required date parameter returns 400 Bad Request")
    @Test(description = "GET /SchoolHolidaysByDate without date should return 400 Bad Request")
    public void shouldReturn400WhenDateMissingForSchoolHolidaysByDate() {
        var response = call("get-school-holidays-by-date", String.class).send();

        ApiResponseAssert.assertThat(response).hasStatus(400);
    }

    @Description("Verifies that GET /PublicHolidaysByDate returns 200 with a non-empty array of public holidays for a known date, conforming to schema and matching golden-file snapshot")
    @Test(description = "GET /PublicHolidaysByDate should return public holidays for a valid date")
    public void shouldReturnPublicHolidaysByDate() {
        var response = call("get-public-holidays-by-date", String.class)
                .query("date", TEST_DATE)
                .query("languageIsoCode", TEST_LANGUAGE)
                .send();

        ApiResponseAssert.assertThat(response)
                .hasStatus(200)
                .body()
                    .isNotEmpty()
                    .first()
                    .field("type").hasValue("Public")
                    .field("country.isoCode").isNotBlank()
                .matchesSchema("schemas/public-holidays-by-date.schema.json")
                .matchesSnapshot("public-holidays-by-date");
    }

    @Description("Verifies that GET /PublicHolidaysByDate without the required date parameter returns 400 Bad Request")
    @Test(description = "GET /PublicHolidaysByDate without date should return 400 Bad Request")
    public void shouldReturn400WhenDateMissingForPublicHolidaysByDate() {
        var response = call("get-public-holidays-by-date", String.class).send();

        ApiResponseAssert.assertThat(response).hasStatus(400);
    }

    @Description("Verifies that GET /Statistics/SchoolHolidays returns 200 with oldestStartDate and youngestStartDate for a known country, conforming to schema and matching golden-file snapshot")
    @Test(description = "GET /Statistics/SchoolHolidays should return statistics for a valid countryIsoCode")
    public void shouldReturnStatisticsForSchoolHolidays() {
        var response = call("get-statistics-school-holidays", String.class)
                .query("countryIsoCode", TEST_COUNTRY)
                .send();

        ApiResponseAssert.assertThat(response)
                .hasStatus(200)
                .body()
                    .field("oldestStartDate").isNotBlank()
                    .field("youngestStartDate").isNotBlank()
                .matchesSchema("schemas/statistics-school-holidays.schema.json")
                .matchesSnapshot("statistics-school-holidays");
    }

    @Description("Verifies that GET /Statistics/SchoolHolidays without the required countryIsoCode parameter returns 400 Bad Request")
    @Test(description = "GET /Statistics/SchoolHolidays without countryIsoCode should return 400 Bad Request")
    public void shouldReturn400WhenCountryMissingForSchoolHolidaysStatistics() {
        var response = call("get-statistics-school-holidays", String.class).send();

        ApiResponseAssert.assertThat(response).hasStatus(400);
    }

    @Description("Verifies that GET /Statistics/PublicHolidays returns 200 with oldestStartDate and youngestStartDate for a known country, conforming to schema and matching golden-file snapshot")
    @Test(description = "GET /Statistics/PublicHolidays should return statistics for a valid countryIsoCode")
    public void shouldReturnStatisticsForPublicHolidays() {
        var response = call("get-statistics-public-holidays", String.class)
                .query("countryIsoCode", TEST_COUNTRY)
                .send();

        ApiResponseAssert.assertThat(response)
                .hasStatus(200)
                .body()
                    .field("oldestStartDate").isNotBlank()
                    .field("youngestStartDate").isNotBlank()
                .matchesSchema("schemas/statistics-public-holidays.schema.json")
                .matchesSnapshot("statistics-public-holidays");
    }

    @Description("Verifies that GET /Statistics/PublicHolidays without the required countryIsoCode parameter returns 400 Bad Request")
    @Test(description = "GET /Statistics/PublicHolidays without countryIsoCode should return 400 Bad Request")
    public void shouldReturn400WhenCountryMissingForPublicHolidaysStatistics() {
        var response = call("get-statistics-public-holidays", String.class).send();

        ApiResponseAssert.assertThat(response).hasStatus(400);
    }
}
