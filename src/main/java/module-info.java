module com.example.pathfinder {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.dlsc.formsfx;
    requires com.google.gson;  // ✅ This allows you to use Gson
    requires org.apache.httpcomponents.client5.httpclient5;

    requires org.apache.httpcomponents.core5.httpcore5;
    requires org.json;
    requires javafx.web;  // Add this for JSON parsing
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
