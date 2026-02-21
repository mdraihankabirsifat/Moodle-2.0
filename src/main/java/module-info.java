module com.example.moodle {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.moodle to javafx.fxml;
    exports com.example.moodle;
}