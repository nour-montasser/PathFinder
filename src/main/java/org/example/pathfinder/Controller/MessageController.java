package org.example.pathfinder.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import org.example.pathfinder.Service.MessageService;
import org.example.pathfinder.Model.Message;

public class MessageController {

    @FXML
    private TextField messageInput;  // Message input field

    @FXML
    private ImageView sendButton;

    @FXML
    private ListView<String> messageListView;// Send button (ImageView)

    private MessageService messageService;

    public MessageController() {
        messageService = new MessageService();  // Initialize the message service
    }

    @FXML
    private void initialize() {
        // Handle the button click for sending the message
        sendButton.setOnMouseClicked(event -> sendMessage());

    }

    @FXML
    private void sendMessage() {
        String content = messageInput.getText();

        if (content == null || content.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Message cannot be empty.");
            return;
        }

        Message message = new Message(content, 1L, 1L, "text", 2L);
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
                messageService.delete(message, messageListView);  // Call delete method
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
}
