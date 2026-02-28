package com.example.moodle.controller;

import com.example.moodle.model.User;
import com.example.moodle.service.DataStore;
import com.example.moodle.util.SceneManager;
import com.example.moodle.util.Session;
import com.example.moodle.util.UserStore;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class CampusAccessController {

    @FXML private RadioButton studentRadio;
    @FXML private RadioButton teacherRadio;
    @FXML private RadioButton authorityRadio;
    @FXML private ToggleGroup roleGroup;
    @FXML private VBox studentFields;
    @FXML private VBox staffFields;
    @FXML private TextField campusIdField;
    @FXML private PasswordField campusPassField;
    @FXML private PasswordField staffPassField;
    @FXML private Label messageLabel;
    @FXML private Label hintLabel;

    @FXML
    public void initialize() {
        studentRadio.setSelected(true);

        roleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            boolean isStudent = (newVal == studentRadio);
            studentFields.setVisible(isStudent);
            studentFields.setManaged(isStudent);
            staffFields.setVisible(!isStudent);
            staffFields.setManaged(!isStudent);
            messageLabel.setText("");
            if (newVal == teacherRadio) {
                hintLabel.setText("Default password: teacher2026");
            } else if (newVal == authorityRadio) {
                hintLabel.setText("Hint: admin2024");
            }
        });
    }

    @FXML
    private void goHome() {
        SceneManager.switchScene("home.fxml");
    }

    @FXML
    private void goToCampus() {
        if (Session.isCampusVerified()) {
            SceneManager.switchScene(Session.getCampusDashboardFxml());
        } else {
            SceneManager.switchScene("campus-access.fxml");
        }
    }

    @FXML
    private void goBack() {
        SceneManager.goBack();
    }

    @FXML
    private void verifyCampus() {
        if (studentRadio.isSelected()) {
            verifyStudent();
        } else if (teacherRadio.isSelected()) {
            verifyTeacher();
        } else if (authorityRadio.isSelected()) {
            verifyAuthority();
        }
    }

    private void verifyStudent() {
        String id = campusIdField.getText().trim();
        String pass = campusPassField.getText().trim();

        if (id.isEmpty() || pass.isEmpty()) {
            showError("Enter Student ID and Password.");
            return;
        }

        // Check campus-specific password: custom > default "1234"
        String customPass = DataStore.getCampusPassword(id);
        String expectedPass = (customPass != null) ? customPass : "1234";

        if (!expectedPass.equals(pass)) {
            showError("Invalid Student ID or Password.");
            return;
        }

        User user = UserStore.getUserByStudentId(id);
        Session.setCampusVerified(true);
        Session.setRole("STUDENT");
        if (user != null) {
            Session.login(user.getName(), user.getUniversity(),
                    user.getStudentId(), user.getEmail());
        } else {
            Session.login("Student " + id, "Campus", id, id + "@campus");
        }
        SceneManager.switchScene("campus-dashboard.fxml");
    }

    private void verifyTeacher() {
        String pass = staffPassField.getText().trim();

        // Teacher must have signed in via Portal Login first
        String name = Session.getName();
        String dept = Session.getDepartment();

        if (name == null || name.isEmpty() || dept == null || dept.isEmpty()
                || !"TEACHER".equals(Session.getRole())) {
            showError("Please sign in as Teacher via Portal Login first.");
            return;
        }

        // Verify password against stored profile or default
        String[] profile = DataStore.getTeacherProfile(name, dept);
        if (profile != null) {
            if (!profile[4].equals(pass)) {
                showError("Invalid password.");
                return;
            }
        } else {
            if (!"teacher2026".equals(pass)) {
                showError("Invalid password.");
                return;
            }
        }

        String teacherId = name.toLowerCase().replace(" ", ".") + "@"
                + dept.toLowerCase().replace(" ", ".") + ".campus";
        Session.login(name, dept, "", teacherId);
        Session.setCampusVerified(true);
        Session.setRole("TEACHER");
        Session.setDepartment(dept);
        Session.setDesignation(Session.getDesignation());
        Session.setTeacherType(Session.getTeacherType());
        SceneManager.switchScene("teacher-dashboard.fxml");
    }

    private void verifyAuthority() {
        String pass = staffPassField.getText().trim();
        if ("admin2024".equals(pass)) {
            Session.login("Admin", "Campus", "", "admin@campus");
            Session.setCampusVerified(true);
            Session.setRole("AUTHORITY");
            SceneManager.switchScene("authority-dashboard.fxml");
        } else {
            showError("Invalid Authority Password.");
        }
    }

    private void showError(String msg) {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText(msg);
    }
}
