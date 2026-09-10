package com.qa.api.tests.POST;

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

public class CreateUserWithJsonStringTest {

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
    public void CreateUserWithJsonStringTest() throws IOException {

        // Generate a unique email
        String email = generateRandomEmail();

        // JSON request body
        String requestJsonBody = "{\n" +
                "  \"name\": \"Navodi QA\",\n" +
                "  \"email\": \"" + email + "\",\n" +
                "  \"gender\": \"female\",\n" +
                "  \"status\": \"active\"\n" +
                "}";

        // Send POST request to create a new user
        APIResponse apiPostResponse = requestContext.post(
                "https://gorest.co.in/public/v2/users",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Authorization",
                                "Bearer 82d10ccf29029a3e8124f2280b32f3ceb2beac1dcb9370d10ae5f50f0e9b015b")
                        .setData(requestJsonBody)
        );

        // Verify user creation was successful
        System.out.println("Response status: " + apiPostResponse.status());
        Assert.assertEquals(apiPostResponse.status(), 201);
        Assert.assertEquals(apiPostResponse.statusText(), "Created");

        // Print response body
        System.out.println(apiPostResponse.text());

        // Convert JSON response into JsonNode
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode apiJsonResponse = objectMapper.readTree(apiPostResponse.body());

        // Print formatted JSON response
        System.out.println(apiJsonResponse.toPrettyString());

        // Capture created user ID
        String userID = apiJsonResponse.get("id").asText();
        System.out.println("User ID: " + userID);

        System.out.println("============== GET Call Response ==============");

        // Send GET request to fetch the created user
        APIResponse getApiResponse = requestContext.get(
                "https://gorest.co.in/public/v2/users/" + userID,
                RequestOptions.create()
                        .setHeader("Authorization",
                                "Bearer 82d10ccf29029a3e8124f2280b32f3ceb2beac1dcb9370d10ae5f50f0e9b015b")
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