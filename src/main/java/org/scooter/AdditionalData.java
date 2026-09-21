package org.scooter;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.datafaker.Faker;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class AdditionalData {

    public static final String COURIER_CREATION_PATH = "/api/v1/courier";
    public static final String COURIER_LOGIN_PATH = "/api/v1/courier/login";
    public static final String ORDER_PATH = "/api/v1/orders";

    public static final String LACK_DATA_TO_CREATE_COURIER = "Недостаточно данных для создания учетной записи";
    public static final String SAME_LOGIN_ERROR = "Этот логин уже используется";
    public static final String LACK_DATA_TO_LOGIN = "Недостаточно данных для входа";
    public static final String NO_SUCH_ACCOUNT = "Учетная запись не найдена";

    private static final Faker faker = new Faker();

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
                .contentType(ContentType.JSON)
                .body(Map.of("login", courier.getLogin(), "password", courier.getPassword()))
                .post(COURIER_LOGIN_PATH);

        if (loginResponse.getStatusCode() == 200) {
            int courierId = loginResponse.path("id");

            given()
                    .delete(COURIER_CREATION_PATH + "/" + courierId);
        }
    }
}

