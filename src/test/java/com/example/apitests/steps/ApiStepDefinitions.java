package com.example.apitests.steps;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiStepDefinitions {

    private Response response;
    private String requestBody;

    @Given("the API base url is configured")
    public void theApiBaseUrlIsConfigured() {
        // Base URL is set by Hooks before each scenario.
    }

    @When("I send a GET request to {string}")
    public void iSendAGetRequestTo(String endpoint) {
        response = given()
                .when()
                .get(endpoint)
                .then()
                .extract()
                .response();
    }

    @When("I set request body to:")
    public void iSetRequestBodyTo(String body) {
        requestBody = body;
    }

    @When("I send a POST request to {string}")
    public void iSendAPostRequestTo(String endpoint) {
        response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(endpoint)
                .then()
                .extract()
                .response();
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedStatusCode) {
        assertThat(response.getStatusCode(), equalTo(expectedStatusCode));
    }

    @And("the response body path {string} should be {int}")
    public void theResponseBodyPathShouldBeInt(String jsonPath, int expectedValue) {
        Integer actualValue = response.jsonPath().getInt(jsonPath);
        assertThat(actualValue, equalTo(expectedValue));
    }

    @And("the response body path {string} should be {string}")
    public void theResponseBodyPathShouldBeString(String jsonPath, String expectedValue) {
        String actualValue = response.jsonPath().getString(jsonPath);
        assertThat(actualValue, equalTo(expectedValue));
    }

    @And("the response body path {string} should not be empty")
    public void theResponseBodyPathShouldNotBeEmpty(String jsonPath) {
        String value = response.jsonPath().getString(jsonPath);
        assertThat(value == null || value.isBlank(), equalTo(false));
    }

    @And("the response should contain key {string} in object path {string}")
    public void theResponseShouldContainKeyInObjectPath(String key, String objectPath) {
        Map<String, Object> object = response.jsonPath().getMap(objectPath);
        assertThat(object.containsKey(key), equalTo(true));
    }
}
