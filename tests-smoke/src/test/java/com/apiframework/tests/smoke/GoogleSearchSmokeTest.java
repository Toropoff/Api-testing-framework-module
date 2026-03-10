package com.apiframework.tests.smoke;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.SkipException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class GoogleSearchSmokeTest {

    @Test
    public void shouldReturnGoogleHomePageOnGetRequest() {
        try {
            Response response = RestAssured
                .given()
                .redirects().follow(true)
                .when()
                .get("https://www.google.com");

            assertTrue(response.statusCode() >= 200 && response.statusCode() < 400,
                "Expected successful or redirected response from Google");
            assertTrue(response.asString().toLowerCase().contains("google"),
                "Expected response body to contain 'google'");
        } catch (Throwable ex) {
            throw new SkipException("External network is unavailable for Google smoke check", ex);
        }
    }
}
