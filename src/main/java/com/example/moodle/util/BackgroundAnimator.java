package com.example.moodle.util;

import java.util.Random;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class BackgroundAnimator {

    private static final Random rand = new Random();

    public static void addFloatingShapes(Pane layer, double width, double height) {

        if (layer == null) return;

        layer.setMouseTransparent(true);
        layer.setPickOnBounds(false);

        for (int i = 0; i < 15; i++) {

            Node shape;

            if (rand.nextBoolean()) {
                Circle c = new Circle(8 + rand.nextDouble() * 25);
                c.setFill(Color.web("#2a5298"));
                shape = c;
            } else {
                double size = 12 + rand.nextDouble() * 30;
                Rectangle r = new Rectangle(size, size);
                r.setArcWidth(8);
                r.setArcHeight(8);
                r.setFill(Color.web("#1e3c72"));
                shape = r;
            }

            shape.setOpacity(0.045 + rand.nextDouble() * 0.055);
            shape.setMouseTransparent(true);
            shape.setLayoutX(rand.nextDouble() * width);
            shape.setLayoutY(rand.nextDouble() * height);

            layer.getChildren().add(shape);

            TranslateTransition moveY = new TranslateTransition(
                    Duration.seconds(4 + rand.nextDouble() * 6), shape);
            moveY.setByY(-25 - rand.nextDouble() * 35);
            moveY.setAutoReverse(true);
            moveY.setCycleCount(Animation.INDEFINITE);
            moveY.setInterpolator(Interpolator.EASE_BOTH);

            TranslateTransition moveX = new TranslateTransition(
                    Duration.seconds(6 + rand.nextDouble() * 8), shape);
            moveX.setByX(-15 + rand.nextDouble() * 30);
            moveX.setAutoReverse(true);
            moveX.setCycleCount(Animation.INDEFINITE);
            moveX.setInterpolator(Interpolator.EASE_BOTH);

            moveY.play();
            moveX.play();
        }
    }

    public static void addFloatingCircles(Pane layer) {
        addFloatingShapes(layer, 1000, 650);
    }
}