package ru.praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import ru.praktikum.model.User;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserCreationTest {
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
    @DisplayName("Создание уникального пользователя")
    public void testCreateUniqueUser() {
        User user = new User("testuser_" + System.currentTimeMillis() + "@yandex.ru",
                "password123", "TestUser");

        Response response = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("https://stellarburgers.nomoreparties.site/api/auth/register");

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));

        accessToken = response.path("accessToken");
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    public void testCreateExistingUser() {
        User user = new User("existinguser_" + System.currentTimeMillis() + "@yandex.ru",
                "password123", "ExistingUser");

        Response firstResponse = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("https://stellarburgers.nomoreparties.site/api/auth/register");
        firstResponse.then().statusCode(200);
        accessToken = firstResponse.path("accessToken");

        Response secondResponse = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("https://stellarburgers.nomoreparties.site/api/auth/register");

        secondResponse.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без заполнения обязательного поля")
    public void testCreateUserWithoutRequiredField() {
        User user = new User("testuser_" + System.currentTimeMillis() + "@yandex.ru",
                null, "TestUser");

        Response response = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("https://stellarburgers.nomoreparties.site/api/auth/register");

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}