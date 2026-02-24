package com.example.moodle.util;

import java.util.HashMap;

import com.example.moodle.model.User;

public class UserStore {

    private static HashMap<String, User> users = new HashMap<>();

    public static void addUser(User user) {
        users.put(user.getEmail(), user);
    }

    public static User getUser(String email) {
        return users.get(email);
    }

    public static boolean emailExists(String email) {
        return users.containsKey(email);
    }
}