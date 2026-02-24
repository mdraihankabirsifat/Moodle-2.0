package com.example.moodle.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.moodle.model.User;
import com.example.moodle.service.FileStore;

public class UserStore {

    private static final String FILE = "users.txt";
    // Format: email|name|university|studentId|password|role

    private static Map<String, User> cache = null;

    private static void loadCache() {
        if (cache != null) return;
        cache = new HashMap<>();
        for (String line : FileStore.loadLines(FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 6) {
                User u = new User(p[1], p[2], p[3], p[0], p[4]);
                u.setRole(p[5]);
                cache.put(p[0], u);
            }
        }
    }

    private static void saveAll() {
        List<String> lines = new ArrayList<>();
        for (User u : cache.values()) {
            lines.add(u.getEmail() + "|" + u.getName() + "|" + u.getUniversity() + "|"
                    + u.getStudentId() + "|" + u.getPassword() + "|" + u.getRole());
        }
        FileStore.saveLines(FILE, lines);
    }

    public static void addUser(User user) {
        loadCache();
        if (user.getRole() == null) user.setRole("STUDENT");
        cache.put(user.getEmail(), user);
        saveAll();
    }

    public static User getUser(String email) {
        loadCache();
        return cache.get(email);
    }

    public static User getUserByStudentId(String studentId) {
        loadCache();
        for (User u : cache.values()) {
            if (studentId.equals(u.getStudentId())) return u;
        }
        return null;
    }

    public static boolean emailExists(String email) {
        loadCache();
        return cache.containsKey(email);
    }

    public static void removeUser(String email) {
        loadCache();
        cache.remove(email);
        saveAll();
    }

    public static void updateUser(User user) {
        loadCache();
        cache.put(user.getEmail(), user);
        saveAll();
    }

    public static List<User> getAllUsers() {
        loadCache();
        return new ArrayList<>(cache.values());
    }

    public static void invalidateCache() {
        cache = null;
    }
}