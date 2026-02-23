package com.example.moodle.app;

import com.example.moodle.util.SceneManager;

import javafx.application.Application;
import javafx.stage.Stage;

public class MoodleApp extends Application {

    @Override
    public void start(Stage stage) {

        SceneManager.setStage(stage);
        SceneManager.switchScene("splash.fxml");

        stage.setTitle("Moodle 2.0");
        stage.setResizable(true);
        stage.show();
    }
}