package ru.praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import ru.praktikum.client.ApiClient;
import ru.praktikum.generator.UserGenerator;
import ru.praktikum.model.User;

import static org.hamcrest.Matchers.*;

public class UserCreationTest {
    private String accessToken;

    @After
    public void tearDown() {
        if (accessToken != null) {
            ApiClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    public void testCreateUniqueUser() {
        User user = UserGenerator.getRandomUser();

        Response response = ApiClient.createUser(user);

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
        User user = UserGenerator.getRandomUser();

        Response firstResponse = ApiClient.createUser(user);
        firstResponse.then().statusCode(200);
        accessToken = firstResponse.path("accessToken");

        Response secondResponse = ApiClient.createUser(user);

        secondResponse.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    public void testCreateUserWithoutEmail() {
        User user = UserGenerator.getUserWithoutEmail();

        Response response = ApiClient.createUser(user);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    public void testCreateUserWithoutPassword() {
        User user = UserGenerator.getUserWithoutPassword();

        Response response = ApiClient.createUser(user);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    public void testCreateUserWithoutName() {
        User user = UserGenerator.getUserWithoutName();

        Response response = ApiClient.createUser(user);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}