package org.scooter;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

public class CourierTest {

    private AdditionalData additionalData;
    private Courier courier;

    @Before
    public void setUp() {
        additionalData = new AdditionalData();
        courier = additionalData.getRandomCourier();
    }

    @Test
    @Description("Check status code of valid courier creation")
    public void testCourierCanBeCreatedWithValidData() {
        Response response = additionalData.createCourier(courier);
        response.then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));
    }

    @Test
    @Description("Check status code of two identical couriers creation")
    public void testCantCreateTwoIdenticalCouriers() {
        additionalData.createCourier(courier);
        Response response = additionalData.createCourier(courier);
        response.then()
                .statusCode(SC_CONFLICT)
                .body("message", containsString(AdditionalData.SAME_LOGIN_ERROR));
    }

    @Test
    @Description("Check status code of invalid courier creation without login")
    public void testCourierCantBeCreatedWithoutLogin() {
        Courier courierWithoutLogin = additionalData.getCourierWithoutLogin();
        Response response = additionalData.createCourier(courierWithoutLogin);
        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", containsString(AdditionalData.LACK_DATA_TO_CREATE_COURIER));
    }

    @Test
    @Description("Check status code of invalid courier creation without password")
    public void testCourierCantBeCreatedWithoutPassword() {
        Courier courierWithoutPassword = additionalData.getCourierWithoutPassword();
        Response response = additionalData.createCourier(courierWithoutPassword);
        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", containsString(AdditionalData.LACK_DATA_TO_CREATE_COURIER));
    }

    @Test
    @Description("Check status code of invalid courier creation with already existing login")
    public void testCantCreateCourierWithExistingLogin() {
        additionalData.createCourier(courier);
        Courier identicalLoginCourier = additionalData.getRandomCourier();
        identicalLoginCourier.setLogin(courier.getLogin());
        Response response = additionalData.createCourier(identicalLoginCourier);
        response.then()
                .statusCode(SC_CONFLICT)
                .body("message", containsString(AdditionalData.SAME_LOGIN_ERROR));
    }

    @After
    public void tearDown() {
        additionalData.deleteCourier(courier);
    }

}