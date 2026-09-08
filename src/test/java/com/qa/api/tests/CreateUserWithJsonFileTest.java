package com.qa.api.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

public class CreateUserWithJsonFileTest {

    // Playwright API objects
    Playwright playwright;
    APIRequest request;
    APIRequestContext requestContext;

    @BeforeTest
    public void setUp() {

        // Launch Playwright
        playwright = Playwright.create();

        // Create API request instance
        request = playwright.request();

        // Create request context
        requestContext = request.newContext();
    }

    // Generate unique email for every execution
    public static String generateRandomEmail() {
        return "navodi" + System.currentTimeMillis() + "@gmail.com";
    }

    @Test
    public void createUserWithJsonFileTest() throws IOException {

        // Read JSON file
        File file = new File("./src/test/data/user.json");

        ObjectMapper objectMapper = new ObjectMapper();

        // Convert JSON file into JsonNode
        JsonNode jsonNode = objectMapper.readTree(file);

        // Update email dynamically
        ((ObjectNode) jsonNode).put("email", generateRandomEmail());

        // Convert updated JSON back to String
        String requestBody = objectMapper.writeValueAsString(jsonNode);

        // Send POST request
        APIResponse apiPostResponse = requestContext.post(
                "https://gorest.co.in/public/v2/users",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setHeader(
                                "Authorization",
                                "Bearer 82d10ccf29029a3e8124f2280b32f3ceb2beac1dcb9370d10ae5f50f0e9b015b"
                        )
                        .setData(requestBody)
        );

        // Print response details
        System.out.println("POST Status Code: " + apiPostResponse.status());
        System.out.println("POST Response:");
        System.out.println(apiPostResponse.text());

        // Verify user creation
        Assert.assertEquals(apiPostResponse.status(), 201);
        Assert.assertEquals(apiPostResponse.statusText(), "Created");

        // Convert response into JsonNode
        JsonNode apiJsonResponse =
                objectMapper.readTree(apiPostResponse.body());

        // Print formatted response
        System.out.println(apiJsonResponse.toPrettyString());

        // Capture created user ID
        String userID = apiJsonResponse.get("id").asText();

        System.out.println("Created User ID: " + userID);

        System.out.println("============== GET Call Response ==============");

        // Send GET request
        APIResponse getApiResponse = requestContext.get(
                "https://gorest.co.in/public/v2/users/" + userID,
                RequestOptions.create()
                        .setHeader(
                                "Authorization",
                                "Bearer 82d10ccf29029a3e8124f2280b32f3ceb2beac1dcb9370d10ae5f50f0e9b015b"
                        )
        );

        // Print GET response
        System.out.println("GET Status Code: " + getApiResponse.status());
        System.out.println(getApiResponse.text());

        // Verify GET request
        Assert.assertEquals(getApiResponse.status(), 200);
        Assert.assertEquals(getApiResponse.statusText(), "OK");

        // Validate response data
        Assert.assertTrue(getApiResponse.text().contains(userID));
        Assert.assertTrue(getApiResponse.text().contains("Navodi QA"));
    }

    @AfterTest
    public void tearDown() {

        // Close Playwright
        playwright.close();
    }
}