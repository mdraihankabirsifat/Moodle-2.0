module com.example.moodle {
    requires javafx.controls;
    requires javafx.fxml;

    // Allow JavaFX to access your controller for the FXML injections (@FXML)
    opens com.example.moodle.controller to javafx.fxml;

    // Open model and service packages for internal access
    opens com.example.moodle.model;
    opens com.example.moodle.service;
    opens com.example.moodle.util;

    // Allow JavaFX to launch your app
    exports com.example.moodle.app;
}