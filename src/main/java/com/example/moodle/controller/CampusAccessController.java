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
    @FXML private TextField staffEmailField;
    @FXML private PasswordField staffPassField;
    @FXML private Label messageLabel;
    @FXML private Label hintLabel;
    @FXML private Label initialPassLabel;

    @FXML
    public void initialize() {
        studentRadio.setSelected(true);

        // Keep button click and Enter key submit available.
        campusIdField.setOnAction(e -> verifyCampus());
        campusPassField.setOnAction(e -> verifyCampus());
        staffEmailField.setOnAction(e -> verifyCampus());
        staffPassField.setOnAction(e -> verifyCampus());

        roleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            boolean isStudent = (newVal == studentRadio);
            boolean isTeacher = (newVal == teacherRadio);
            studentFields.setVisible(isStudent);
            studentFields.setManaged(isStudent);
            initialPassLabel.setVisible(isStudent);
            initialPassLabel.setManaged(isStudent);
            staffFields.setVisible(!isStudent);
            staffFields.setManaged(!isStudent);
            staffEmailField.setVisible(isTeacher);
            staffEmailField.setManaged(isTeacher);
            if (!isTeacher) {
                staffEmailField.clear();
            }
            messageLabel.setText("");
            if (newVal == teacherRadio) {
                hintLabel.setText(" Use your teacher email. Default campus pass: teacher123");
            } else if (newVal == authorityRadio) {
                // admin pass- "admin123". Multiple admins from multiple universities.
                // They control their university individually.
                hintLabel.setText("Hint: admin123");
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
        String email = staffEmailField.getText().trim().toLowerCase();
        String pass = staffPassField.getText().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            showError("Enter teacher email and campus password.");
            return;
        }

        String[] profile = DataStore.getTeacherProfileByEmail(email);
        if (profile == null) {
            showError("Teacher account not found. Please sign up first.");
            return;
        }

        // Campus access password is separate from signup/login password.
        // Default campus password is "teacher123", provided by authority.
        // Teachers can change it from their MyProfile section in the Teacher Dashboard.
        String campusPass = DataStore.getCampusPassword(email);
        String expectedCampusPass = (campusPass != null) ? campusPass : "teacher123";

        if (!expectedCampusPass.equals(pass)) {
            showError("Invalid campus password.");
            return;
        }

        String name = profile[0];
        String dept = profile[1];
        String designation = profile[2];
        String type = profile[3];
        String teacherEmail = (profile.length >= 6 && profile[5] != null && !profile[5].trim().isEmpty())
                ? profile[5].trim().toLowerCase()
                : email;

        Session.login(name, dept, "", teacherEmail);
        Session.setCampusVerified(true);
        Session.setRole("TEACHER");
        Session.setDepartment(dept);
        Session.setDesignation(designation);
        Session.setTeacherType(type);
        SceneManager.switchScene("teacher-dashboard.fxml");
    }

    private void verifyAuthority() {
        // admin pass- "admin123"
        // Multiple admins from multiple universities control their university individually.
        // They have access to edit the University_page.
        String pass = staffPassField.getText().trim();
        if ("admin123".equals(pass)) {
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
