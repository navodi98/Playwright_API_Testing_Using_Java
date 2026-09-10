package com.qa.api.tests.POST;

import com.api.data.User;
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

public class CreateUserPostCallWithPOJOTest {

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
    public void CreateUserPostCallWithPOJOTest() throws IOException {

        // Generate a unique email
        String email = generateRandomEmail();

        // Create a User object with the test data
        User user = new User(
                "Sanduni QA",
                email,
                "female",
                "active"
        );

        // Send a POST request to create a new user
        APIResponse apiPostResponse = requestContext.post(
                "https://gorest.co.in/public/v2/users",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setHeader(
                                "Authorization",
                                "Bearer 82d10ccf29029a3e8124f2280b32f3ceb2beac1dcb9370d10ae5f50f0e9b015b"
                        )
                        .setData(user)
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
        Assert.assertEquals(actualUser.getName(), user.getName());
        Assert.assertEquals(actualUser.getEmail(), user.getEmail());
        Assert.assertEquals(actualUser.getStatus(), user.getStatus());
        Assert.assertEquals(actualUser.getGender(), user.getGender());

        // Verify that the server generated an ID for the new user
        Assert.assertNotNull(actualUser.getId());
    }

    @AfterTest
    public void tearDown() {

        // Close Playwright and release resources
        playwright.close();
    }
}
