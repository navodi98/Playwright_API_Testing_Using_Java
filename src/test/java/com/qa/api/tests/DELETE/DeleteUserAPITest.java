package com.qa.api.tests.DELETE;

import com.api.data.User;
import com.api.data.Users;
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

public class DeleteUserAPITest {

    // 1. POST - create a fresh user --> user id --> 201
    // 2. DELETE user --> user id --> 204
    // 3. GET user --> same user id --> 404 error


    // Playwright API objects
    Playwright playwright;
    APIRequest request;
    APIRequestContext requestContext;

    // Store the generated email
    static String randomEmail;

    @BeforeTest
    public void setUp() {

        // Start Playwright
        playwright = Playwright.create();

        // Create the API request object
        request = playwright.request();

        // Create a request context to send API requests
        requestContext = request.newContext();
    }

    // Generate a unique email for every test run
    public static String generateRandomEmail() {

        randomEmail = "navodi" + System.currentTimeMillis() + "@gmail.com";

        return randomEmail;
    }

    @Test
    public void deleteUserTest() throws IOException {

        // Generate a unique email
        String email = generateRandomEmail();

        // Create a Users object : using builder pattern
        Users users = Users.builder()
                .name("Sanduni")
                .email(email)
                .gender("female")
                .status("active").build();



        // 1. POST - create a fresh user --> user id --> 201
        APIResponse apiPostResponse = requestContext.post(
                "https://gorest.co.in/public/v2/users",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setHeader(
                                "Authorization",
                                "Bearer 82d10ccf29029a3e8124f2280b32f3ceb2beac1dcb9370d10ae5f50f0e9b015b"
                        )
                        .setData(users)
        );

        // Print the response status code
        System.out.println("Response status: " + apiPostResponse.status());

        // Verify that the user was successfully created
        Assert.assertEquals(apiPostResponse.status(), 201);

        // Get the response body as a String
        String responseText = apiPostResponse.text();

        // Print the response body
        System.out.println("Response body:");
        System.out.println(responseText);

        // Create ObjectMapper to convert JSON into a Java object
        ObjectMapper objectMapper = new ObjectMapper();

        // Convert the response JSON into a User POJO
        // This process is called deserialization
        User actualUser = objectMapper.readValue(responseText, User.class);

        // Print the User object created from the response
        System.out.println();
        System.out.println("Actual user from the response:");
        System.out.println(actualUser);

        // Verify that the server generated an ID for the new user
        Assert.assertNotNull(actualUser.getId());

        String userId = actualUser.getId();
        System.out.println("new user id is :" + userId);


        // 2. DELETE user --> user id --> 204
        APIResponse apiDELETEResponse = requestContext.delete("https://gorest.co.in/public/v2/users/" + userId,
                RequestOptions.create()
                        .setHeader(
                                "Authorization",
                                "Bearer 82d10ccf29029a3e8124f2280b32f3ceb2beac1dcb9370d10ae5f50f0e9b015b"
                        ));

        System.out.println(apiDELETEResponse.status());
        System.out.println(apiDELETEResponse.statusText());

        Assert.assertEquals(apiDELETEResponse.status(), 204);

        // Ensure the response contain blank response
        System.out.println("Delete user response body: " + apiDELETEResponse.text());


        // 3. GET user --> same user id --> 404 error

        // Send GET request with query parameters
        APIResponse apiResponse = requestContext.get(
                "https://gorest.co.in/public/v2/users/" + userId,
                RequestOptions.create()
                        .setHeader(
                                "Authorization", "Bearer 82d10ccf29029a3e8124f2280b32f3ceb2beac1dcb9370d10ae5f50f0e9b015b"
        ));

        System.out.println(apiResponse.text());

        // Get and print response status code
        int statusCode = apiResponse.status();
        System.out.println("Response code: " + statusCode);

        // Verify status code is 200
        Assert.assertEquals(statusCode, 404);
        Assert.assertEquals(apiResponse.statusText(), "Not Found");

        Assert.assertTrue(apiResponse.text().contains("Resource not found"));
    }

    @AfterTest
    public void tearDown() {

        // Close Playwright and release resources
        playwright.close();
    }





}
