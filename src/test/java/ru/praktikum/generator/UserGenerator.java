package ru.praktikum.generator;

import ru.praktikum.model.User;

public class UserGenerator {
    public static User getRandomUser() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return new User(
                "testuser_" + timestamp + "@yandex.ru",
                "password" + timestamp,
                "TestUser_" + timestamp
        );
    }

    public static User getUserWithoutEmail() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return new User(
                null,
                "password" + timestamp,
                "TestUser_" + timestamp
        );
    }

    public static User getUserWithoutPassword() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return new User(
                "testuser_" + timestamp + "@yandex.ru",
                null,
                "TestUser_" + timestamp
        );
    }

    public static User getUserWithoutName() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return new User(
                "testuser_" + timestamp + "@yandex.ru",
                "password" + timestamp,
                null
        );
    }

    public static User getExistingUser() {
        return new User(
                "existinguser@yandex.ru",
                "password123",
                "ExistingUser"
        );
    }
}