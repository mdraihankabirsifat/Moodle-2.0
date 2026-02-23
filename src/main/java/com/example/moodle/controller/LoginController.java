package com.example.moodle.controller;

import com.example.moodle.util.SceneManager;
import com.example.moodle.util.Session;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

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
    private void handleLogin() {

        String name = nameField.getText().trim();
        String university = universityBox.getValue();
        String id = idField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // Basic validation
        if (name.isEmpty() || university == null || id.isEmpty()
                || email.isEmpty() || password.isEmpty()) {

            messageLabel.setText("Please fill all fields.");
            return;
        }

        // Simple dummy validation (for simulation)
        if (password.equals("1234")) {

            // Store session
            Session.login(name, university, id, email);

            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Login successful!");

            // Redirect back to Home
            SceneManager.switchScene("home.fxml");

        } else {

            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Invalid password. Try 1234");
        }
    }
}
