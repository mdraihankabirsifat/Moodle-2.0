package com.example.moodle.util;

public class Session {

    private static boolean loggedIn = false;
    private static boolean campusVerified = false;
    private static String name;
    private static String university;
    private static String studentId;
    private static String email;
    private static String selectedUniversity;
    private static String role = "STUDENT";

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

    public static void setCampusVerified(boolean v) {
        campusVerified = v;
    }

    public static boolean isCampusVerified() {
        return campusVerified;
    }

    public static void setSelectedUniversity(String uni) {
        selectedUniversity = uni;
    }

    public static String getSelectedUniversity() {
        return selectedUniversity;
    }

    public static void setRole(String r) {
        role = r;
    }

    public static String getRole() {
        return role;
    }

    public static String getCampusDashboardFxml() {
        if ("TEACHER".equals(role)) return "teacher-dashboard.fxml";
        if ("AUTHORITY".equals(role)) return "authority-dashboard.fxml";
        return "campus-dashboard.fxml";
    }

    public static String getIdentifier() {
        if (email != null && !email.isEmpty()) return email;
        if ("TEACHER".equals(role)) return "faculty@campus";
        if ("AUTHORITY".equals(role)) return "admin@campus";
        return "unknown";
    }

    public static void logout() {
        loggedIn = false;
        campusVerified = false;
        selectedUniversity = null;
        role = "STUDENT";
        name = null;
        university = null;
        studentId = null;
        email = null;
    }
}