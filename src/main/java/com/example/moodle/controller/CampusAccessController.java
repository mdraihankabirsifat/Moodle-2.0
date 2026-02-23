package com.example.moodle.controller;

import com.example.moodle.util.SceneManager;
import com.example.moodle.util.Session;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class CampusAccessController {

    @FXML
    private Label universityLabel;

    @FXML
    private TextField campusIdField;

    @FXML
    private PasswordField campusPassField;

    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        universityLabel.setText(Session.getUniversity());
    }

    @FXML
    private void verifyCampus() {

        String enteredId = campusIdField.getText().trim();
        String enteredPass = campusPassField.getText().trim();

        // Accept any ID, password must be 1234
        if (!enteredId.isEmpty() && enteredPass.equals("1234")) {

            SceneManager.switchScene("campus-dashboard.fxml");

        } else {
            messageLabel.setText("Invalid Campus Credentials.");
        }
    }
}
