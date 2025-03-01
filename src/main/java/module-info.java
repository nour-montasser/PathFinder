module org.example.pathfinder {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // Required for database connection

    requires java.desktop; // 🚀 This fixes the issue!
    requires javafx.graphics;
    requires org.apache.pdfbox;
    requires javafx.swing;
    requires kernel;
    requires layout;
    requires io;
    requires org.json;


    // Open the package containing your controllers to JavaFX
    opens org.example.pathfinder.Controller to javafx.fxml;
    opens org.example.pathfinder.App to javafx.fxml; // Open the package for JavaFX
    exports org.example.pathfinder.App;

    opens org.example.pathfinder.Model to javafx.base;  // 🔥 Add this line
}
