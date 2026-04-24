package com.smartration;

public class LoggedInUser {
    private static String email;

    public static void setEmail(String emailValue) {
        email = emailValue;
    }

    public static String getEmail() {
        return email;
    }
}
