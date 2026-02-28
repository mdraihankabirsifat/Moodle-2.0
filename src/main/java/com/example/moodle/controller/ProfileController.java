package com.example.moodle.controller;

import java.io.File;

import com.example.moodle.model.User;
import com.example.moodle.service.DataStore;
import com.example.moodle.util.SceneManager;
import com.example.moodle.util.Session;
import com.example.moodle.util.UserStore;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

public class ProfileController {

    @FXML private StackPane photoContainer;
    @FXML private Label photoPlaceholder;
    @FXML private Label photoMsg;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> universityBox;
    @FXML private TextField idField;
    @FXML private TextField emailField;
    @FXML private Label messageLabel;
    @FXML private PasswordField currentPassField;
    @FXML private PasswordField newPassField;
    @FXML private PasswordField currentCampusPassField;
    @FXML private PasswordField newCampusPassField;
    @FXML private Label passMessage;

    @FXML
    public void initialize() {
        universityBox.getItems().addAll(
                "BUET", "Dhaka Medical College", "BAU", "DU", "RUET"
        );

        nameField.setText(Session.getName());
        universityBox.setValue(Session.getUniversity());
        idField.setText(Session.getStudentId());
        emailField.setText(Session.getEmail());

        loadPhoto();
    }

    private void loadPhoto() {
        String photoPath = DataStore.getProfilePhoto(Session.getIdentifier());
        if (photoPath != null && !photoPath.isEmpty()) {
            try {
                Image img = new Image(photoPath, 100, 100, true, true);
                ImageView iv = new ImageView(img);
                iv.setFitWidth(100);
                iv.setFitHeight(100);
                Circle clip = new Circle(50, 50, 50);
                iv.setClip(clip);
                photoContainer.getChildren().setAll(iv);
            } catch (Exception ex) {
                // keep placeholder
            }
        }
    }

    @FXML
    private void uploadPhoto() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Profile Photo");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File file = fc.showOpenDialog(photoContainer.getScene().getWindow());
        if (file != null) {
            DataStore.setProfilePhoto(Session.getIdentifier(), file.toURI().toString());
            photoMsg.setStyle("-fx-text-fill: green;");
            photoMsg.setText("Photo updated!");
            loadPhoto();
        }
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

        String oldEmail = Session.getEmail();
        User oldUser = UserStore.getUser(oldEmail);
        if (oldUser != null) {
            UserStore.removeUser(oldEmail);
            UserStore.addUser(new User(name, university, id, email, oldUser.getPassword()));
        }

        Session.login(name, university, id, email);

        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText("Profile updated successfully!");
    }

    @FXML
    private void changeUniversalPassword() {
        String current = currentPassField.getText().trim();
        String newPass = newPassField.getText().trim();
        if (current.isEmpty() || newPass.isEmpty()) {
            passMessage.setStyle("-fx-text-fill: red;");
            passMessage.setText("Fill both password fields.");
            return;
        }
        User user = UserStore.getUser(Session.getEmail());
        if (user == null || !user.getPassword().equals(current)) {
            passMessage.setStyle("-fx-text-fill: red;");
            passMessage.setText("Current universal password is incorrect.");
            return;
        }
        user.setPassword(newPass);
        UserStore.updateUser(user);
        passMessage.setStyle("-fx-text-fill: green;");
        passMessage.setText("Universal password changed! \u2705");
        currentPassField.clear();
        newPassField.clear();
    }

    @FXML
    private void changeCampusPassword() {
        String current = currentCampusPassField.getText().trim();
        String newPass = newCampusPassField.getText().trim();
        if (current.isEmpty() || newPass.isEmpty()) {
            passMessage.setStyle("-fx-text-fill: red;");
            passMessage.setText("Fill both password fields.");
            return;
        }

        String studentId = Session.getStudentId();
        String stored = DataStore.getCampusPassword(studentId);
        String expectedCurrent = (stored != null) ? stored : "1234";

        if (!expectedCurrent.equals(current)) {
            passMessage.setStyle("-fx-text-fill: red;");
            passMessage.setText("Current campus password is incorrect.");
            return;
        }

        DataStore.setCampusPassword(studentId, newPass);
        passMessage.setStyle("-fx-text-fill: green;");
        passMessage.setText("Campus password changed! \u2705");
        currentCampusPassField.clear();
        newCampusPassField.clear();
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
