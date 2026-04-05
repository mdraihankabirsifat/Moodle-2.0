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
import javafx.scene.shape.Line;
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

        // Add cyber particles
        addCyberParticles();

        // Typewriter effect for title
        String fullTitle = "MOODLE 2.0";
        titleLabel.setText("");
        titleLabel.setOpacity(1);
        subtitleLabel.setOpacity(0);

        // Neon cyan glow effect on title
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#00e5ff"));
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
                            new KeyValue(glow.radiusProperty(), 30))
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

        // Animated dark gradient background
        Timeline gradientAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(rootPane.styleProperty(),
                                "-fx-background-color: linear-gradient(to bottom right, #050810, #0a1628, #060c1a);")),
                new KeyFrame(Duration.seconds(4),
                        new KeyValue(rootPane.styleProperty(),
                                "-fx-background-color: linear-gradient(to bottom right, #0a1628, #0d2847, #050810);"))
        );
        gradientAnimation.setAutoReverse(true);
        gradientAnimation.setCycleCount(Animation.INDEFINITE);
        gradientAnimation.play();

        // Auto redirect
        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(e -> SceneManager.switchScene("home.fxml"));
        delay.play();
    }

    private void addCyberParticles() {
        Random rand = new Random();

        // Floating cyan dots
        for (int i = 0; i < 30; i++) {
            Circle dot = new Circle(1 + rand.nextDouble() * 3);
            dot.setFill(Color.web("#00e5ff"));
            dot.setOpacity(0.04 + rand.nextDouble() * 0.1);
            dot.setMouseTransparent(true);
            dot.setTranslateX(-500 + rand.nextDouble() * 1000);
            dot.setTranslateY(-325 + rand.nextDouble() * 650);

            rootPane.getChildren().add(0, dot);

            TranslateTransition moveY = new TranslateTransition(
                    Duration.seconds(3 + rand.nextDouble() * 5), dot);
            moveY.setByY(-30 - rand.nextDouble() * 50);
            moveY.setAutoReverse(true);
            moveY.setCycleCount(Animation.INDEFINITE);
            moveY.setInterpolator(Interpolator.EASE_BOTH);
            moveY.play();

            FadeTransition fade = new FadeTransition(
                    Duration.seconds(2 + rand.nextDouble() * 3), dot);
            fade.setFromValue(dot.getOpacity());
            fade.setToValue(0.01);
            fade.setAutoReverse(true);
            fade.setCycleCount(Animation.INDEFINITE);
            fade.play();
        }

        // Horizontal scan lines for cyber effect
        for (int i = 0; i < 5; i++) {
            Line scanLine = new Line(-500, 0, 500, 0);
            scanLine.setStroke(Color.web("#00e5ff"));
            scanLine.setOpacity(0.02 + rand.nextDouble() * 0.03);
            scanLine.setStrokeWidth(0.5);
            scanLine.setMouseTransparent(true);
            scanLine.setTranslateY(-200 + i * 100);
            rootPane.getChildren().add(0, scanLine);

            FadeTransition lineFade = new FadeTransition(
                    Duration.seconds(2 + rand.nextDouble() * 4), scanLine);
            lineFade.setFromValue(scanLine.getOpacity());
            lineFade.setToValue(0.005);
            lineFade.setAutoReverse(true);
            lineFade.setCycleCount(Animation.INDEFINITE);
            lineFade.play();
        }
    }
}