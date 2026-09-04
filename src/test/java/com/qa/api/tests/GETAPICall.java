package com.qa.api.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.annotations.AfterTest;

import java.io.IOException;
import java.util.Map;

public class GETAPICall {

    // Playwright API objects
    Playwright playwright;
    APIRequest request;
    APIRequestContext requestContext;

    @BeforeTest
    public void setUp() {

        // Initialize Playwright
        playwright = Playwright.create();

        // Create API request instance
        request = playwright.request();

        // Create API request context
        requestContext = request.newContext();
    }

    @Test
    public void getUsersApiTest() throws IOException {

        // Send GET request to retrieve all users
        APIResponse apiResponse = requestContext.get(
                "https://gorest.co.in/public/v2/users");

        // Get and print response status code
        int statusCode = apiResponse.status();
        System.out.println("Response code: " + statusCode);

        // Verify status code is 200
        Assert.assertEquals(statusCode, 200);

        // Get and print status text
        String statusText = apiResponse.statusText();
        System.out.println("Response text: " + statusText);

        // Print raw response body
        System.out.println("--print API response with plain text---");
        System.out.println(apiResponse.text());

        // Convert response to formatted JSON and print
        System.out.println("--print API json response---");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(apiResponse.body());
        String jsonPrettyResponse = jsonResponse.toPrettyString();
        System.out.println(jsonPrettyResponse);

        // Print requested URL
        System.out.println("--print API URL---");
        System.out.println(apiResponse.url());

        // Print response headers
        System.out.println("--print response headers---");
        Map<String, String> headersMap = apiResponse.headers();
        System.out.println(headersMap);

        // Verify content type header
        Assert.assertEquals(
                headersMap.get("content-type"),
                "application/json; charset=utf-8"
        );
    }

    @Test
    public void getSpecificUserApiTest() throws IOException {

        // Send GET request with query parameters
        APIResponse apiResponse = requestContext.get(
                "https://gorest.co.in/public/v2/users",
                RequestOptions.create()
                        .setQueryParam("gender", "male")
                        .setQueryParam("status", "active")
        );

        // Get and print response status code
        int statusCode = apiResponse.status();
        System.out.println("Response code: " + statusCode);

        // Verify status code is 200
        Assert.assertEquals(statusCode, 200);

        // Get and print status text
        String statusText = apiResponse.statusText();
        System.out.println("Response text: " + statusText);

        // Print raw response body
        System.out.println("--print API response with plain text---");
        System.out.println(apiResponse.text());

        // Convert response to formatted JSON and print
        System.out.println("--print API json response---");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(apiResponse.body());
        String jsonPrettyResponse = jsonResponse.toPrettyString();
        System.out.println(jsonPrettyResponse);
    }

    @AfterTest
    public void tearDown() {

        // Close Playwright and release resources
        playwright.close();
    }
}