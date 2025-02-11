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

import java.sql.Timestamp;
import java.util.List;


public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        MessageService messageService = new MessageService();

        // Opérations sur la base de données (tests)
        Message message = new Message("Hello, this is a test message", 1L, 1L, "text", 2L);
        System.out.println("Adding message...");
        messageService.add(message);
        System.out.println("Retrieving all messages...");
        List<Message> messages = messageService.getall();
        for (Message msg : messages) {
            System.out.println("Message ID: " + msg.getIdMessage() + ", Content: " + msg.getContent());
        }

        // CHARGEMENT DE L'INTERFACE JAVA FX 📌
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Channel.fxml"));
        Parent root = loader.load();  // Charge l'interface depuis un fichier FXML
        Scene scene = new Scene(root);

        stage.setTitle("PathFinder App");
        stage.setScene(scene);
        stage.show();  // Affiche la fenêtre
    }

    public static void main(String[] args) {
        launch();  // Lance l'application JavaFX
    }

}
