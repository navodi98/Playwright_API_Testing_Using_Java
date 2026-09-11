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

public class BookingTest {

    // Playwright API objects
    Playwright playwright;
    APIRequest request;
    APIRequestContext requestContext;

    // Token will be stored here after authentication
    private String token;

    // Booking ID will be stored here after creating a booking
    private int bookingId;

    @BeforeTest
    public void setUp() throws IOException {

        // Start Playwright
        playwright = Playwright.create();

        // Create API request object
        request = playwright.request();

        // Create API request context
        requestContext = request.newContext();

        // Login request body
        String requestTokenJsonBody = "{\n" +
                "  \"username\": \"admin\",\n" +
                "  \"password\": \"password123\"\n" +
                "}";

        // Send POST request to get authentication token
        APIResponse apiPostTokenResponse = requestContext.post(
                "https://restful-booker.herokuapp.com/auth",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(requestTokenJsonBody)
        );

        // Verify token request was successful
        Assert.assertEquals(apiPostTokenResponse.status(), 200);

        // Convert response into JsonNode
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode apiJsonResponse =
                objectMapper.readTree(apiPostTokenResponse.body());

        // Print token response
        System.out.println("Token Response:");
        System.out.println(apiJsonResponse.toPrettyString());

        // Get token from response
        token = apiJsonResponse.get("token").asText();

        System.out.println("Token ID: " + token);
    }


    @Test
    public void createUpdateDeleteBookingTest() throws IOException {

        /*
         * ---------------------------------------------------
         * 1. CREATE BOOKING
         * ---------------------------------------------------
         */

        String bookingJson = "{\n" +
                "  \"firstname\": \"James\",\n" +
                "  \"lastname\": \"Brown\",\n" +
                "  \"totalprice\": 555,\n" +
                "  \"depositpaid\": true,\n" +
                "  \"bookingdates\": {\n" +
                "    \"checkin\": \"2024-01-01\",\n" +
                "    \"checkout\": \"2024-01-01\"\n" +
                "  },\n" +
                "  \"additionalneeds\": \"Breakfast\"\n" +
                "}";

        // Send POST request to create a new booking
        APIResponse createBookingResponse = requestContext.post(
                "https://restful-booker.herokuapp.com/booking",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(bookingJson)
        );

        // Print Create response
        System.out.println("\n========== CREATE BOOKING ==========");
        System.out.println("Status: "
                + createBookingResponse.status()
                + ":"
                + createBookingResponse.statusText());

        System.out.println("URL: " + createBookingResponse.url());
        System.out.println(createBookingResponse.text());

        // Verify booking was created successfully
        Assert.assertEquals(createBookingResponse.status(), 200);

        /*
         * The Create Booking response contains:
         *
         * {
         *   "bookingid": 123,
         *   "booking": {
         *      ...
         *   }
         * }
         */

        // Convert response into JsonNode
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode createBookingJson =
                objectMapper.readTree(createBookingResponse.body());

        // Get dynamically created booking ID
        bookingId = createBookingJson.get("bookingid").asInt();

        System.out.println("Created Booking ID: " + bookingId);

        // Make sure booking ID was created
        Assert.assertTrue(bookingId > 0);


        /*
         * ---------------------------------------------------
         * 2. UPDATE BOOKING
         * ---------------------------------------------------
         */

        String updateBookingJson = "{\n" +
                "  \"firstname\": \"James Updated\",\n" +
                "  \"lastname\": \"Brown\",\n" +
                "  \"totalprice\": 600,\n" +
                "  \"depositpaid\": true,\n" +
                "  \"bookingdates\": {\n" +
                "    \"checkin\": \"2024-02-01\",\n" +
                "    \"checkout\": \"2024-02-05\"\n" +
                "  },\n" +
                "  \"additionalneeds\": \"Breakfast and Dinner\"\n" +
                "}";

        // Create the dynamic booking URL
        String bookingUrl =
                "https://restful-booker.herokuapp.com/booking/" + bookingId;

        // Send PUT request using the dynamically created booking ID
        APIResponse updateBookingResponse = requestContext.put(
                bookingUrl,
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Cookie", "token=" + token)
                        .setData(updateBookingJson)
        );

        // Print Update response
        System.out.println("\n========== UPDATE BOOKING ==========");
        System.out.println("Status: "
                + updateBookingResponse.status()
                + ":"
                + updateBookingResponse.statusText());

        System.out.println("URL: " + updateBookingResponse.url());
        System.out.println(updateBookingResponse.text());

        // Verify booking was updated successfully
        Assert.assertEquals(updateBookingResponse.status(), 200);

        // Verify updated first name
        JsonNode updateBookingJsonResponse =
                objectMapper.readTree(updateBookingResponse.body());

        Assert.assertEquals(
                updateBookingJsonResponse.get("firstname").asText(),
                "James Updated"
        );


        /*
         * ---------------------------------------------------
         * 3. DELETE BOOKING
         * ---------------------------------------------------
         */

        // Send DELETE request using the same dynamic booking ID
        APIResponse deleteBookingResponse = requestContext.delete(
                bookingUrl,
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Cookie", "token=" + token)
        );

        // Print Delete response
        System.out.println("\n========== DELETE BOOKING ==========");
        System.out.println("Status: "
                + deleteBookingResponse.status()
                + ":"
                + deleteBookingResponse.statusText());

        System.out.println("URL: " + deleteBookingResponse.url());
        System.out.println(deleteBookingResponse.text());

        // Restful Booker returns 201 when booking is deleted
        Assert.assertEquals(deleteBookingResponse.status(), 201);

        // Verify delete response
        Assert.assertTrue(
                deleteBookingResponse.text().contains("Created")
        );
    }


    @AfterTest
    public void tearDown() {

        // Close Playwright and release resources
        playwright.close();
    }
}