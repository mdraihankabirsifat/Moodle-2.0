package com.example.moodle.controller;

import com.example.moodle.util.SceneManager;
import javafx.fxml.FXML;

public class AboutController {
    @FXML
    private void goHome() {
        SceneManager.switchScene("home.fxml");
    }

    @FXML
    private void goBack() {
        SceneManager.goBack();
    }
}
