package org.scooter;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;


public class OrdersListTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @Description("Check status code of getting the order list")
    public void testGetOrderList() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get(AdditionalData.ORDER_PATH)
                .then()
                .statusCode(200)
                .body("orders.size()", greaterThan(0));
    }
}