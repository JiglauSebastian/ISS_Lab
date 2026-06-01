package com.slim.utils;

public class PasswordUtil {

    private PasswordUtil() {}

    public static boolean matches(String rawPassword, String storedPassword) {
        return rawPassword != null && rawPassword.equals(storedPassword);
    }
}
