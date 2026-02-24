package com.example.moodle.controller;

import java.util.List;

import com.example.moodle.util.SceneManager;
import com.example.moodle.util.Session;
import com.example.moodle.util.UniversityDatabase;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class HomeController {

    @FXML private Button loginButton;
    @FXML private Button campusButton;
    @FXML private MenuButton profileMenu;
    @FXML private MenuItem nameItem;
    @FXML private MenuItem universityItem;
    @FXML private MenuItem emailItem;
    @FXML private Pane floatingLayer;
    @FXML private Button signupButton;
    @FXML private TextField searchField;
    @FXML private VBox searchResults;

    @FXML
    public void initialize() {

        if (Session.isLoggedIn()) {
            loginButton.setVisible(false);
            signupButton.setVisible(false);
            campusButton.setVisible(true);
            profileMenu.setVisible(true);

            nameItem.setText("Name: " + Session.getName());
            universityItem.setText("University: " + Session.getUniversity());
            emailItem.setText("Email: " + Session.getEmail());
        } else {
            loginButton.setVisible(true);
            signupButton.setVisible(true);
            campusButton.setVisible(false);
            profileMenu.setVisible(false);
        }

        // Live search
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            searchResults.getChildren().clear();
            if (newVal.trim().isEmpty()) {
                searchResults.setVisible(false);
                return;
            }
            List<String> matches = UniversityDatabase.search(newVal.trim());
            if (matches.isEmpty()) {
                Label noResult = new Label("No university found");
                noResult.setStyle("-fx-text-fill: #888; -fx-padding: 6;");
                searchResults.getChildren().add(noResult);
            } else {
                for (String uni : matches) {
                    Button btn = new Button(uni);
                    btn.setMaxWidth(Double.MAX_VALUE);
                    btn.setStyle("-fx-background-color: white; -fx-text-fill: #1e3c72; " +
                            "-fx-font-size: 13px; -fx-cursor: hand; -fx-padding: 8 12 8 12; " +
                            "-fx-border-color: #eee; -fx-border-radius: 4; -fx-background-radius: 4;");
                    btn.setOnAction(e -> openUniversity(uni));
                    searchResults.getChildren().add(btn);
                }
            }
            searchResults.setVisible(true);
        });
    }

    private void openUniversity(String name) {
        Session.setSelectedUniversity(name);
        SceneManager.switchScene("university-page.fxml");
    }

    @FXML
    private void goHome() {
        SceneManager.switchScene("home.fxml");
    }

    @FXML
    private void goToSignup() {
        SceneManager.switchScene("signup.fxml");
    }

    @FXML
    private void goToLogin() {
        SceneManager.switchScene("login.fxml");
    }

    @FXML
    private void goToCampus() {
        if (Session.isCampusVerified()) {
            SceneManager.switchScene("campus-dashboard.fxml");
        } else {
            SceneManager.switchScene("campus-access.fxml");
        }
    }

    @FXML
    private void goToProfile() {
        SceneManager.switchScene("profile.fxml");
    }

    @FXML
    private void logout() {
        Session.logout();
        SceneManager.switchScene("home.fxml");
    }

    @FXML
    private void goBack() {
        SceneManager.goBack();
    }
}
