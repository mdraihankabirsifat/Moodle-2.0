package com.example.moodle.controller;

import com.example.moodle.model.User;
import com.example.moodle.util.SceneManager;
import com.example.moodle.util.Session;
import com.example.moodle.util.UserStore;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    private void goHome() {
        SceneManager.switchScene("home.fxml");
    }

    @FXML
    private void goToCampus() {
        SceneManager.switchScene("campus-access.fxml");
    }

    @FXML
    private void goBack() {
        SceneManager.goBack();
    }

    @FXML
    private void handleLogin() {

        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        User user = UserStore.getUser(email);

        if (user == null || !user.getPassword().equals(password)) {

            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Invalid email or password.");
            return;
        }

        // Session created ONLY after login
        Session.login(
                user.getName(),
                user.getUniversity(),
                user.getStudentId(),
                user.getEmail()
        );

        SceneManager.switchScene("home.fxml");
    }
}