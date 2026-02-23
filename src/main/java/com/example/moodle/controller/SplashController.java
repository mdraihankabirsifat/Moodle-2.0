package com.example.moodle.controller;

import com.example.moodle.util.SceneManager;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class SplashController {

    @FXML
    private StackPane rootPane;

    @FXML
    private Label titleLabel;

    @FXML
    public void initialize() {

        // Fade in title
        FadeTransition fade = new FadeTransition(Duration.seconds(2), titleLabel);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        // Animated gradient
        Timeline gradientAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(rootPane.styleProperty(),
                                "-fx-background-color: linear-gradient(to bottom right, #141e30, #243b55);")),
                new KeyFrame(Duration.seconds(4),
                        new KeyValue(rootPane.styleProperty(),
                                "-fx-background-color: linear-gradient(to bottom right, #1e3c72, #2a5298);"))
        );

        gradientAnimation.setAutoReverse(true);
        gradientAnimation.setCycleCount(Animation.INDEFINITE);
        gradientAnimation.play();

        // Auto redirect
        PauseTransition delay = new PauseTransition(Duration.seconds(4));
        delay.setOnFinished(e -> SceneManager.switchScene("home.fxml"));
        delay.play();
    }
}