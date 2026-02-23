package com.example.moodle.util;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class BackgroundAnimator {

    public static void addFloatingCircles(Pane layer) {

        if (layer == null) {
            return;
        }

        for (int i = 0; i < 8; i++) {

            Circle circle = new Circle(20 + Math.random() * 40);
            circle.setFill(Color.WHITE);
            circle.setOpacity(0.07);
            circle.setMouseTransparent(true);

            layer.getChildren().add(circle);

            TranslateTransition vertical
                    = new TranslateTransition(Duration.seconds(5 + Math.random() * 5), circle);

            vertical.setByY(40);
            vertical.setAutoReverse(true);
            vertical.setCycleCount(Animation.INDEFINITE);
            vertical.play();
        }
    }
}