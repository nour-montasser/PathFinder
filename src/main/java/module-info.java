
module org.example.pathfinder {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop; // Required for database connection

    // Open specific packages for JavaFX access
    opens org.example.pathfinder.Controller to javafx.fxml;
    opens org.example.pathfinder.App to javafx.fxml;

    // Export the base package (where your main app classes might reside)
    exports org.example.pathfinder.App;
}
