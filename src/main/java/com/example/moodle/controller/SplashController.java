package com.example.moodle.controller;

import java.util.Random;

import com.example.moodle.util.SceneManager;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class SplashController {

    @FXML
    private StackPane rootPane;

    @FXML
    private Label titleLabel;

    @FXML
    private Label subtitleLabel;

    @FXML
    public void initialize() {

        // Add floating particles
        addSplashParticles();

        // Typewriter effect for title
        String fullTitle = "MOODLE 2.0";
        titleLabel.setText("");
        titleLabel.setOpacity(1);
        subtitleLabel.setOpacity(0);

        // Glow effect on title
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#4fc3f7"));
        glow.setRadius(0);
        titleLabel.setEffect(glow);

        // Typewriter timeline
        Timeline typewriter = new Timeline();
        for (int i = 0; i < fullTitle.length(); i++) {
            final int idx = i + 1;
            typewriter.getKeyFrames().add(
                    new KeyFrame(Duration.millis(180 * idx), e ->
                            titleLabel.setText(fullTitle.substring(0, idx))
                    )
            );
        }

        // After typewriter: scale pulse + glow + subtitle
        typewriter.setOnFinished(e -> {
            ScaleTransition pulse = new ScaleTransition(
                    Duration.millis(300), titleLabel);
            pulse.setToX(1.12);
            pulse.setToY(1.12);
            pulse.setAutoReverse(true);
            pulse.setCycleCount(2);
            pulse.setInterpolator(Interpolator.EASE_BOTH);
            pulse.play();

            Timeline glowAnim = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(glow.radiusProperty(), 0)),
                    new KeyFrame(Duration.seconds(1),
                            new KeyValue(glow.radiusProperty(), 25))
            );
            glowAnim.play();

            subtitleLabel.setText("Varsity Simulator");
            FadeTransition subFade = new FadeTransition(
                    Duration.seconds(1), subtitleLabel);
            subFade.setFromValue(0);
            subFade.setToValue(1);
            subFade.play();
        });

        typewriter.play();

        // Animated gradient background
        Timeline gradientAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(rootPane.styleProperty(),
                                "-fx-background-color: linear-gradient(to bottom right, #0f0c29, #302b63, #24243e);")),
                new KeyFrame(Duration.seconds(4),
                        new KeyValue(rootPane.styleProperty(),
                                "-fx-background-color: linear-gradient(to bottom right, #1e3c72, #2a5298, #0f2027);"))
        );
        gradientAnimation.setAutoReverse(true);
        gradientAnimation.setCycleCount(Animation.INDEFINITE);
        gradientAnimation.play();

        // Auto redirect
        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(e -> SceneManager.switchScene("home.fxml"));
        delay.play();
    }

    private void addSplashParticles() {
        Random rand = new Random();
        for (int i = 0; i < 25; i++) {
            Circle dot = new Circle(1.5 + rand.nextDouble() * 3.5);
            dot.setFill(Color.WHITE);
            dot.setOpacity(0.08 + rand.nextDouble() * 0.15);
            dot.setMouseTransparent(true);
            dot.setTranslateX(-500 + rand.nextDouble() * 1000);
            dot.setTranslateY(-325 + rand.nextDouble() * 650);

            rootPane.getChildren().add(0, dot);

            TranslateTransition moveY = new TranslateTransition(
                    Duration.seconds(3 + rand.nextDouble() * 5), dot);
            moveY.setByY(-40 - rand.nextDouble() * 60);
            moveY.setAutoReverse(true);
            moveY.setCycleCount(Animation.INDEFINITE);
            moveY.setInterpolator(Interpolator.EASE_BOTH);
            moveY.play();

            FadeTransition fade = new FadeTransition(
                    Duration.seconds(2 + rand.nextDouble() * 3), dot);
            fade.setFromValue(dot.getOpacity());
            fade.setToValue(0.03);
            fade.setAutoReverse(true);
            fade.setCycleCount(Animation.INDEFINITE);
            fade.play();
        }
    }
}