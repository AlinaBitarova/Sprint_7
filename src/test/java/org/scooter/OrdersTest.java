package org.scooter;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collections;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)
public class OrdersTest {

    private AdditionalData additionalData;
    private final List<String> scooterColors;

    public OrdersTest(List<String> scooterColors) {
        this.scooterColors = scooterColors;
    }

    @Before
    public void setUp() {
        additionalData = new AdditionalData();
    }

    @Parameterized.Parameters
    public static Object[][] getOrdersData() {
        return new Object[][]{
                {List.of("BLACK")},
                {List.of("GREY")},
                {List.of("BLACK", "GREY")},
                {Collections.emptyList()}
        };
    }

    @Test
    @Description("Check status code of ordering scooters with different colors")
    public void testOrderCanBeCreatedWithDifferentColors() {
        Orders order = new Orders(
                "Yuri",
                "Katsuki",
                "Tokyo, 17 apt.",
                "4",
                "+7 999 888 77 66",
                4,
                "2026-08-10",
                "Next to the Winter Sports Stadium",
                scooterColors
        );
        Response response = additionalData.createOrder(order);
        response.then()
                .statusCode(SC_CREATED)
                .body("track", notNullValue());
    }
}