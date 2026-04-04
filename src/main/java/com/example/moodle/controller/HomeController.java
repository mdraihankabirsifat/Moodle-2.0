package com.example.moodle.controller;

import java.util.List;

import com.example.moodle.util.SceneManager;
import com.example.moodle.util.Session;
import com.example.moodle.util.ThemeManager;
import com.example.moodle.util.UniversityDatabase;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class HomeController {

    @FXML private Button loginButton;
    @FXML private Button campusButton;
    @FXML private Button profileButton;
    @FXML private Button logoutButton;
    @FXML private Button signupButton;
    @FXML private TextField searchField;
    @FXML private VBox searchResults;
    @FXML private MenuButton themeMenuButton;
    @FXML private VBox homeContentBox;
    @FXML private VBox heroBox;

    private Timeline heroGradientTimeline;

    @FXML
    public void initialize() {

        // Campus button always visible for all roles
        campusButton.setVisible(true);

        if (Session.isLoggedIn()) {
            loginButton.setVisible(false);
            signupButton.setVisible(false);
            profileButton.setVisible(true);
            logoutButton.setVisible(true);
        } else {
            loginButton.setVisible(true);
            signupButton.setVisible(true);
            profileButton.setVisible(false);
            logoutButton.setVisible(false);
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
                    btn.getStyleClass().add("search-result-btn");
                    btn.setOnAction(e -> openUniversity(uni));
                    searchResults.getChildren().add(btn);
                }
            }
            searchResults.setVisible(true);
        });

        updateThemeMenuButtonText();

        setupHeroAnimation();
        setupResponsiveLayout();
    }

    private void setupHeroAnimation() {
        if (heroBox == null) {
            return;
        }

        String styleA = "-fx-background-color: linear-gradient(to right, rgba(255,255,255,0.82), rgba(224,246,255,0.84), rgba(255,233,244,0.82));"
                + "-fx-background-radius: 24; -fx-border-color: rgba(255,255,255,0.55); -fx-border-radius: 24;"
                + "-fx-padding: 26; -fx-max-width: 860;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.16), 20, 0.08, 0, 6);";

        String styleB = "-fx-background-color: linear-gradient(to right, rgba(255,255,255,0.8), rgba(237,241,255,0.86), rgba(223,251,246,0.84));"
                + "-fx-background-radius: 24; -fx-border-color: rgba(255,255,255,0.55); -fx-border-radius: 24;"
                + "-fx-padding: 26; -fx-max-width: 860;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.16), 20, 0.08, 0, 6);";

        heroGradientTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(heroBox.styleProperty(), styleA)),
                new KeyFrame(Duration.seconds(8), new KeyValue(heroBox.styleProperty(), styleB))
        );
        heroGradientTimeline.setAutoReverse(true);
        heroGradientTimeline.setCycleCount(Animation.INDEFINITE);
        heroGradientTimeline.play();
    }

    private void setupResponsiveLayout() {
        searchField.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                applyResponsiveLayout(newScene.getWidth());
                newScene.widthProperty().addListener((o, oldW, newW) -> applyResponsiveLayout(newW.doubleValue()));
            }
        });
    }

    private void applyResponsiveLayout(double width) {
        if (homeContentBox == null || heroBox == null) {
            return;
        }

        if (width < 760) {
            homeContentBox.setSpacing(30);
            heroBox.setMaxWidth(560);
            themeMenuButton.setPrefWidth(96);
        } else if (width < 980) {
            homeContentBox.setSpacing(42);
            heroBox.setMaxWidth(680);
            themeMenuButton.setPrefWidth(108);
        } else {
            homeContentBox.setSpacing(60);
            heroBox.setMaxWidth(860);
            themeMenuButton.setPrefWidth(120);
        }
    }

    private void updateThemeMenuButtonText() {
        if (themeMenuButton != null) {
            themeMenuButton.setText("Mode");
        }
    }

    @FXML
    private void setDarkMode() {
        ThemeManager.setThemeByLabel("Dark");
        updateThemeMenuButtonText();
    }

    @FXML
    private void setLightMode() {
        ThemeManager.setThemeByLabel("Light");
        updateThemeMenuButtonText();
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
            SceneManager.switchScene(Session.getCampusDashboardFxml());
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
