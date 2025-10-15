package ru.praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.praktikum.client.ApiClient;
import ru.praktikum.model.Order;
import ru.praktikum.model.User;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;

public class OrderCreationTest {
    private String accessToken;
    private List<String> validIngredients;
    private User user;

    @Before
    public void setUp() {
        user = new User(
                "orderuser_" + System.currentTimeMillis() + "@yandex.ru",
                "password123",
                "OrderUser"
        );

        Response registerResponse = ApiClient.createUser(user);
        registerResponse.then().statusCode(200);
        accessToken = registerResponse.path("accessToken");

        Response ingredientsResponse = ApiClient.getIngredients();
        ingredientsResponse.then().statusCode(200);

        validIngredients = Arrays.asList(
                ingredientsResponse.jsonPath().getString("data[0]._id"),
                ingredientsResponse.jsonPath().getString("data[1]._id")
        );
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            ApiClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void testCreateOrderWithAuth() {
        Order order = new Order(validIngredients);
        Response orderResponse = ApiClient.createOrder(order, accessToken);

        orderResponse.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void testCreateOrderWithoutAuth() {
        Order order = new Order(validIngredients);
        Response response = ApiClient.createOrder(order, null);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами")
    public void testCreateOrderWithIngredients() {
        Order order = new Order(validIngredients);
        Response response = ApiClient.createOrder(order, null);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void testCreateOrderWithoutIngredients() {
        Order order = new Order(null);
        Response response = ApiClient.createOrder(order, null);

        response.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void testCreateOrderWithInvalidIngredientHash() {
        List<String> invalidIngredients = Arrays.asList("invalid_hash_1", "invalid_hash_2");
        Order order = new Order(invalidIngredients);
        Response response = ApiClient.createOrder(order, null);

        response.then().statusCode(500);
    }
}