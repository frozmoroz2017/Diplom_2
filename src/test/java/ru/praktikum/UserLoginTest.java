package ru.praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import ru.praktikum.model.User;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserLoginTest {
    private String accessToken;

    @After
    public void tearDown() {
        if (accessToken != null) {
            given()
                    .header("Authorization", accessToken)
                    .delete("https://stellarburgers.nomoreparties.site/api/auth/user");
        }
    }

    @Test
    @DisplayName("Вход под существующим пользователем")
    public void testLoginWithExistingUser() {
        User user = new User("loginuser_" + System.currentTimeMillis() + "@yandex.ru",
                "password123", "LoginUser");

        Response registerResponse = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("https://stellarburgers.nomoreparties.site/api/auth/register");
        registerResponse.then().statusCode(200);
        accessToken = registerResponse.path("accessToken");

        Response loginResponse = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("https://stellarburgers.nomoreparties.site/api/auth/login");

        loginResponse.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Вход с неверным логином и паролем")
    public void testLoginWithInvalidCredentials() {
        User user = new User("invalid@yandex.ru", "wrongpassword", "InvalidUser");

        Response response = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("https://stellarburgers.nomoreparties.site/api/auth/login");

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}