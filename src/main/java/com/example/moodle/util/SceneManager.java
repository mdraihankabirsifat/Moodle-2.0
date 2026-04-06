package com.example.moodle.util;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SceneManager {

    // ========== Multi-window support ==========
    private static final Map<Stage, StageContext> stages = new HashMap<>();
    private static Stage activeStage;

    private static class StageContext {
        final Stack<String> history = new Stack<>();
        String currentFxml = null;
    }

    public static void setStage(Stage stage) {
        stages.put(stage, new StageContext());
        activeStage = stage;
        stage.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused && stages.containsKey(stage)) activeStage = stage;
        });
        stage.setOnCloseRequest(e -> {
            stages.remove(stage);
            Session.removeStage(stage);
        });
    }

    /** Returns the currently active (focused) stage */
    public static Stage getActiveStage() {
        return activeStage;
    }

    private static StageContext ctx() {
        if (activeStage == null || !stages.containsKey(activeStage)) return null;
        return stages.get(activeStage);
    }

    public static void switchScene(String fxml) {
        StageContext ctx = ctx();
        if (ctx == null) return;
        if (ctx.currentFxml != null && !ctx.currentFxml.equals("splash.fxml")
                && !ctx.currentFxml.equals(fxml)) {
            ctx.history.push(ctx.currentFxml);
        }
        ctx.currentFxml = fxml;
        loadScene(fxml, activeStage);
    }

    public static void goBack() {
        StageContext ctx = ctx();
        if (ctx != null && !ctx.history.isEmpty()) {
            String prev = ctx.history.pop();
            ctx.currentFxml = prev;
            loadScene(prev, activeStage);
        }
    }

    /** Open a brand-new independent window starting at home.fxml */
    public static Stage openNewWindow() {
        Stage newStage = new Stage();
        newStage.setTitle("Unimate — Window " + stages.size());
        setStage(newStage);
        switchScene("home.fxml");
        newStage.setResizable(true);
        newStage.show();
        return newStage;
    }

    private static void loadScene(String fxml, Stage stage) {

        if (stage == null) {
            System.out.println("Stage not set!");
            return;
        }

        // Ensure active stage is correct before loading FXML (controllers use Session in initialize)
        activeStage = stage;

        if (stage.getScene() != null && stage.getScene().getRoot() != null && !fxml.equals("splash.fxml")) {
            Parent oldRoot = stage.getScene().getRoot();
            stage.getScene().setFill(javafx.scene.paint.Color.valueOf("#0a1628"));
            FadeTransition fadeOut = new FadeTransition(Duration.millis(50), oldRoot);
            fadeOut.setFromValue(oldRoot.getOpacity());
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> executeSceneSwitch(fxml, stage));
            fadeOut.play();
        } else {
            executeSceneSwitch(fxml, stage);
        }
    }

    private static void executeSceneSwitch(String fxml, Stage stage) {
        try {
            String path = "/com/example/moodle/" + fxml;
            URL resource = SceneManager.class.getResource(path);

            if (resource == null) {
                System.out.println("FXML NOT FOUND: " + path);
                return;
            }

            // Preserve window dimensions and state before switching
            boolean wasMaximized = stage.isMaximized();
            double prevWidth = Math.max(1000, stage.getWidth());
            double prevHeight = Math.max(650, stage.getHeight());
            boolean hadScene = (stage.getScene() != null);

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

            Scene scene;
            if (hadScene) {
                // Use previous dimensions to preserve user's window size
                scene = new Scene(root, prevWidth, prevHeight);
            } else {
                scene = new Scene(root, 1000, 650);
            }
            scene.setFill(javafx.scene.paint.Color.valueOf("#0a1628"));

            // Safe CSS loading
            URL css = SceneManager.class.getResource("/com/example/moodle/style.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            ThemeManager.applyTheme(scene);
            stage.setScene(scene);

            // Restore maximized state after setting the scene
            if (wasMaximized) {
                stage.setMaximized(true);
            }

            FadeTransition fadeIn = new FadeTransition(Duration.millis(150), root);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            TranslateTransition slideIn = new TranslateTransition(Duration.millis(250), root);
            slideIn.setFromY(20);
            slideIn.setToY(0);

            ParallelTransition pt = new ParallelTransition(fadeIn, slideIn);
            pt.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}