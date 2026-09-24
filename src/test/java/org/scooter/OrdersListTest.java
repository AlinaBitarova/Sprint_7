package org.scooter;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.greaterThan;


public class OrdersListTest {

    private AdditionalData additionalData;

    @Before
    public void setUp() {
        additionalData = new AdditionalData();
    }

    @Test
    @Description("Check status code of getting the order list")
    public void testGetOrderList() {
        Response response = additionalData.getOrderList();
        response.then()
                .statusCode(SC_OK)
                .body("orders.size()", greaterThan(0));
    }
}