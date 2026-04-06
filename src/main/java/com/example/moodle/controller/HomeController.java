package com.example.moodle.controller;

import java.util.List;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.Map;
import com.example.moodle.service.DataStore;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.control.Tooltip;


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
    @FXML private VBox activityTrackerPanel;
    @FXML private Label activityTrackerTitle;
    @FXML private VBox activityDaysLabels;
    @FXML private HBox activityGridContainer;


    @FXML
    public void initialize() {

        // Campus button always visible for all roles
        campusButton.setVisible(true);

        if (Session.isLoggedIn()) {
            loginButton.setVisible(false); loginButton.setManaged(false);
            signupButton.setVisible(false); signupButton.setManaged(false);
            profileButton.setVisible(true); profileButton.setManaged(true);
            logoutButton.setVisible(true); logoutButton.setManaged(true);
        } else {
            loginButton.setVisible(true); loginButton.setManaged(true);
            signupButton.setVisible(true); signupButton.setManaged(true);
            profileButton.setVisible(false); profileButton.setManaged(false);
            logoutButton.setVisible(false); logoutButton.setManaged(false);
        }

        // Live search
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            searchResults.getChildren().clear();
            if (newVal.trim().isEmpty()) {
                searchResults.setVisible(false);
                searchResults.setManaged(false);
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
            searchResults.setManaged(true);
        });

        updateThemeMenuButtonText();

        setupHeroAnimation();
        setupResponsiveLayout();

        MessageNetworkBridge.startServer();
        setNetworkPanelVisible(false);
        refreshNetworkPanel();
        
        renderActivityTracker();

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
        SceneManager.switchScene("about.fxml");
    }

    private void renderActivityTracker() {
        if (!Session.isLoggedIn() || activityTrackerPanel == null) return;
        activityTrackerPanel.setVisible(true);
        activityTrackerPanel.setManaged(true);
        
        String myId = Session.getIdentifier();
        Map<LocalDate, Integer> counts = DataStore.getActivityCounts(myId);
        
        int total = 0;
        for (int c : counts.values()) total += c;
        activityTrackerTitle.setText(total + " contributions in the last year");
        
        activityDaysLabels.getChildren().clear();
        String[] days = {"Mon", "", "Wed", "", "Fri", "", ""};
        for (int i = 0; i < days.length; i++) {
            Label l = new Label(days[i]);
            l.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 10px;");
            l.setMinHeight(12);
            l.setMaxHeight(12);
            activityDaysLabels.getChildren().add(l);
        }
        
        activityGridContainer.getChildren().clear();
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(364);
        while (start.getDayOfWeek() != DayOfWeek.MONDAY) {
            start = start.minusDays(1);
        }
        
        LocalDate current = start;
        while (!current.isAfter(today)) {
            VBox weekCol = new VBox(4);
            for (int i = 0; i < 7; i++) {
                if (current.isAfter(today)) {
                    // Fill remaining empty cells so that alignment doesn't break
                    Region emptyCell = new Region();
                    emptyCell.setMinSize(12, 12);
                    emptyCell.setMaxSize(12, 12);
                    weekCol.getChildren().add(emptyCell);
                    current = current.plusDays(1);
                    continue;
                }
                
                Region cell = new Region();
                cell.setMinSize(12, 12);
                cell.setMaxSize(12, 12);
                int count = counts.getOrDefault(current, 0);
                cell.setStyle("-fx-background-radius: 2; " + getColorForCount(count));
                
                Tooltip t = new Tooltip(count + " activities on " + current);
                Tooltip.install(cell, t);
                
                weekCol.getChildren().add(cell);
                current = current.plusDays(1);
            }
            activityGridContainer.getChildren().add(weekCol);
        }
    }
    
    private String getColorForCount(int count) {
        if (count == 0) return "-fx-background-color: #161b22;";
        if (count == 1 || count == 2) return "-fx-background-color: #0e4429;";
        if (count == 3 || count == 4) return "-fx-background-color: #006d32;";
        if (count >= 5 && count <= 6) return "-fx-background-color: #26a641;";
        return "-fx-background-color: #39d353;";
    }

}
