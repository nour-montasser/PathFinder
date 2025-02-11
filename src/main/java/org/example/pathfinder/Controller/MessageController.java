package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import org.example.pathfinder.Service.MessageService;
import org.example.pathfinder.Model.Message;

public class MessageController {

    @FXML
    private TextField messageInput;  // Message input field

    @FXML
    private ImageView sendButton;  // Send button (ImageView)

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
        System.out.println("Send button clicked!");

        String content = messageInput.getText();  // Get message content from the input field
        System.out.println("Message content: " + content); // Debug print

        try {
            // If content is empty, we don't proceed further
            if (content == null || content.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Message cannot be empty.");
                return;

            }

            // Create the Message object with actual values (sender id, type, timestamp, channel id)
            Message message = new Message(content, 1L, 1L, "text",2L);  // Sample values for now

            // Call the service to add the message
            messageService.add(message);

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Message Sent", "The message was successfully added to the database.");

            // Clear the input field after sending the message
            messageInput.clear();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while sending the message.");
            e.printStackTrace();
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
