package com.example.moodle.controller;

import com.example.moodle.util.SceneManager;
import com.example.moodle.util.UserStore;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignupController {

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<String> universityBox;

    @FXML
    private TextField idField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {

        // Populate university list
        universityBox.getItems().addAll(
                "BUET",
                "Dhaka Medical College",
                "BAU",
                "DU",
                "RUET"
        );
    }

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
    private void handleSignup() {

        String name = nameField.getText().trim();
        String university = universityBox.getValue();
        String id = idField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (name.isEmpty() || university == null || id.isEmpty()
                || email.isEmpty() || password.isEmpty()) {

            messageLabel.setText("Please fill all fields.");
            return;
        }

        if (UserStore.emailExists(email)) {
            messageLabel.setText("Email already registered.");
            return;
        }

        UserStore.addUser(
                new com.example.moodle.model.User(
                        name, university, id, email, password
                )
        );

        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText("Account created! Please login.");

        SceneManager.switchScene("login.fxml");
    }
}
