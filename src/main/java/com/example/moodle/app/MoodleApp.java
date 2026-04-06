package com.example.moodle.app;

import com.example.moodle.service.MessageNetworkBridge;
import com.example.moodle.util.SceneManager;

import javafx.application.Application;
import javafx.stage.Stage;

public class MoodleApp extends Application {

    @Override
    public void start(Stage stage) {

        MessageNetworkBridge.startServer();

        SceneManager.setStage(stage);
        SceneManager.switchScene("splash.fxml");

        stage.setTitle("Unimate");
        stage.setResizable(true);
        stage.setMaximized(true);
        stage.show();
    }

    @Override
    public void stop() {
        MessageNetworkBridge.shutdown();
    }
}