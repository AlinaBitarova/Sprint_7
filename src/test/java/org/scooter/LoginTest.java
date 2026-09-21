package org.scooter;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

public class LoginTest {

    private AdditionalData additionalData;
    private Courier courier;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        additionalData = new AdditionalData();
        courier = additionalData.getRandomCourier();
        given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post(AdditionalData.COURIER_CREATION_PATH);
    }

    @Test
    @Description("Check status code of valid log in")
    public void testCourierCanLogInWithValidData() {
        Map<String, String> credentials = Map.of(
                "login", courier.getLogin(),
                "password", courier.getPassword()
        );
        given()
                .contentType(ContentType.JSON)
                .body(credentials)
                .when()
                .post(AdditionalData.COURIER_LOGIN_PATH)
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @Description("Check status code of invalid log in without login")
    public void testTryingToLogInWithoutLoginReturnsError() {
        Map<String, String> credentials = Map.of(
                "password", courier.getPassword()
        );
        given()
                .contentType(ContentType.JSON)
                .body(credentials)
                .when()
                .post(AdditionalData.COURIER_LOGIN_PATH)
                .then()
                .statusCode(400)
                .body("message", containsString(AdditionalData.LACK_DATA_TO_LOGIN));
    }

    @Test
    @Description("Check status code of invalid log in without password")
    public void testTryingToLogInWithoutPasswordReturnsError() {
        Map<String, String> credentials = Map.of(
                "login", courier.getLogin()
        );
        given()
                .contentType(ContentType.JSON)
                .body(credentials)
                .when()
                .post(AdditionalData.COURIER_LOGIN_PATH)
                .then()
                .statusCode(400)
                .body("message", containsString(AdditionalData.LACK_DATA_TO_LOGIN));
    }

    @Test
    @Description("Check status code of invalid log in with incorrect login")
    public void testLoggingInWithIncorrectLoginReturnsError() {
        Map<String, String> wrongCredentials = Map.of(
                "login", "incorrect_login_654",
                "password", courier.getPassword()
        );
        given()
                .contentType(ContentType.JSON)
                .body(wrongCredentials)
                .when()
                .post(AdditionalData.COURIER_LOGIN_PATH)
                .then()
                .statusCode(404)
                .body("message", containsString(AdditionalData.NO_SUCH_ACCOUNT));
    }

    @Test
    @Description("Check status code of invalid log in with incorrect password")
    public void testLoggingInWithIncorrectPasswordReturnsError() {
        Map<String, String> wrongCredentials = Map.of(
                "login", courier.getLogin(),
                "password", "incorrect_password_987"
        );
        given()
                .contentType(ContentType.JSON)
                .body(wrongCredentials)
                .when()
                .post(AdditionalData.COURIER_LOGIN_PATH)
                .then()
                .statusCode(404)
                .body("message", containsString(AdditionalData.NO_SUCH_ACCOUNT));
    }

    @Test
    @Description("Check status code of invalid log in with unregistered credentials")
    public void testLoggingInWithUnregisteredUserReturnsError() {
        Map<String, String> unregisteredCredentials = Map.of(
                "login", "unregistered_login",
                "password", "unregistered_password"
        );
        given()
                .contentType(ContentType.JSON)
                .body(unregisteredCredentials)
                .when()
                .post(AdditionalData.COURIER_LOGIN_PATH)
                .then()
                .statusCode(404)
                .body("message", containsString(AdditionalData.NO_SUCH_ACCOUNT));
    }

    @After
    public void tearDown() {
        additionalData.deleteCourier(courier);
    }
}