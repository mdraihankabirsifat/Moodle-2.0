package com.example.moodle.util;

public class Session {

    private static boolean loggedIn = false;
    private static String name;
    private static String university;
    private static String studentId;
    private static String email;

    public static void login(String n, String u, String id, String e) {
        loggedIn = true;
        name = n;
        university = u;
        studentId = id;
        email = e;
    }

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static String getName() {
        return name;
    }

    public static String getUniversity() {
        return university;
    }

    public static String getStudentId() {
        return studentId;
    }

    public static String getEmail() {
        return email;
    }

    public static void logout() {
        loggedIn = false;
    }
}