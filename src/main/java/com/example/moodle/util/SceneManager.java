package com.example.moodle.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource("/com/example/moodle/" + fxml)
            );

            Scene scene = new Scene(loader.load(), 1000, 650);

            // ✅ Load CSS here
            scene.getStylesheets().add(
                    SceneManager.class
                            .getResource("/com/example/moodle/style.css")
                            .toExternalForm()
            );

            primaryStage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
