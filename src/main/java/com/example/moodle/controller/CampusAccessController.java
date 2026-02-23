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

        String id = campusIdField.getText();
        String pass = campusPassField.getText();

        // Simple dummy validation
        if (id.equals(Session.getStudentId()) && pass.equals("campus123")) {

            SceneManager.switchScene("campus-dashboard.fxml");

        } else {
            messageLabel.setText("Invalid Campus Credentials.");
        }
    }
}