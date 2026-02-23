package com.example.moodle.util;

import java.net.URL;

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

            String path = "/com/example/moodle/" + fxml;

            URL resource = SceneManager.class.getResource(path);

            if (resource == null) {
                System.out.println("FXML NOT FOUND: " + path);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Scene scene = new Scene(loader.load(), 1000, 650);

            primaryStage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}