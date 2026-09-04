package com.qa.api.tests;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import org.testng.annotations.Test;

public class GETAPICall {

    @Test
    public void getUsersApiTest(){

        //create an instance
        Playwright playwright = Playwright.create();

        APIRequest request = playwright.request();

        APIRequestContext requestContext = request.newContext();
        APIResponse apiResponse = requestContext.get("https://gorest.co.in/public/v2/users");
    }

}
