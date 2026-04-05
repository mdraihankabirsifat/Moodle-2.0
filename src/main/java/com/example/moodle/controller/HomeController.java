package com.example.moodle.controller;

import java.util.List;

import com.example.moodle.service.MessageNetworkBridge;
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
    @FXML private VBox networkPanel;
    @FXML private TextField serverAddressField;
    @FXML private Label networkStatusLabel;
    @FXML private Label localServerLabel;
    @FXML private Button connectServerButton;
    @FXML private Button disconnectServerButton;

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

        MessageNetworkBridge.startServer();
        setNetworkPanelVisible(false);
        refreshNetworkPanel();
    }

    private void setupHeroAnimation() {
        if (heroBox == null) {
            return;
        }

        String styleA = "-fx-background-color: linear-gradient(to right, rgba(0,229,255,0.05), rgba(13,27,42,0.92), rgba(0,229,255,0.05));"
                + "-fx-background-radius: 12; -fx-border-color: rgba(0,229,255,0.3); -fx-border-radius: 12;"
                + "-fx-padding: 30; -fx-max-width: 860;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,229,255,0.15), 16, 0.08, 0, 0);";

        String styleB = "-fx-background-color: linear-gradient(to right, rgba(0,136,204,0.04), rgba(10,22,40,0.94), rgba(191,64,255,0.04));"
                + "-fx-background-radius: 12; -fx-border-color: rgba(0,229,255,0.2); -fx-border-radius: 12;"
                + "-fx-padding: 30; -fx-max-width: 860;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,229,255,0.1), 16, 0.08, 0, 0);";

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

    private void setNetworkPanelVisible(boolean visible) {
        if (networkPanel == null) {
            return;
        }
        networkPanel.setVisible(visible);
        networkPanel.setManaged(visible);
    }

    @FXML
    private void showNetworkPanel() {
        setNetworkPanelVisible(true);
        refreshNetworkPanel();
    }

    private void refreshNetworkPanel() {
        if (localServerLabel != null) {
            localServerLabel.setText("Local IP: " + MessageNetworkBridge.getLocalAddressHint());
        }

        if (networkStatusLabel != null) {
            if (!MessageNetworkBridge.isServerRunning()) {
                networkStatusLabel.setStyle("-fx-text-fill: #ff3366; -fx-font-weight: bold;");
                networkStatusLabel.setText(MessageNetworkBridge.getServerStatus());
            } else if (MessageNetworkBridge.isConnected()) {
                networkStatusLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold;");
                networkStatusLabel.setText(MessageNetworkBridge.getConnectionStatus());
            } else {
                networkStatusLabel.setStyle("-fx-text-fill: #ffb300; -fx-font-weight: bold;");
                networkStatusLabel.setText(MessageNetworkBridge.getConnectionStatus());
            }
        }

        if (connectServerButton != null) {
            connectServerButton.setDisable(!MessageNetworkBridge.isServerRunning());
        }

        if (disconnectServerButton != null) {
            disconnectServerButton.setDisable(!MessageNetworkBridge.isConnected());
        }
    }

    @FXML
    private void connectToServer() {
        if (serverAddressField == null) {
            return;
        }
        MessageNetworkBridge.connectToPeer(serverAddressField.getText());
        refreshNetworkPanel();
    }

    @FXML
    private void disconnectFromServer() {
        MessageNetworkBridge.disconnectPeer();
        refreshNetworkPanel();
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

    @FXML
    private void openBuetVlog() {
        openVlogPage("buet-icpc");
    }

    @FXML
    private void openDhakaMedicalVlog() {
        openVlogPage("dhaka-medical-beds");
    }

    @FXML
    private void openFutureVlogOne() {
        openVlogPage("future-vlog-1");
    }

    @FXML
    private void openFutureVlogTwo() {
        openVlogPage("future-vlog-2");
    }

    private void openVlogPage(String vlogId) {
        Session.setSelectedNewsVlog(vlogId);
        SceneManager.switchScene("news-vlog.fxml");
    }

    @FXML
    private void showAbout() {
        javafx.stage.Stage aboutStage = new javafx.stage.Stage();
        aboutStage.setTitle("About — Moodle 2.0");

        VBox root = new VBox(20);
        root.setAlignment(javafx.geometry.Pos.CENTER);
        root.setStyle("-fx-background-color: #0d1b2a; -fx-padding: 40;");

        Label title = new Label("MOODLE 2.0");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: 900; -fx-text-fill: #00e5ff; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,229,255,0.4), 12, 0.3, 0, 0);");

        Label subtitle = new Label("Varsity Simulator");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #5a6a7e; -fx-font-weight: 600;");

        Label goal = new Label("A student-friendly platform where academic tools,\ncampus services, and communication stay connected.");
        goal.setWrapText(true);
        goal.setStyle("-fx-text-fill: #d0d8e8; -fx-font-size: 13px; -fx-text-alignment: center;");
        goal.setMaxWidth(400);

        javafx.scene.control.Separator sep1 = new javafx.scene.control.Separator();
        sep1.setStyle("-fx-border-color: rgba(0,229,255,0.2);");

        // Developers section
        Label devTitle = new Label("⚡ DEVELOPERS");
        devTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: 800; -fx-text-fill: #ffb300; -fx-letter-spacing: 2;");

        VBox dev1 = createPersonCard("Md. Raihan Kabir Sifat", "CSE, BUET");
        VBox dev2 = createPersonCard("Khandokar Naeemul Haque", "CSE, BUET");

        javafx.scene.control.Separator sep2 = new javafx.scene.control.Separator();
        sep2.setStyle("-fx-border-color: rgba(0,229,255,0.2);");

        // Advisor section
        Label advTitle = new Label("🎓 ADVISOR");
        advTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: 800; -fx-text-fill: #00ff88; -fx-letter-spacing: 2;");

        VBox advisor = createPersonCard("Md. Nurul Muttakin", "Lecturer, CSE, BUET");

        root.getChildren().addAll(title, subtitle, goal, sep1, devTitle, dev1, dev2, sep2, advTitle, advisor);

        javafx.scene.Scene scene = new javafx.scene.Scene(root, 480, 560);
        aboutStage.setScene(scene);
        aboutStage.setResizable(false);
        aboutStage.show();
    }

    private VBox createPersonCard(String name, String role) {
        VBox card = new VBox(4);
        card.setAlignment(javafx.geometry.Pos.CENTER);
        card.setStyle("-fx-padding: 10 20 10 20; -fx-background-color: #111a2e; "
                + "-fx-background-radius: 8; -fx-border-color: rgba(0,229,255,0.2); -fx-border-radius: 8;");
        card.setMaxWidth(320);

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");
        Label roleLabel = new Label(role);
        roleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #5a6a7e;");
        card.getChildren().addAll(nameLabel, roleLabel);
        return card;
    }
}
