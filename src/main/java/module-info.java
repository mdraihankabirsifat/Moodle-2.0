module com.example.moodle {
    requires javafx.controls;
    requires javafx.fxml;

    // Allow JavaFX to access your controller for the FXML injections (@FXML)
    opens com.example.moodle.controller to javafx.fxml;

    // Allow JavaFX to launch your app
    exports com.example.moodle.app;
}