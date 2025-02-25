package org.example.pathfinder.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.example.pathfinder.Model.Channel;
import org.example.pathfinder.Model.Message;
import org.example.pathfinder.Model.User;
import org.example.pathfinder.Service.ChannelService;
import org.example.pathfinder.Service.MessageService;
import org.example.pathfinder.Service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChannelMessageController {
    @FXML
    public ComboBox searchBox;

    private UserController userController;
    private ChannelService channelService;
    @FXML
    private Label userLabel;
    @FXML
    private ListView<Channel> channelListView;
    @FXML
    private ListView<User> userListView;  // Assuming your ListView holds User objects
    @FXML
    private TextField messageInput;
    @FXML
    private Button deleteChannelButton;

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

        // Channel ListView mouse click event
        channelListView.setOnMouseClicked(event -> {
            // Get the selected channel from the ListView
            Channel selectedChannel = channelListView.getSelectionModel().getSelectedItem();
            if (selectedChannel != null) {
                // Fetch the user related to this channel (assuming user2Id links user to channel)
                User selectedUser = userService.getall().stream()
                        .filter(user -> user.getId() == selectedChannel.getUser2Id())  // Assuming user2Id links user to channel
                        .findFirst()
                        .orElse(null);

                if (selectedUser != null) {
                    this.selectedUser = selectedUser;  // Set the selected user
                    System.out.println("Selected User: " + selectedUser.getName());
                    userLabel.setText(selectedUser.getName()); // Assuming selectedUser is a User object
                    userLabel.setUnderline(true);// Debug output


                    // Fetch the messages related to the selected channel
                    List<Message> messages = messageService.getMessagesByChannelId(selectedChannel.getId());

                    // Call the method to display the messages in the messageListView
                    displayMessages(messages, selectedChannel.getId());
                } else {
                    showAlert(Alert.AlertType.ERROR, "User Error", "User not found for the selected channel.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Channel Error", "No channel selected.");
            }
        });

        // Fetch all users and populate the user list for the ComboBox
        List<User> usersList = userService.getall();
        if (usersList != null && !usersList.isEmpty()) {
            ObservableList<String> allUserNames = FXCollections.observableArrayList(
                    usersList.stream().map(User::getName).collect(Collectors.toList())
            );

            // Populate ComboBox with the full list of user names
            searchBox.setItems(allUserNames); // Set ComboBox to always show users

            // Handle ComboBox user selection to create channel if needed
            searchBox.setOnAction(event -> {
                String selectedUserName = (String) searchBox.getValue();
                // Get selected user name
                if (selectedUserName != null) {
                    User selectedUser = userService.getall().stream()
                            .filter(user -> user.getName().equals(selectedUserName)) // Find the selected user by name
                            .findFirst()
                            .orElse(null);

                    if (selectedUser != null) {
                        // Check if a channel already exists with the selected user
                        List<Channel> existingChannels = channelService.getall().stream()
                                .filter(channel -> channel.getUser2Id() == selectedUser.getId())  // Assuming user2Id links user to channel
                                .collect(Collectors.toList());

                        if (existingChannels.isEmpty()) {
                            // If no existing channel, create a new one
                            Channel newChannel = new Channel();  // Create a new channel object

// Check if the selected user is the same as user1Id
                            if (newChannel.getUser1Id() == selectedUser.getId()) {
                                System.out.println("Cannot select the same user for both user1 and user2.");
                            } else {
                                // Set user2Id only if it's not the same as user1Id
                                newChannel.setUser2Id(selectedUser.getId());
                                channelService.add(newChannel);  // Add the new channel
                                System.out.println("New channel created with " + selectedUser.getName());
                            }



                            // Update the channel list in the ListView
                            ObservableList<Channel> updatedChannels = FXCollections.observableArrayList(channelService.getall());

                            channelListView.setItems(updatedChannels);

                        } else {
                            System.out.println("A channel already exists with " + selectedUser.getName());
                        }
                    } else {
                        showAlert(Alert.AlertType.ERROR, "User Error", "Selected user not found.");
                    }
                }
            });
        } else {
            System.out.println("No users found.");
        }

        // Fetch all channels and populate the channel list
        List<Channel> channelsList = channelService.getall();
        if (channelsList != null && !channelsList.isEmpty()) {
            ObservableList<Channel> channelsObservableList = FXCollections.observableArrayList(channelsList);
            channelListView.setItems(channelsObservableList);

            // Set the custom cell factory to display channel elements in HBox
            channelListView.setCellFactory(listView -> new ListCell<Channel>() {
                @Override
                protected void updateItem(Channel item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        // Retrieve user and profile information
                        User user = userService.getUserById(item.getUser2Id());
                        String url = userService.getProfilePictureById(item.getUser2Id()); // Fetch profile picture URL

                        // Load profile picture
                        ImageView profileImage = new ImageView();
                        profileImage.setImage(new Image(url, 40, 40, true, true));
                        profileImage.setFitHeight(79);
                        profileImage.setFitWidth(56);
                        profileImage.setPreserveRatio(true);

                        // Create labels for username and status
                        Label usernameLabel = new Label(user.getName());
                        usernameLabel.setFont(new Font("Ebrima Bold", 20));
                        usernameLabel.setTextFill(Color.web("#5b3a29"));
                        VBox.setMargin(usernameLabel, new Insets(20, 0, 0, 10));

                        Label statusLabel = new Label("You: Non"); // Placeholder for chat status
                        statusLabel.setFont(new Font("Ebrima Bold", 20));
                        statusLabel.setTextFill(Color.web("#9e9e9e"));
                        VBox.setMargin(statusLabel, new Insets(0, 0, 0, 10));

                        VBox userInfoBox = new VBox(usernameLabel, statusLabel);
                        userInfoBox.setPrefWidth(207);

                        // Arrange elements in HBox
                        HBox channelLayout = new HBox(profileImage, userInfoBox);
                        channelLayout.setSpacing(10);
                        channelLayout.setPrefSize(252, 103);
                        channelLayout.setPadding(new Insets(5, 0, 0, 0));

                        // Set the custom layout
                        setGraphic(channelLayout);
                    }
                }
            });
        } else {
            System.out.println("No channels found.");
        }

        sendButton.setOnMouseClicked(event -> sendMessage());
        deleteChannelButton.setOnAction(event -> deleteSelectedChannel());
    }
    @FXML
    private void deleteSelectedChannel() {
        Channel selectedChannel = channelListView.getSelectionModel().getSelectedItem();

        if (selectedChannel == null) {
            System.out.println("No channel selected.");
            return;
        }

        // Confirm before deletion (optional)
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this channel?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            channelService.delete(selectedChannel.getId()); // Call the service to delete
            channelListView.getItems().remove(selectedChannel); // Remove from ListView
        }
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
                messageService.delete(message.getIdMessage());  // Call delete method
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

        // Filter and display messages for the selected channel
        for (Message message : messages) {
            if (message.getIdChannel().equals(channelId)) {
                messageListView.getItems().add(message.getContent());
            }
        }
    }
    private Channel findChannelByUserId(Long userId) {
        // Iterate over the list of channels and find the channel that has the userId
        for (Channel channel : channelListView.getItems()) {
            if (channel.getUser2Id().equals(userId)) {
                return channel;
            }
        }
        return null;  // Return null if no channel is found for this user
    }


    @FXML
    private void handleUserClick(ActionEvent event) {
        // Get the selected username from the ComboBox
        String selectedName = (String) searchBox.getValue();

        if (selectedName != null) {
            // Find the corresponding user object from the list
            User selectedUser = userService.getall().stream()
                    .filter(user -> user.getName().equals(selectedName))
                    .findFirst()
                    .orElse(null);

            if (selectedUser != null) {
                this.selectedUser = selectedUser;  // Set the selected user

                // Perform logic to find the corresponding channel for this user
                Channel selectedChannel = findChannelByUserId(selectedUser.getId());
                if (selectedChannel != null) {
                    // Select the channel in the channelListView
                    channelListView.getSelectionModel().select(selectedChannel);
                    // Optionally, you can call the displayMessages method to show the channel's messages
                    List<Message> messages = messageService.getMessagesByChannelId(selectedChannel.getId());
                    displayMessages(messages, selectedChannel.getId());
                } else {
                    showAlert(Alert.AlertType.ERROR, "Channel Error", "No channel found for this user.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "User Error", "User not found.");
            }
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
