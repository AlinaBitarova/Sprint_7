package org.scooter;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Map;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;

public class LoginTest {

    private AdditionalData additionalData;
    private Courier courier;

    @Before
    public void setUp() {
        additionalData = new AdditionalData();
        courier = additionalData.getRandomCourier();
        additionalData.createCourier(courier);
    }

    @Test
    @Description("Check status code of valid log in")
    public void testCourierCanLogInWithValidData() {
        Map<String, String> credentials = Map.of(
                "login", courier.getLogin(),
                "password", courier.getPassword()
        );
        Response response = additionalData.loginCourier(credentials);
        response.then()
                .statusCode(SC_OK)
                .body("id", notNullValue());
    }

    @Test
    @Description("Check status code of invalid log in without login")
    public void testTryingToLogInWithoutLoginReturnsError() {
        Map<String, String> credentials = Map.of(
                "password", courier.getPassword()
        );
        Response response = additionalData.loginCourier(credentials);
        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", containsString(AdditionalData.LACK_DATA_TO_LOGIN));
    }

    @Test
    @Description("Check status code of invalid log in without password")
    public void testTryingToLogInWithoutPasswordReturnsError() {
        Map<String, String> credentials = Map.of(
                "login", courier.getLogin()
        );
        Response response = additionalData.loginCourier(credentials);
        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", containsString(AdditionalData.LACK_DATA_TO_LOGIN));
    }

    @Test
    @Description("Check status code of invalid log in with incorrect login")
    public void testLoggingInWithIncorrectLoginReturnsError() {
        Map<String, String> wrongCredentials = Map.of(
                "login", "incorrect_login_654",
                "password", courier.getPassword()
        );
        Response response = additionalData.loginCourier(wrongCredentials);
        response.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", containsString(AdditionalData.NO_SUCH_ACCOUNT));
    }

    @Test
    @Description("Check status code of invalid log in with incorrect password")
    public void testLoggingInWithIncorrectPasswordReturnsError() {
        Map<String, String> wrongCredentials = Map.of(
                "login", courier.getLogin(),
                "password", "incorrect_password_987"
        );
        Response response = additionalData.loginCourier(wrongCredentials);
        response.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", containsString(AdditionalData.NO_SUCH_ACCOUNT));
    }

    @Test
    @Description("Check status code of invalid log in with unregistered credentials")
    public void testLoggingInWithUnregisteredUserReturnsError() {
        Map<String, String> unregisteredCredentials = Map.of(
                "login", "unregistered_login",
                "password", "unregistered_password"
        );
        Response response = additionalData.loginCourier(unregisteredCredentials);
        response.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", containsString(AdditionalData.NO_SUCH_ACCOUNT));
    }

    @After
    public void tearDown() {
        additionalData.deleteCourier(courier);
    }
}