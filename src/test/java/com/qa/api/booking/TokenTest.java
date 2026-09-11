package com.qa.api.booking;

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

public class TokenTest {


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

    @Test
    public void getTokenTest() throws IOException {

        // JSON request body
        String requestTokenJsonBody = "{\n" +
                "  \"username\": \"admin\",\n" +
                "  \"password\": \"password123\"\n" +
                "}";

        // Get a token
        APIResponse apiPostTokenResponse = requestContext.post(
                "https://restful-booker.herokuapp.com/auth",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(requestTokenJsonBody)
        );

        // Verify user creation was successful
        System.out.println("Response status: " + apiPostTokenResponse.status());
        Assert.assertEquals(apiPostTokenResponse.status(), 200);
        Assert.assertEquals(apiPostTokenResponse.statusText(), "OK");

        // Print response body
        System.out.println(apiPostTokenResponse.text());

        // Convert JSON response into JsonNode
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode apiJsonResponse = objectMapper.readTree(apiPostTokenResponse.body());

        // Print formatted JSON response
        System.out.println(apiJsonResponse.toPrettyString());

        // Capture the created Token
        String tokenID = apiJsonResponse.get("token").asText();
        System.out.println("token ID: " + tokenID);

        // Ensure token id is not null
        Assert.assertNotNull(tokenID);

    }
    @AfterTest
    public void tearDown() {

        // Close Playwright and release resources
        playwright.close();
    }

}
