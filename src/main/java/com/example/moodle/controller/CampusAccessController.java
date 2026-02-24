package com.example.moodle.controller;

import com.example.moodle.model.User;
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

        User user = UserStore.getUserByStudentId(id);
        if (user != null && user.getPassword().equals(pass)) {
            Session.setCampusVerified(true);
            Session.setRole("STUDENT");
            if (!Session.isLoggedIn()) {
                Session.login(user.getName(), user.getUniversity(),
                        user.getStudentId(), user.getEmail());
            }
            SceneManager.switchScene("campus-dashboard.fxml");
        } else {
            showError("Invalid Student ID or Password.");
        }
    }

    private void verifyTeacher() {
        String pass = staffPassField.getText().trim();
        if ("teacher2024".equals(pass)) {
            Session.setCampusVerified(true);
            Session.setRole("TEACHER");
            SceneManager.switchScene("teacher-dashboard.fxml");
        } else {
            showError("Invalid Teacher Password.");
        }
    }

    private void verifyAuthority() {
        String pass = staffPassField.getText().trim();
        if ("admin2024".equals(pass)) {
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
