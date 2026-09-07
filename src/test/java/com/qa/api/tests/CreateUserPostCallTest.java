package com.qa.api.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CreateUserPostCallTest {

    // Playwright API objects
    Playwright playwright;
    APIRequest request;
    APIRequestContext requestContext;

    // Store the generated email for later validation
    static String randomEmail;

    @BeforeTest
    public void setUp() {

        // Launch Playwright
        playwright = Playwright.create();

        // Create API request instance
        request = playwright.request();

        // Create request context for sending API calls
        requestContext = request.newContext();
    }

    // Generate a unique email for every test run
    public static String generateRandomEmail() {
        randomEmail = "navodi" + System.currentTimeMillis() + "@gmail.com";
        return randomEmail;
    }

    @Test
    public void createUserTest() throws IOException {

        // Request body data
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Navodi QA");
        data.put("email", generateRandomEmail());
        data.put("gender", "female");
        data.put("status", "active");

        // Send POST request to create a new user
        APIResponse apiPostResponse = requestContext.post(
                "https://gorest.co.in/public/v2/users",
                RequestOptions.create()
                        .setHeader("Content-type", "application/json")
                        .setHeader("Authorization",
                                "Bearer c173d5eb393df7100bf47c21e5eabe48b79471fe9b405dca49eba2b1354221c0")
                        .setData(data)
        );

        // Verify user creation was successful
        System.out.println("Response status: " + apiPostResponse.status());
        Assert.assertEquals(apiPostResponse.status(), 201);
        Assert.assertEquals(apiPostResponse.statusText(), "Created");

        // Print response body
        System.out.println(apiPostResponse.text());

        // Convert JSON response into JsonNode object
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode apiJsonResponse = objectMapper.readTree(apiPostResponse.body());

        // Print formatted JSON response
        String jsonPrettyResponse = apiJsonResponse.toPrettyString();
        System.out.println(jsonPrettyResponse);

        // Capture created user ID from response
        String userID = apiJsonResponse.get("id").asText();
        System.out.println("User ID: " + userID);

        System.out.println("============== GET Call Response ==============");

        // Send GET request to fetch the created user
        APIResponse getApiResponse = requestContext.get(
                "https://gorest.co.in/public/v2/users/" + userID,
                RequestOptions.create()
                        .setHeader("Authorization",
                                "Bearer c173d5eb393df7100bf47c21e5eabe48b79471fe9b405dca49eba2b1354221c0")
        );

        // Verify GET request was successful
        Assert.assertEquals(getApiResponse.status(), 200);
        Assert.assertEquals(getApiResponse.statusText(), "OK");

        // Print GET response
        System.out.println(getApiResponse.text());

        // Validate response contains expected user details
        Assert.assertTrue(getApiResponse.text().contains(userID));
        Assert.assertTrue(getApiResponse.text().contains("Navodi QA"));
        Assert.assertTrue(getApiResponse.text().contains(randomEmail));
    }

    @AfterTest
    public void tearDown() {

        // Close Playwright and release resources
        playwright.close();
    }
}