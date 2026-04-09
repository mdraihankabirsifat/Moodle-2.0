package com.example.moodle.controller;

import com.example.moodle.model.UniversityInfo;
import com.example.moodle.util.SceneManager;
import com.example.moodle.util.Session;
import com.example.moodle.util.UniversityDatabase;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;

public class UniversityPageController {

    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        showAbout();
    }

    private void setScrollContent(VBox content) {
        javafx.scene.control.ScrollPane scroll = new javafx.scene.control.ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        contentArea.getChildren().setAll(scroll);
    }

    @FXML
    private void showAbout() {
        String uniName = Session.getSelectedUniversity();
        if (uniName == null) return;

        UniversityInfo info = UniversityDatabase.getUniversity(uniName);
        if (info == null) return;

        VBox box = new VBox(20);
        box.setPadding(new javafx.geometry.Insets(10));

        Label title = new Label(info.getName() + " (" + info.getShortName() + ")");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        VBox contactBox = new VBox(12);
        contactBox.setStyle("-fx-padding: 20; -fx-background-color: #111a2e; -fx-background-radius: 10; -fx-border-color: rgba(0,229,255,0.2); -fx-border-radius: 10;");
        Label cTitle = new Label("📞  Contact Information");
        cTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #00ff88;");
        contactBox.getChildren().addAll(cTitle, 
            new Label("Phone: " + info.getPhone()), 
            new Label("Email: " + info.getEmail()), 
            new Label("Website: " + info.getWebsite()));

        VBox locationBox = new VBox(12);
        locationBox.setStyle("-fx-padding: 20; -fx-background-color: #111a2e; -fx-background-radius: 10; -fx-border-color: rgba(0,229,255,0.2); -fx-border-radius: 10;");
        Label lTitle = new Label("📍  Location");
        lTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #00ff88;");
        Label locBody = new Label(info.getLocation());
        locBody.setWrapText(true);
        locationBox.getChildren().addAll(lTitle, locBody);

        VBox noticesBox = new VBox(12);
        noticesBox.setStyle("-fx-padding: 20; -fx-background-color: #111a2e; -fx-background-radius: 10; -fx-border-color: #ffb300; -fx-border-radius: 10;");
        Label nTitle = new Label("📢  Global Notices");
        nTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffb300;");
        noticesBox.getChildren().add(nTitle);
        for (int i = 0; i < info.getNotices().size(); i++) {
            Label notice = new Label((i + 1) + ".  " + info.getNotices().get(i));
            notice.setWrapText(true);
            notice.setStyle("-fx-padding: 8 12; -fx-background-color: #0d1b2a; -fx-background-radius: 8;");
            noticesBox.getChildren().add(notice);
        }

        box.getChildren().addAll(title, contactBox, locationBox, noticesBox);
        setScrollContent(box);
    }

    @FXML
    private void showDepartments() {
        VBox box = new VBox(15);
        box.setPadding(new javafx.geometry.Insets(10));
        Label title = new Label("🎓 Departments");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");
        Label info = new Label("Department information will be available here.");
        box.getChildren().addAll(title, info);
        setScrollContent(box);
    }

    @FXML
    private void showFaculty() {
        VBox box = new VBox(15);
        box.setPadding(new javafx.geometry.Insets(10));
        Label title = new Label("👨‍🏫 Faculty");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");
        Label info = new Label("Faculty member details will be available here.");
        box.getChildren().addAll(title, info);
        setScrollContent(box);
    }

    @FXML
    private void showCampusLife() {
        VBox box = new VBox(15);
        box.setPadding(new javafx.geometry.Insets(10));
        Label title = new Label("🏫 Campus Life");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");
        Label info = new Label("Halls, Clubs, and Facilities.");
        box.getChildren().addAll(title, info);
        setScrollContent(box);
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
