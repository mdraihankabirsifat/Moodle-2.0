package com.example.moodle.controller;

import com.example.moodle.util.SceneManager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CampusDashboardController {

    @FXML
    private StackPane contentArea;

    @FXML
    private void goHome() {
        SceneManager.switchScene("home.fxml");
    }

    @FXML
    private void goToCampus() {
        SceneManager.switchScene("campus-access.fxml");
    }

    @FXML
    private void goBack() {
        SceneManager.goBack();
    }

    private void setContent(String title, String description) {

        VBox box = new VBox(15);
        box.getChildren().addAll(
                new Label(title),
                new Label(description)
        );

        box.setStyle("-fx-font-size: 16px;");
        contentArea.getChildren().setAll(box);
    }

    @FXML
    private void showProjects() {
        setContent("Project Submission",
                "Submit and manage your academic projects here.");
    }

    @FXML
    private void showHall() {
        setContent("Hall Management",
                "Manage room allocation and hall activities.");
    }

    @FXML
    private void showSchedule() {
        setContent("Class Schedule",
                "View your semester class schedule.");
    }

    @FXML
    private void showNotices() {
        setContent("Internal Notices",
                "See university internal announcements.");
    }

    @FXML
    private void showActivities() {
        setContent("Activities & Games",
                "Participate in campus competitions and activities.");
    }
}