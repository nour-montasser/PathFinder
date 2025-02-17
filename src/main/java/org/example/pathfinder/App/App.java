package org.example.pathfinder.App;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.pathfinder.Model.Channel;
import org.example.pathfinder.Model.Message;
import org.example.pathfinder.Service.ChannelService;
import org.example.pathfinder.Service.MessageService;

import java.util.List;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Initialize services
        MessageService messageService = new MessageService();



        // Test MessageService (CRUD operations)

        System.out.println("Retrieving all messages...");
        List<Message> messages = messageService.getall();
        for (Message msg : messages) {
            System.out.println("Message ID: " + msg.getIdMessage() + ", Content: " + msg.getContent());
        }

        // Load JavaFX interface

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Message.fxml"));
        Parent root = loader.load();  // Load the FXML layout
        Scene scene = new Scene(root);


        stage.setTitle("PathFinder App");
        stage.setScene(scene);
        stage.show();  // Display the window
    }

    public static void main(String[] args) {
        launch();  // Launch the JavaFX application
    }
}
