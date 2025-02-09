module com.example.pathfinder {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.dlsc.formsfx;

    opens org.example.pathfinder to javafx.fxml;

    exports org.example.pathfinder.App;


    // Add these exports to allow other modules to access them
    exports org.example.pathfinder.Controller;
    exports org.example.pathfinder.Model;
    exports org.example.pathfinder.Service;

    // If using FXML in these packages, open them too
    opens org.example.pathfinder.Controller to javafx.fxml;
    opens org.example.pathfinder.Model to javafx.fxml;
    opens org.example.pathfinder.Service to javafx.fxml;
}
