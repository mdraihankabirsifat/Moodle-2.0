package com.example.moodle.controller;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;
import com.example.moodle.util.SceneManager;

public class SplashController {

    @FXML
    private Label titleLabel;

    @FXML
    public void initialize() {

        FadeTransition fade = new FadeTransition(Duration.seconds(2), titleLabel);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> SceneManager.switchScene("home.fxml"));
        delay.play();
    }
}
