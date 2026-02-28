package com.example.moodle.util;

import java.util.HashMap;
import java.util.Map;

import javafx.stage.Stage;

/**
 * Per-window session: each Stage gets its own independent login state.
 * This allows running two windows simultaneously with different users.
 */
public class Session {

    private static final Map<Stage, SessionData> sessions = new HashMap<>();

    private static class SessionData {
        boolean loggedIn = false;
        boolean campusVerified = false;
        String name;
        String university;
        String studentId;
        String email;
        String selectedUniversity;
        String role = "STUDENT";
        String department;
        String designation;
        String teacherType; // "Faculty Teacher" or "Guest Teacher"
    }

    private static SessionData current() {
        Stage stage = SceneManager.getActiveStage();
        if (stage == null) return new SessionData();
        return sessions.computeIfAbsent(stage, k -> new SessionData());
    }

    public static void login(String n, String u, String id, String e) {
        SessionData s = current();
        s.loggedIn = true;
        s.name = n;
        s.university = u;
        s.studentId = id;
        s.email = e;
    }

    public static boolean isLoggedIn() { return current().loggedIn; }
    public static String getName() { return current().name; }
    public static String getUniversity() { return current().university; }
    public static String getStudentId() { return current().studentId; }
    public static String getEmail() { return current().email; }

    public static void setCampusVerified(boolean v) { current().campusVerified = v; }
    public static boolean isCampusVerified() { return current().campusVerified; }

    public static void setSelectedUniversity(String uni) { current().selectedUniversity = uni; }
    public static String getSelectedUniversity() { return current().selectedUniversity; }

    public static void setRole(String r) { current().role = r; }
    public static String getRole() { return current().role; }

    public static void setDepartment(String d) { current().department = d; }
    public static String getDepartment() { return current().department; }
    public static void setDesignation(String d) { current().designation = d; }
    public static String getDesignation() { return current().designation; }
    public static void setTeacherType(String t) { current().teacherType = t; }
    public static String getTeacherType() { return current().teacherType; }

    public static String getCampusDashboardFxml() {
        String r = current().role;
        if ("TEACHER".equals(r)) return "teacher-dashboard.fxml";
        if ("AUTHORITY".equals(r)) return "authority-dashboard.fxml";
        return "campus-dashboard.fxml";
    }

    public static String getIdentifier() {
        SessionData s = current();
        if (s.email != null && !s.email.isEmpty()) return s.email;
        if ("TEACHER".equals(s.role)) return "faculty@campus";
        if ("AUTHORITY".equals(s.role)) return "admin@campus";
        return "unknown";
    }

    public static void logout() {
        SessionData s = current();
        s.loggedIn = false;
        s.campusVerified = false;
        s.selectedUniversity = null;
        s.role = "STUDENT";
        s.name = null;
        s.university = null;
        s.studentId = null;
        s.email = null;
        s.department = null;
        s.designation = null;
        s.teacherType = null;
    }

    /** Called when a Stage is closed to free memory */
    public static void removeStage(Stage stage) {
        sessions.remove(stage);
    }
}