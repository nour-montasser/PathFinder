module org.example.pathfinder {
    requires javafx.fxml;
    requires javafx.controls;

    requires java.sql;
    requires org.json;
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
    requires javafx.graphics;
    requires java.desktop;


    opens org.example.pathfinder.Controller to javafx.fxml;
    opens org.example.pathfinder.App to javafx.fxml; // Open the App subpackage
    opens org.example.pathfinder.Model to javafx.base;

    exports org.example.pathfinder.App; // Export the App subpackage
    exports org.example.pathfinder.Controller;
}
