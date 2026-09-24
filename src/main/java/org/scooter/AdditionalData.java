package org.scooter;

import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.datafaker.Faker;

import java.util.Map;

import static org.apache.http.HttpStatus.*;
import static io.restassured.RestAssured.given;

public class AdditionalData {

    public static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    public static final String COURIER_CREATION_PATH = "/api/v1/courier";
    public static final String COURIER_LOGIN_PATH = "/api/v1/courier/login";
    public static final String ORDER_PATH = "/api/v1/orders";

    public static final String LACK_DATA_TO_CREATE_COURIER = "Недостаточно данных для создания учетной записи";
    public static final String SAME_LOGIN_ERROR = "Этот логин уже используется";
    public static final String LACK_DATA_TO_LOGIN = "Недостаточно данных для входа";
    public static final String NO_SUCH_ACCOUNT = "Учетная запись не найдена";

    private static final Faker faker = new Faker();

    @Step("Requesting Base Url")
    public RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .build();
    }

    @Step("Sending POST request to create a courier")
    public Response createCourier(Courier courier) {
        return given()
                .spec(getRequestSpec())
                .body(courier)
                .post(COURIER_CREATION_PATH);
    }

    @Step("Sending POST request to login a courier")
    public Response loginCourier(Object credentials) {
        return given()
                .spec(getRequestSpec())
                .body(credentials)
                .post(COURIER_LOGIN_PATH);
    }

    @Step("Sending POST request to create an order")
    public Response createOrder(Orders order) {
        return given()
                .spec(getRequestSpec())
                .body(order)
                .post(ORDER_PATH);
    }

    @Step("Sending GET request to receive order list")
    public Response getOrderList() {
        return given()
                .spec(getRequestSpec())
                .get(ORDER_PATH);
    }

    @Step("Creating a valid courier via Faker")
    public Courier getRandomCourier() {
        return new Courier(
                faker.name().username(),
                faker.internet().password(),
                faker.name().firstName()
        );
    }

    @Step("Creating an invalid courier without login")
    public Courier getCourierWithoutLogin() {
        return new Courier(null, faker.internet().password(), faker.name().firstName());
    }

    @Step("Creating an invalid courier without password")
    public Courier getCourierWithoutPassword() {
        return new Courier(faker.name().username(), null, faker.name().firstName());
    }

    @Step("Deleting a courier (with logging in)")
    public void deleteCourier(Courier courier) {
        if (courier == null || courier.getLogin() == null || courier.getPassword() == null) {
            return;
        }

        Response loginResponse = given()
                .spec(getRequestSpec())
                .body(Map.of("login", courier.getLogin(), "password", courier.getPassword()))
                .post(COURIER_LOGIN_PATH);

        if (loginResponse.getStatusCode() == SC_OK) {
            int courierId = loginResponse.path("id");

            given()
                    .spec(getRequestSpec())
                    .body(Map.of("id", courierId))
                    .when()
                    .delete(COURIER_CREATION_PATH + "/" + courierId)
                    .then()
                    .statusCode(SC_OK);
        }
    }


}

