module org.example.pathfinder {
    requires mysql.connector.j;
    requires org.apache.pdfbox;
    requires okhttp3;
    requires commons.email;
    requires com.google.api.services.calendar;
    requires com.google.api.client;
    requires google.api.client;
    requires com.google.api.client.auth;
    requires com.google.auth.oauth2;
    requires com.google.api.client.json.jackson2;
    requires com.google.auth;
    requires com.google.api.client.extensions.java6.auth;
    requires com.google.api.client.extensions.jetty.auth;
    requires jdk.httpserver;
    requires java.net.http;

    // ✅ JavaFX Modules (Ensure all required ones are included)
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.swing;

    requires java.desktop;
    requires java.sql; // Required for database connection

    requires kernel;
    requires layout;
    requires io;
    requires org.json;

    // ✅ Open necessary packages for JavaFX to access them
    opens org.example.pathfinder.Controller to javafx.fxml;
    opens org.example.pathfinder.App to javafx.fxml;
    opens org.example.pathfinder.Model to javafx.base;

    // ✅ Export necessary packages
    exports org.example.pathfinder.Controller;
    exports org.example.pathfinder.App;
}
