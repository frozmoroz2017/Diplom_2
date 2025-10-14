package ru.praktikum.client;

import io.restassured.response.Response;
import ru.praktikum.model.Order;
import ru.praktikum.model.User;

import static io.restassured.RestAssured.given;

public class ApiClient {
    private static final String BASE_URL = "https://stellarburgers.education-services.ru/api";

    public static Response createUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .body(user)
                .when()
                .post("/auth/register");
    }

    public static Response loginUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .body(user)
                .when()
                .post("/auth/login");
    }

    public static Response deleteUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .baseUri(BASE_URL)
                .when()
                .delete("/auth/user");
    }

    public static Response getIngredients() {
        return given()
                .baseUri(BASE_URL)
                .when()
                .get("/ingredients");
    }

    public static Response createOrder(Order order, String accessToken) {
        if (accessToken != null) {
            return given()
                    .header("Content-type", "application/json")
                    .header("Authorization", accessToken)
                    .baseUri(BASE_URL)
                    .body(order)
                    .when()
                    .post("/orders");
        } else {
            return given()
                    .header("Content-type", "application/json")
                    .baseUri(BASE_URL)
                    .body(order)
                    .when()
                    .post("/orders");
        }
    }
}