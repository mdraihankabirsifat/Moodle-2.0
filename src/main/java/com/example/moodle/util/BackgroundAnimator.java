package com.example.moodle.util;

import java.util.Random;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
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

        for (int i = 0; i < 25; i++) {

            Node shape;

            if (rand.nextBoolean()) {
                Circle c = new Circle(4 + rand.nextDouble() * 12);
                c.setFill(Color.web("#00e5ff"));
                shape = c;
            } else {
                double size = 6 + rand.nextDouble() * 16;
                Rectangle r = new Rectangle(size, size);
                r.setArcWidth(3);
                r.setArcHeight(3);
                r.setFill(Color.web("#0088cc"));
                shape = r;
            }

            shape.setOpacity(0.025 + rand.nextDouble() * 0.04);
            shape.setMouseTransparent(true);
            shape.setLayoutX(rand.nextDouble() * width);
            shape.setLayoutY(rand.nextDouble() * height);

            layer.getChildren().add(shape);

            TranslateTransition moveY = new TranslateTransition(
                    Duration.seconds(5 + rand.nextDouble() * 8), shape);
            moveY.setByY(-20 - rand.nextDouble() * 30);
            moveY.setAutoReverse(true);
            moveY.setCycleCount(Animation.INDEFINITE);
            moveY.setInterpolator(Interpolator.EASE_BOTH);

            TranslateTransition moveX = new TranslateTransition(
                    Duration.seconds(7 + rand.nextDouble() * 10), shape);
            moveX.setByX(-10 + rand.nextDouble() * 20);
            moveX.setAutoReverse(true);
            moveX.setCycleCount(Animation.INDEFINITE);
            moveX.setInterpolator(Interpolator.EASE_BOTH);

            FadeTransition fade = new FadeTransition(
                    Duration.seconds(3 + rand.nextDouble() * 5), shape);
            fade.setFromValue(shape.getOpacity());
            fade.setToValue(0.01);
            fade.setAutoReverse(true);
            fade.setCycleCount(Animation.INDEFINITE);

            moveY.play();
            moveX.play();
            fade.play();
        }
    }

    public static void addFloatingCircles(Pane layer) {
        addFloatingShapes(layer, 1000, 650);
    }
}