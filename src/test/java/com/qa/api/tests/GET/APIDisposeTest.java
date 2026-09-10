package com.qa.api.tests.GET;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class APIDisposeTest {

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
    public void disposeResponseTest() {

        // Send GET request to retrieve all users
        // Request no:1
        APIResponse apiResponse1 = requestContext.get("https://gorest.co.in/public/v2/users");

        // Get and print response status code
        int statusCode = apiResponse1.status();
        System.out.println("Response code: " + statusCode);

        // Verify status code is 200
        Assert.assertEquals(statusCode, 200);

        // Get and print status text
        String statusText = apiResponse1.statusText();
        System.out.println("Response text: " + statusText);

        // Print raw response body
        System.out.println("--print API response with plain text---");
        System.out.println(apiResponse1.text());

        // Only dispose the response body but the status code, url, status text are remain same
        apiResponse1.dispose();

        System.out.println();

        int statusCodeAfterDispose = apiResponse1.status();
        System.out.println("response status code after dispose: " + statusCodeAfterDispose);

        System.out.println("response url: " + apiResponse1.url());


        // Request no:2
        APIResponse apiResponse2 = requestContext.get("https://reqres.in/api/users/2");

        System.out.println("get response body for the 2nd request: ");
        System.out.println("status code: " + apiResponse2.status());
        System.out.println("response body: " + apiResponse2.text());

        //request context dispose
        requestContext.dispose();

    }


    @AfterTest
    public void tearDown() {

        // Close Playwright and release resources
        playwright.close();
    }

}
