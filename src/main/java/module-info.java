module org.example.pathfinder {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // Required for database connection
    // Export the base package


    // Open the package containing your controllers to JavaFX
    opens org.example.pathfinder.Controller to javafx.fxml;
    opens org.example.pathfinder.App to javafx.fxml; // Open the package for JavaFX
    exports org.example.pathfinder.App;
}
