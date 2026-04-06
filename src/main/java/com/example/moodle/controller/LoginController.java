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

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    // Role selection
    @FXML private RadioButton studentLoginRadio;
    @FXML private RadioButton teacherLoginRadio;
    @FXML private ToggleGroup loginRoleGroup;
    @FXML private VBox studentLoginFields;
    @FXML private VBox teacherLoginFields;

    // Teacher fields
    @FXML private TextField teacherEmailField;
    @FXML private PasswordField teacherPasswordField;

    @FXML
    public void initialize() {
        loginRoleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            boolean isStudent = (newVal == studentLoginRadio);
            studentLoginFields.setVisible(isStudent);
            studentLoginFields.setManaged(isStudent);
            teacherLoginFields.setVisible(!isStudent);
            teacherLoginFields.setManaged(!isStudent);
            messageLabel.setText("");
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
    private void handleLogin() {
        if (teacherLoginRadio.isSelected()) {
            handleTeacherLogin();
        } else {
            handleStudentLogin();
        }
    }

    private void handleStudentLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        User user = UserStore.getUser(email);

        if (user == null || !user.getPassword().equals(password)) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Invalid email or password.");
            return;
        }

        Session.login(
                user.getName(),
                user.getUniversity(),
                user.getStudentId(),
                user.getEmail()
        );

        SceneManager.switchScene("home.fxml");
    }

    private void handleTeacherLogin() {
        String email = teacherEmailField.getText().trim();
        String pass = teacherPasswordField.getText().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Enter teacher email and password.");
            return;
        }

        String[] profile = DataStore.getTeacherProfileByEmail(email);
        if (profile == null) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Teacher account not found. Please sign up first.");
            return;
        }

        if (!profile[4].equals(pass)) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Invalid password.");
            return;
        }

        String name = profile[0];
        String dept = profile[1];
        String designation = profile[2];
        String type = profile[3];

        String normalizedName = name.toLowerCase().replace(" ", ".");
        String normalizedDept = dept == null ? "" : dept.trim().toLowerCase().replace(" ", ".");
        String teacherId = normalizedDept.isEmpty()
                ? normalizedName + "@campus"
                : normalizedName + "@" + normalizedDept + ".campus";

        // Let's use the actual email if present, else fallback
        String activeId = profile.length >= 6 ? profile[5] : teacherId;

        Session.login(name, dept, "", activeId);
        Session.setRole("TEACHER");
        Session.setDepartment(dept);
        Session.setDesignation(designation);
        Session.setTeacherType(type);

        SceneManager.switchScene("home.fxml");
    }
}