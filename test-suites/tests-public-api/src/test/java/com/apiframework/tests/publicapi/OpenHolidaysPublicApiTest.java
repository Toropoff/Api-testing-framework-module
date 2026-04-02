package com.apiframework.tests.publicapi;

import com.apiframework.domains.openholidays.endpoint.OpenHolidaysApi;
import com.apiframework.domains.openholidays.model.HolidayByDateResponse;
import com.apiframework.domains.openholidays.model.SubdivisionResponse;
import com.apiframework.testsupport.base.BaseApiTest;
import com.apiframework.testsupport.contracts.JsonSchemaContractValidator;
import com.apiframework.testsupport.contracts.SnapshotContractValidator;
import io.qameta.allure.Description;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OpenHolidaysPublicApiTest extends BaseApiTest {
    private static final String TEST_DATE = "2024-12-25";
    private static final String TEST_LANGUAGE = "EN";
    private static final String TEST_COUNTRY = "DE";

    private OpenHolidaysApi openHolidaysApi;
    private JsonSchemaContractValidator schemaValidator;
    private SnapshotContractValidator snapshotValidator;

    @Override protected String basePath() { return OpenHolidaysApi.basePath(); }
    @Override protected String targetApi() { return "open-holidays"; }

    @BeforeClass(alwaysRun = true, dependsOnMethods = "initHttpClient")
    public void init() {
        this.openHolidaysApi = api(OpenHolidaysApi::new);
        this.schemaValidator = new JsonSchemaContractValidator();
        this.snapshotValidator = new SnapshotContractValidator();
    }

    @Description("Verifies that GET /Subdivisions returns a non-empty list with valid isoCode, shortName, name, and officialLanguages for a known country code")
    @Test(description = "GET /Subdivisions should return a non-empty list for a valid countryIsoCode")
    public void shouldReturnSubdivisionsForValidCountry() {
        var response = openHolidaysApi.getSubdivisions("DE", "EN");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotNull().isNotEmpty();
        SubdivisionResponse first = response.body()[0];
        assertThat(first.isoCode()).isNotBlank();
        assertThat(first.shortName()).isNotBlank();
        assertThat(first.name()).isNotEmpty();
        assertThat(first.officialLanguages()).isNotEmpty();
    }

    @Description("Verifies that GET /Subdivisions without a countryIsoCode query parameter returns 400 Bad Request")
    @Test(description = "GET /Subdivisions without countryIsoCode should return 400 Bad Request")
    public void shouldReturn400WhenCountryIsoCodeIsMissing() {
        var response = openHolidaysApi.getSubdivisionsRaw();

        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Description("Verifies that GET /SchoolHolidaysByDate returns 200 with a non-empty array of school holidays for a known date, conforming to schema and matching golden-file snapshot")
    @Test(description = "GET /SchoolHolidaysByDate should return school holidays for a valid date")
    public void shouldReturnSchoolHolidaysByDate() {
        var response = openHolidaysApi.getSchoolHolidaysByDate(TEST_DATE, TEST_LANGUAGE);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotNull().isNotEmpty();
        HolidayByDateResponse first = response.body()[0];
        assertThat(first.type()).isEqualTo("School");
        assertThat(first.country()).isNotNull();
        assertThat(first.country().isoCode()).isNotBlank();
        schemaValidator.assertMatchesSchema(response.rawBody(), "schemas/school-holidays-by-date.schema.json");
        snapshotValidator.assertMatchesSnapshot("school-holidays-by-date", response.rawBody());
    }

    @Description("Verifies that GET /SchoolHolidaysByDate without the required date parameter returns 400 Bad Request")
    @Test(description = "GET /SchoolHolidaysByDate without date should return 400 Bad Request")
    public void shouldReturn400WhenDateMissingForSchoolHolidaysByDate() {
        var response = openHolidaysApi.getSchoolHolidaysByDateRaw();

        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Description("Verifies that GET /PublicHolidaysByDate returns 200 with a non-empty array of public holidays for a known date, conforming to schema and matching golden-file snapshot")
    @Test(description = "GET /PublicHolidaysByDate should return public holidays for a valid date")
    public void shouldReturnPublicHolidaysByDate() {
        var response = openHolidaysApi.getPublicHolidaysByDate(TEST_DATE, TEST_LANGUAGE);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotNull().isNotEmpty();
        HolidayByDateResponse first = response.body()[0];
        assertThat(first.type()).isEqualTo("Public");
        assertThat(first.country()).isNotNull();
        assertThat(first.country().isoCode()).isNotBlank();
        schemaValidator.assertMatchesSchema(response.rawBody(), "schemas/public-holidays-by-date.schema.json");
        snapshotValidator.assertMatchesSnapshot("public-holidays-by-date", response.rawBody());
    }

    @Description("Verifies that GET /PublicHolidaysByDate without the required date parameter returns 400 Bad Request")
    @Test(description = "GET /PublicHolidaysByDate without date should return 400 Bad Request")
    public void shouldReturn400WhenDateMissingForPublicHolidaysByDate() {
        var response = openHolidaysApi.getPublicHolidaysByDateRaw();

        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Description("Verifies that GET /Statistics/SchoolHolidays returns 200 with oldestStartDate and youngestStartDate for a known country, conforming to schema and matching golden-file snapshot")
    @Test(description = "GET /Statistics/SchoolHolidays should return statistics for a valid countryIsoCode")
    public void shouldReturnStatisticsForSchoolHolidays() {
        var response = openHolidaysApi.getStatisticsSchoolHolidays(TEST_COUNTRY);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotNull();
        assertThat(response.body().oldestStartDate()).isNotBlank();
        assertThat(response.body().youngestStartDate()).isNotBlank();
        schemaValidator.assertMatchesSchema(response.rawBody(), "schemas/statistics-school-holidays.schema.json");
        snapshotValidator.assertMatchesSnapshot("statistics-school-holidays", response.rawBody());
    }

    @Description("Verifies that GET /Statistics/SchoolHolidays without the required countryIsoCode parameter returns 400 Bad Request")
    @Test(description = "GET /Statistics/SchoolHolidays without countryIsoCode should return 400 Bad Request")
    public void shouldReturn400WhenCountryMissingForSchoolHolidaysStatistics() {
        var response = openHolidaysApi.getStatisticsSchoolHolidaysRaw();

        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Description("Verifies that GET /Statistics/PublicHolidays returns 200 with oldestStartDate and youngestStartDate for a known country, conforming to schema and matching golden-file snapshot")
    @Test(description = "GET /Statistics/PublicHolidays should return statistics for a valid countryIsoCode")
    public void shouldReturnStatisticsForPublicHolidays() {
        var response = openHolidaysApi.getStatisticsPublicHolidays(TEST_COUNTRY);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotNull();
        assertThat(response.body().oldestStartDate()).isNotBlank();
        assertThat(response.body().youngestStartDate()).isNotBlank();
        schemaValidator.assertMatchesSchema(response.rawBody(), "schemas/statistics-public-holidays.schema.json");
        snapshotValidator.assertMatchesSnapshot("statistics-public-holidays", response.rawBody());
    }

    @Description("Verifies that GET /Statistics/PublicHolidays without the required countryIsoCode parameter returns 400 Bad Request")
    @Test(description = "GET /Statistics/PublicHolidays without countryIsoCode should return 400 Bad Request")
    public void shouldReturn400WhenCountryMissingForPublicHolidaysStatistics() {
        var response = openHolidaysApi.getStatisticsPublicHolidaysRaw();

        assertThat(response.statusCode()).isEqualTo(400);
    }
}
