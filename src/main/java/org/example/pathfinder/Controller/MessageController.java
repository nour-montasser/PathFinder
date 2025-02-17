package org.example.pathfinder.Controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import org.example.pathfinder.Model.Message;
import org.example.pathfinder.Service.ChannelService;
import org.example.pathfinder.Service.MessageService;

import java.io.IOException;
import java.util.List;

public class MessageController {
    private UserController userController;
    private ChannelService channelService;

    @FXML
    private TextField messageInput;  // Message input field

    @FXML
    private ImageView sendButton;

    @FXML
    private ListView<String> messageListView;// Send button (ImageView)

    private MessageService messageService;


    public MessageController() {
        messageService = new MessageService();
        userController = new UserController();
        channelService = new ChannelService();  // Initialize ChannelService
    }


    @FXML
    private void initialize() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Channel.fxml"));
        Parent root = null;  // Load the FXML layout
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(root);
        sendButton.setOnMouseClicked(event -> sendMessage());

    }

    @FXML
    private void sendMessage() {
        String content = messageInput.getText();

        if (content == null || content.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Message cannot be empty.");
            return;
        }

        Message message = new Message(content, 1L, 1L, "text", 9L);
        messageService.add(message);  // Add to database

        // Update UI after adding the message
        messageListView.getItems().add(content);

        showAlert(Alert.AlertType.INFORMATION, "Message Sent", "Message added successfully.");
        messageInput.clear();
    }
    @FXML
    private void deleteMessage() {
        String selectedMessage = messageListView.getSelectionModel().getSelectedItem();

        if (selectedMessage == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a message to delete.");
            return;
        }

        // Find the message in the database by content
        for (Message message : messageService.getall()) {
            if (message.getContent().equals(selectedMessage)) {
                messageService.delete(message);  // Call delete method

                // Remove the deleted message from ListView
                ObservableList<String> items = messageListView.getItems();
                items.remove(selectedMessage);

                showAlert(Alert.AlertType.INFORMATION, "Message Deleted", "Message has been deleted successfully.");
                return;
            }
        }

        showAlert(Alert.AlertType.ERROR, "Error", "Message not found in database.");
    }



    @FXML
    private void updateMessage() {
        String selectedMessage = messageListView.getSelectionModel().getSelectedItem();

        if (selectedMessage == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a message to update.");
            return;
        }

        String newContent = messageInput.getText().trim();

        if (newContent.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "New content cannot be empty.");
            return;
        }

        // Recherche du message dans la base de données
        for (Message message : messageService.getall()) {
            if (message.getContent().equals(selectedMessage)) {
                message.setContent(newContent);
                messageService.update(message);  // Mise à jour en base de données

                // Mise à jour de l'interface utilisateur
                int index = messageListView.getItems().indexOf(selectedMessage);
                messageListView.getItems().set(index, newContent);

                showAlert(Alert.AlertType.INFORMATION, "Message Updated", "Message updated successfully.");
                messageInput.clear();
                return;
            }
        }

        showAlert(Alert.AlertType.ERROR, "Error", "Message not found in database.");
    }






    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void displayMessages(List<Message> messages) {
        // Clear any previous messages from the ListView
        messageListView.getItems().clear();

        // Loop through the messages and add each one to the ListView
        for (Message message : messages) {
            messageListView.getItems().add(message.getContent()); // Assuming you're showing the message content
        }
    }


}
