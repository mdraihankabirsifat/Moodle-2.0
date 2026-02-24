package com.example.moodle.util;

import java.net.URL;
import java.util.Stack;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SceneManager {

    private static Stage primaryStage;
    private static final Stack<String> history = new Stack<>();
    private static String currentFxml = null;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(String fxml) {
        if (currentFxml != null && !currentFxml.equals("splash.fxml") && !currentFxml.equals(fxml)) {
            history.push(currentFxml);
        }
        currentFxml = fxml;
        loadScene(fxml);
    }

    public static void goBack() {
        if (!history.isEmpty()) {
            String prev = history.pop();
            currentFxml = prev;
            loadScene(prev);
        }
    }

    private static void loadScene(String fxml) {

        if (primaryStage == null) {
            System.out.println("Primary stage not set!");
            return;
        }

        try {

            String path = "/com/example/moodle/" + fxml;
            URL resource = SceneManager.class.getResource(path);

            if (resource == null) {
                System.out.println("FXML NOT FOUND: " + path);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            // Add floating background for non-splash pages
            if (!fxml.equals("splash.fxml")) {
                Pane floatingLayer = new Pane();
                floatingLayer.setMouseTransparent(true);
                floatingLayer.setPickOnBounds(false);
                StackPane wrapper = new StackPane(floatingLayer, root);
                BackgroundAnimator.addFloatingShapes(floatingLayer, 1000, 650);
                root = wrapper;
            }

            root.setOpacity(0);

            Scene scene = new Scene(root, 1000, 650);

            // Safe CSS loading
            URL css = SceneManager.class
                    .getResource("/com/example/moodle/style.css");

            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            primaryStage.setScene(scene);

            FadeTransition fadeIn
                    = new FadeTransition(Duration.millis(400), root);

            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}