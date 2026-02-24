package com.example.moodle.controller;

import com.example.moodle.model.User;
import com.example.moodle.util.SceneManager;
import com.example.moodle.util.Session;
import com.example.moodle.util.UserStore;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ProfileController {

    @FXML private TextField nameField;
    @FXML private ComboBox<String> universityBox;
    @FXML private TextField idField;
    @FXML private TextField emailField;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        universityBox.getItems().addAll(
                "BUET", "Dhaka Medical College", "BAU", "DU", "RUET"
        );

        // Pre-fill with session data
        nameField.setText(Session.getName());
        universityBox.setValue(Session.getUniversity());
        idField.setText(Session.getStudentId());
        emailField.setText(Session.getEmail());
    }

    @FXML
    private void saveProfile() {
        String name = nameField.getText().trim();
        String university = universityBox.getValue();
        String id = idField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || university == null || id.isEmpty() || email.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("All fields are required.");
            return;
        }

        // Update the stored user record
        String oldEmail = Session.getEmail();
        User oldUser = UserStore.getUser(oldEmail);
        if (oldUser != null) {
            UserStore.removeUser(oldEmail);
            UserStore.addUser(new User(name, university, id, email, oldUser.getPassword()));
        }

        // Update session
        Session.login(name, university, id, email);

        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText("Profile updated successfully!");
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
}
