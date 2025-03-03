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
    requires java.sql;
    requires com.dlsc.formsfx;
    requires com.google.gson;  // ✅ This allows you to use Gson
    requires org.apache.httpcomponents.client5.httpclient5;
    requires javafx.base;
    requires javafx.swing;

    requires org.apache.httpcomponents.core5.httpcore5;
    requires java.desktop;
    requires java.sql; // Required for database connection

    requires kernel;
    requires layout;
    requires io;
    requires org.json;
    requires javafx.web;
    requires jdk.jsobject;  // Add this for JSON parsing
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
