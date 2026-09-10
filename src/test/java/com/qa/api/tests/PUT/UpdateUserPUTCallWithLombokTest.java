package com.qa.api.tests.PUT;

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

public class UpdateUserPUTCallWithLombokTest {


    /*Flow
        1. POST - fetch te user id --> 123
        2. PUT - use the same user id --> /123
        3. GET --> /123
     */


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
    public void UpdateUserPUTCallWithLombokTest() throws IOException {

        // Generate a unique email
        String email = generateRandomEmail();

        // Create a Users object : using builder pattern
        Users users = Users.builder()
                .name("Sanduni")
                .email(email)
                .gender("female")
                .status("active").build();


        // 1. POST call --> create a user
        // Send a POST request to create a new user
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

        // Verify the response status text
        Assert.assertEquals(apiPostResponse.statusText(), "Created");

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

        // Verify that the response contains the correct user details
        Assert.assertEquals(actualUser.getName(), users.getName());
        Assert.assertEquals(actualUser.getEmail(), users.getEmail());
        Assert.assertEquals(actualUser.getStatus(), users.getStatus());
        Assert.assertEquals(actualUser.getGender(), users.getGender());

        // Verify that the server generated an ID for the new user
        Assert.assertNotNull(actualUser.getId());


        String userID = actualUser.getId();
        System.out.println("New user Id: " + userID);

        // Update the status (active --> inactive)
        users.setStatus("inactive");
        // Update the name
        users.setName("Taniya");

        System.out.println("--------------------------------PUT CALL---------------------------------");

        // 2. PUT call --> update the user
        APIResponse apiPUTResponse = requestContext.put(
                "https://gorest.co.in/public/v2/users/" + userID,
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setHeader(
                                "Authorization",
                                "Bearer 82d10ccf29029a3e8124f2280b32f3ceb2beac1dcb9370d10ae5f50f0e9b015b"
                        )
                        .setData(users)
        );

        System.out.println(apiPUTResponse.status() + ": " + apiPUTResponse.statusText());
        // Validate the status
        Assert.assertEquals(apiPUTResponse.status(), 200);

        String putResponseText = apiPUTResponse.text();
        Users actualPUTUser = objectMapper.readValue(putResponseText, Users.class);

        System.out.println("Updated user: " + putResponseText);

        Assert.assertEquals(actualPUTUser.getId(), userID);
        Assert.assertEquals(actualPUTUser.getStatus(), users.getStatus());
        Assert.assertEquals(actualPUTUser.getName(), users.getName());


        // 3. get te updated user with GET call
        APIResponse apiGETResponse = requestContext.get("https://gorest.co.in/public/v2/users/" + userID,
                RequestOptions.create()
                        .setHeader("Authorization", "Bearer 82d10ccf29029a3e8124f2280b32f3ceb2beac1dcb9370d10ae5f50f0e9b015b"));

        // Get and print response status code
        int statusCode = apiGETResponse.status();
        System.out.println("Response code: " + statusCode);

        // Verify status code is 200
        Assert.assertEquals(statusCode, 200);

        // Get and print status text
        String statusGETStatusText = apiGETResponse.statusText();
        System.out.println(statusGETStatusText);

        String getResponseText = apiGETResponse.text();


        Users actualGETUser = objectMapper.readValue(getResponseText, Users.class);
        Assert.assertEquals(actualGETUser.getId(), userID);
        Assert.assertEquals(actualGETUser.getStatus(), users.getStatus());
        Assert.assertEquals(actualGETUser.getName(), users.getName());
    }

    @AfterTest
    public void tearDown() {

        // Close Playwright and release resources
        playwright.close();
    }



}
