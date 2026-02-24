package com.example.moodle.controller;

import com.example.moodle.model.UniversityInfo;
import com.example.moodle.util.SceneManager;
import com.example.moodle.util.Session;
import com.example.moodle.util.UniversityDatabase;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class UniversityPageController {

    @FXML private Label uniNameLabel;
    @FXML private VBox noticesBox;
    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;
    @FXML private Label websiteLabel;
    @FXML private Label locationLabel;

    @FXML
    public void initialize() {
        String uniName = Session.getSelectedUniversity();
        if (uniName == null) return;

        UniversityInfo info = UniversityDatabase.getUniversity(uniName);
        if (info == null) {
            uniNameLabel.setText("University not found: " + uniName);
            return;
        }

        uniNameLabel.setText(info.getName() + " (" + info.getShortName() + ")");
        phoneLabel.setText("Phone: " + info.getPhone());
        emailLabel.setText("Email: " + info.getEmail());
        websiteLabel.setText("Website: " + info.getWebsite());
        locationLabel.setText(info.getLocation());

        for (int i = 0; i < info.getNotices().size(); i++) {
            Label notice = new Label((i + 1) + ".  " + info.getNotices().get(i));
            notice.setWrapText(true);
            notice.setStyle("-fx-font-size: 14px; -fx-padding: 8 12 8 12; " +
                    "-fx-background-color: #f0f4ff; -fx-background-radius: 8;");
            noticesBox.getChildren().add(notice);
        }
    }

    @FXML
    private void goHome() {
        SceneManager.switchScene("home.fxml");
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
    private void goBack() {
        SceneManager.goBack();
    }
}
