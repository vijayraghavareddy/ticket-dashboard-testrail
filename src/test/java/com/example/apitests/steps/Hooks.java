package com.example.apitests.steps;

import com.example.apitests.config.ConfigManager;
import io.cucumber.java.Before;
import io.restassured.RestAssured;

public class Hooks {

    @Before
    public void setBaseUri() {
        String baseUrl = System.getProperty("baseUrl", ConfigManager.get("base.url"));
        RestAssured.baseURI = baseUrl;
        String apiKey = ConfigManager.get("api.key");
        if (apiKey != null && !apiKey.isBlank()) {
            RestAssured.requestSpecification = RestAssured.given().header("x-api-key", apiKey);
        }
    }
}
