module org.example.pathfinder {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    opens org.example.pathfinder.Controller to javafx.fxml;
    opens org.example.pathfinder.App to javafx.fxml; // Open the App subpackage
    opens org.example.pathfinder.Model to javafx.base;

    exports org.example.pathfinder.App; // Export the App subpackage
    exports org.example.pathfinder.Controller;
}
