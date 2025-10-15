package ru.praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.praktikum.client.ApiClient;
import ru.praktikum.generator.UserGenerator;
import ru.praktikum.model.User;

import static org.hamcrest.Matchers.*;

public class UserLoginTest {
    private String accessToken;
    private User user;

    @Before
    public void setUp() {
        user = UserGenerator.getRandomUser();

        Response registerResponse = ApiClient.createUser(user);
        registerResponse.then().statusCode(200);
        accessToken = registerResponse.path("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            ApiClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Вход под существующим пользователем")
    public void testLoginWithExistingUser() {
        Response loginResponse = ApiClient.loginUser(user);

        loginResponse.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Вход с неверным email")
    public void testLoginWithInvalidEmail() {
        User invalidUser = new User(
                "invalid@yandex.ru",
                user.getPassword(),
                user.getName()
        );

        Response response = ApiClient.loginUser(invalidUser);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Вход с неверным паролем")
    public void testLoginWithInvalidPassword() {
        User invalidUser = new User(
                user.getEmail(),
                "wrongpassword",
                user.getName()
        );

        Response response = ApiClient.loginUser(invalidUser);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}