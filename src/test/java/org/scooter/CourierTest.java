package org.scooter;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

public class CourierTest {

    private AdditionalData additionalData;
    private Courier courier;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        additionalData = new AdditionalData();
        courier = additionalData.getRandomCourier();
    }

    @Test
    @Description("Check status code of valid courier creation")
    public void testCourierCanBeCreatedWithValidData() {
        given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post(AdditionalData.COURIER_CREATION_PATH)
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Test
    @Description("Check status code of two identical couriers creation")
    public void testCantCreateTwoIdenticalCouriers() {
        given()
                .contentType(ContentType.JSON)
                .body(courier)
                .post(AdditionalData.COURIER_CREATION_PATH);

        given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post(AdditionalData.COURIER_CREATION_PATH)
                .then()
                .statusCode(409)
                .body("message", containsString(AdditionalData.SAME_LOGIN_ERROR));
    }

    @Test
    @Description("Check status code of invalid courier creation without login")
    public void testCourierCantBeCreatedWithoutLogin() {
        Courier courierWithoutLogin = additionalData.getCourierWithoutLogin();
        given()
                .contentType(ContentType.JSON)
                .body(courierWithoutLogin)
                .when()
                .post(AdditionalData.COURIER_CREATION_PATH)
                .then()
                .statusCode(400)
                .body("message", containsString(AdditionalData.LACK_DATA_TO_CREATE_COURIER));
    }

    @Test
    @Description("Check status code of invalid courier creation without password")
    public void testCourierCantBeCreatedWithoutPassword() {
        Courier courierWithoutPassword = additionalData.getCourierWithoutPassword();
        given()
                .contentType(ContentType.JSON)
                .body(courierWithoutPassword)
                .when()
                .post(AdditionalData.COURIER_CREATION_PATH)
                .then()
                .statusCode(400)
                .body("message", containsString(AdditionalData.LACK_DATA_TO_CREATE_COURIER));
    }

    @Test
    @Description("Check status code of invalid courier creation with already existing login")
    public void testCantCreateCourierWithExistingLogin() {
        given()
                .contentType(ContentType.JSON)
                .body(courier)
                .post(AdditionalData.COURIER_CREATION_PATH);

        Courier identicalLoginCourier = additionalData.getRandomCourier();
        identicalLoginCourier.setLogin(courier.getLogin());
        given()
                .contentType(ContentType.JSON)
                .body(identicalLoginCourier)
                .when()
                .post(AdditionalData.COURIER_CREATION_PATH)
                .then()
                .statusCode(409)
                .body("message", containsString(AdditionalData.SAME_LOGIN_ERROR));
    }

    @After
    public void tearDown() {
        additionalData.deleteCourier(courier);
    }

}