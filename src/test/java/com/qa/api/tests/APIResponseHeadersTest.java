package com.qa.api.tests;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.HttpHeader;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.util.List;
import java.util.Map;

public class APIResponseHeadersTest {


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
    public void getHeadersTest() {

        // Send a GET request to get all users
        APIResponse apiResponse = requestContext.get("https://gorest.co.in/public/v2/users");

        // Get the response status code
        int statusCode = apiResponse.status();
        System.out.println("Response code: " + statusCode);

        // Verify the status code is 200 (Success)
        Assert.assertEquals(statusCode, 200);

        // Get all response headers as a Map
        Map<String, String> headersMap = apiResponse.headers();

        // Print all headers
        headersMap.forEach((key1, value1) -> System.out.println(key1 + ":" + value1));

        // Print total number of headers
        System.out.println("Total no. of response headers: " + headersMap.size());

        // Verify specific header values
        Assert.assertEquals(headersMap.get("server"), "cloudflare");
        Assert.assertEquals(headersMap.get("x-frame-options"), "SAMEORIGIN");

        System.out.println("=========================================================");

        // Get all headers as a List
        List<HttpHeader> httpHeaderList = apiResponse.headersArray();

        // Print each header from the list
        for (HttpHeader e : httpHeaderList) {
            System.out.println(e.name + ":" + e.value);
        }
    }


    @AfterTest
    public void tearDown() {

        // Close Playwright and release resources
        playwright.close();
    }

}
