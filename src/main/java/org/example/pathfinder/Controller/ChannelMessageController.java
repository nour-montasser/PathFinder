package org.example.pathfinder.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.example.pathfinder.Model.Message;
import org.example.pathfinder.Model.User;
import org.example.pathfinder.Service.ChannelService;
import org.example.pathfinder.Service.MessageService;
import org.example.pathfinder.Service.UserService;

import java.util.List;

public class ChannelMessageController {
    private UserController userController;
    private ChannelService channelService;
    @FXML
    private ListView<User> userListView;  // Assuming your ListView holds User objects
    @FXML
    private TextField messageInput;  // Message input field

    @FXML
    private ImageView sendButton;

    @FXML
    private ListView<String> messageListView; // Message display (ListView)

    private MessageService messageService;
    private User selectedUser;  // Currently selected user
    private Long currentChannelId;
    private UserService userService;

    public ChannelMessageController() {
        messageService = new MessageService();
        userController = new UserController();
        channelService = new ChannelService();
        userService = new UserService(); // Ensure the UserService is initialized
    }

    @FXML
    private void initialize() {
        // Fetch all users and populate the user list
        List<User> usersList = userService.getall();  // Fetching the list of users
        if (usersList != null && !usersList.isEmpty()) {
            ObservableList<User> usersObservableList = FXCollections.observableArrayList(usersList);  // Converting to ObservableList
            userListView.setItems(usersObservableList);  // Setting the items to the ListView

            // Set the custom cell factory for displaying user names
            userListView.setCellFactory(listView -> new ListCell<User>() {
                @Override
                protected void updateItem(User item, boolean empty) {
                    super.updateItem(item, empty);  // Always call the superclass method first
                    if (empty || item == null) {
                        setText(null);  // If the cell is empty, set no text
                    } else {
                        setText(item.getName());  // Otherwise, display the user's name
                    }
                }
            });
        } else {
            System.out.println("No users found.");
        }

        sendButton.setOnMouseClicked(event -> sendMessage());  // Set the action for sendButton
    }


    @FXML
    private void sendMessage() {
        String content = messageInput.getText().trim();

        if (content.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Message cannot be empty.");
            return;
        }

        Long userId = 1L;  // Assuming userId is 1L (replace with dynamic logged-in user ID)
        Long selectedUserId = selectedUser != null ? selectedUser.getId() : null;

        if (selectedUserId == null) {
            showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select a user to message.");
            return;
        }

        Long channelId = messageService.getChannelIdBetweenUsers(userId, selectedUserId);
        if (channelId == null) {
            showAlert(Alert.AlertType.ERROR, "Channel Error", "No channel found between the selected users.");
            return;
        }

        Message message = new Message(content, userId, selectedUserId, "text", channelId);
        messageService.add(message);  // Add to database

        // Update the message ListView after adding the message
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

        for (Message message : messageService.getall()) {
            if (message.getContent().equals(selectedMessage)) {
                messageService.delete(message);  // Call delete method
                messageListView.getItems().remove(selectedMessage);  // Remove from ListView

                showAlert(Alert.AlertType.INFORMATION, "Message Deleted", "Message deleted successfully.");
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

        for (Message message : messageService.getall()) {
            if (message.getContent().equals(selectedMessage)) {
                message.setContent(newContent);
                messageService.update(message);  // Update in database
                messageListView.getItems().set(messageListView.getItems().indexOf(selectedMessage), newContent);

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

    private void displayMessages(List<Message> messages, Long channelId) {
        messageListView.getItems().clear();  // Clear previous messages

        for (Message message : messages) {
            if (message.getIdChannel().equals(channelId)) {  // Filter by channel
                messageListView.getItems().add(message.getContent());
            }
        }
    }

    @FXML
    private void handleUserClick(MouseEvent event) {
        User selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            this.selectedUser = selectedUser;  // Set the selected user
            onUserSelected(selectedUser.getId());
        }
    }

    @FXML
    private void onUserSelected(Long selectedUserId) {
        Long userId = 1L;  // Dynamically set based on logged-in user

        Long channelId = messageService.getChannelIdBetweenUsers(userId, selectedUserId);
        if (channelId == null) {
            showAlert(Alert.AlertType.ERROR, "Channel Error", "No channel found between you and the selected user.");
            return;
        }

        List<Message> messages = messageService.getMessagesByChannelId(channelId);
        displayMessages(messages, channelId);  // Display messages for the selected channel
    }

    @FXML
    private void createOrGetChannel(ActionEvent event) {
        Long userId = 1L;  // Dynamically set based on logged-in user
        Long selectedUserId = selectedUser != null ? selectedUser.getId() : null;

        if (selectedUserId == null) {
            showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select a user first.");
            return;
        }

        Long channelId = channelService.getOrCreateChannel(selectedUserId);
        if (channelId != null) {
            System.out.println("Channel ID: " + channelId);
        } else {
            System.out.println("Failed to create or get the channel.");
        }
    }
}
