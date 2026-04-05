package com.example.moodle.controller;

import com.example.moodle.service.DataStore;
import com.example.moodle.util.SceneManager;
import com.example.moodle.util.Session;
import com.example.moodle.util.UserStore;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

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
    private RadioButton studentSignupRadio;

    @FXML
    private RadioButton teacherSignupRadio;

    @FXML
    private ToggleGroup signupRoleGroup;

    @FXML
    private VBox studentSignupFields;

    @FXML
    private VBox teacherSignupFields;

    @FXML
    private TextField teacherNameField;

    @FXML
    private TextField teacherEmailField;

    @FXML
    private TextField teacherDeptField;

    @FXML
    private TextField teacherDesignationField;

    @FXML
    private ToggleGroup teacherTypeGroup;

    @FXML
    private RadioButton facultyTeacherRadio;

    @FXML
    private RadioButton guestTeacherRadio;

    @FXML
    private PasswordField teacherPasswordField;

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

        signupRoleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            boolean isStudent = (newVal == studentSignupRadio);
            studentSignupFields.setVisible(isStudent);
            studentSignupFields.setManaged(isStudent);
            teacherSignupFields.setVisible(!isStudent);
            teacherSignupFields.setManaged(!isStudent);
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
    private void handleSignup() {

        if (teacherSignupRadio.isSelected()) {
            handleTeacherSignup();
        } else {
            handleStudentSignup();
        }
    }

    private void handleStudentSignup() {

        String name = nameField.getText().trim();
        String university = universityBox.getValue();
        String id = idField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (name.isEmpty() || university == null || id.isEmpty()
                || email.isEmpty() || password.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: #ff3366;");
            messageLabel.setText("Please fill all fields.");
            return;
        }

        if (!isValidEmail(email)) {
            messageLabel.setStyle("-fx-text-fill: #ff3366;");
            messageLabel.setText("Please enter a valid email address.");
            return;
        }

        if (UserStore.emailExists(email)) {
            messageLabel.setStyle("-fx-text-fill: #ff3366;");
            messageLabel.setText("Email already registered.");
            return;
        }

        UserStore.addUser(
                new com.example.moodle.model.User(
                        name, university, id, email, password
                )
        );

        messageLabel.setStyle("-fx-text-fill: #00ff88;");
        messageLabel.setText("Account created! Please login.");

        SceneManager.switchScene("login.fxml");
    }

    private void handleTeacherSignup() {

        String name = teacherNameField.getText().trim();
        String email = teacherEmailField.getText().trim().toLowerCase();
        String dept = teacherDeptField.getText().trim();
        String designation = teacherDesignationField.getText().trim();
        String type;
        if (teacherTypeGroup.getSelectedToggle() == guestTeacherRadio) {
            type = "Guest Teacher";
        } else if (teacherTypeGroup.getSelectedToggle() == facultyTeacherRadio) {
            type = "Faculty Teacher";
        } else {
            type = "Faculty Teacher";
        }
        String password = teacherPasswordField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || dept.isEmpty()
                || designation.isEmpty() || password.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: #ff3366;");
            messageLabel.setText("Please fill all fields.");
            return;
        }

        if (!isValidEmail(email)) {
            messageLabel.setStyle("-fx-text-fill: #ff3366;");
            messageLabel.setText("Please enter a valid email address.");
            return;
        }

        if (UserStore.emailExists(email) || DataStore.getTeacherProfileByEmail(email) != null) {
            messageLabel.setStyle("-fx-text-fill: #ff3366;");
            messageLabel.setText("Email already registered.");
            return;
        }

        DataStore.saveTeacherProfile(name, dept, designation, type, password, email);

        messageLabel.setStyle("-fx-text-fill: #00ff88;");
        messageLabel.setText("Teacher account created! Please login.");

        SceneManager.switchScene("login.fxml");
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}
