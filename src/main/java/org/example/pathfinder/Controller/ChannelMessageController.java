package org.example.pathfinder.Controller;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import java.sql.Timestamp;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.example.pathfinder.App.Client;
import org.example.pathfinder.App.ClientListener;
import org.example.pathfinder.Model.Channel;
import org.example.pathfinder.Model.LoggedUser;
import org.example.pathfinder.Model.Message;
import org.example.pathfinder.Model.User;
import org.example.pathfinder.Service.ApplicationService;
import org.example.pathfinder.Service.ChannelService;
import org.example.pathfinder.Service.MessageService;
import org.example.pathfinder.Service.UserService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javafx.scene.control.Alert;
public class ChannelMessageController {
    private static final String API_URL = "https://api-inference.huggingface.co/models/deepseek-ai/DeepSeek-R1-Distill-Qwen-32B";
    private static final String API_KEY = "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";
    @FXML
    private ListView<String> suggestionList;
    private VBox searchContainer;
    private boolean isProcessingKey = false;


    @FXML
    public TextField searchBox;

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

    private ListView<Message> messageListView;  // Change from ListView<String> to ListView<Message>
    private MessageService messageService;
    private User selectedUser;  // Currently selected user
    private Long currentChannelId;
    private UserService userService;
    private Long loggedInUserId= LoggedUser.getInstance().getUserId();

    public ChannelMessageController() {
        messageService = new MessageService();
        userController = new UserController();
        channelService = new ChannelService();
        userService = new UserService(); // Ensure the UserService is initialized
    }

    @FXML
    private void initialize() {
        setupSearchBox();

        setupMessageInput();

        messageListView.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-control-inner-background: transparent;" +
                        "-fx-background-insets: 0;" +
                        "-fx-padding: 0;"
        );


        messageListView.getStyleClass().add("no-hover-list-view");
        VBox.setVgrow(messageListView, Priority.ALWAYS);
        channelListView.setStyle("-fx-background-insets: 0; -fx-padding: 0;");
        channelListView.setFixedCellSize(Region.USE_COMPUTED_SIZE);

        // Scroll to bottom when items change
        messageListView.getItems().addListener((ListChangeListener<Message>) change -> {
            if (messageListView.getItems().size() > 0) {
                messageListView.scrollTo(messageListView.getItems().size() - 1);
            }
        });


        // Fetch channels for current user only
        List<Channel> channelsList = channelService.getall(loggedInUserId);
        ObservableList<Channel> channelsObservableList = FXCollections.observableArrayList(channelsList);
        channelListView.setItems(channelsObservableList);

        // Channel ListView mouse click event
        channelListView.setOnMouseClicked(event -> {
            Channel selectedChannel = channelListView.getSelectionModel().getSelectedItem();
            if (selectedChannel != null) {
                // Get the other user's ID (not the current user)
                Long otherUserId = selectedChannel.getUser1Id().equals(loggedInUserId) ?
                        selectedChannel.getUser2Id() : selectedChannel.getUser1Id();

                User selectedUser = userService.getUserById(otherUserId);
                if (selectedUser != null) {
                    this.selectedUser = selectedUser;
                    userLabel.setText(selectedUser.getName());
                    userLabel.setFont(new Font("Ebrima Bold", 30));
                    userLabel.setUnderline(true);

                    List<Message> messages = messageService.getMessagesByChannelId(selectedChannel.getId());
                    displayMessages(messages, selectedChannel.getId());
                } else {
                    showAlert(Alert.AlertType.ERROR, "User Error", "User not found for the selected channel.");
                }
            }
        });


        // Set up search box
        searchBox.setPromptText("Search users...");
        searchBox.setOnAction(this::handleUserSearch);

        // Set up cell factory for channel list
        channelListView.setCellFactory(listView -> new ListCell<Channel>() {
            private String truncateMessage(String message, int maxLength) {
                if (message == null || message.length() <= maxLength) {
                    return message;
                }
                return message.substring(0, maxLength - 3) + "...";
            }


            @Override
            protected void updateItem(Channel item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    // Get the other user's ID (not the current user)
                    Long otherUserId = item.getUser1Id().equals(loggedInUserId) ?
                            item.getUser2Id() : item.getUser1Id();

                    User user = userService.getUserById(otherUserId);

                    // Create profile image
                    ImageView profileImage = new ImageView();
                    String defaultImagePath = "/org/example/pathfinder/view/Sources/pathfinder_logo_compass.png";

                    try {
                        String userImagePath = user.getImage();
                        if (userImagePath != null && !userImagePath.isEmpty()) {
                            // Try to load user's image from resources
                            InputStream imageStream = getClass().getResourceAsStream(userImagePath);
                            if (imageStream != null) {
                                profileImage.setImage(new Image(imageStream));
                            } else {
                                // Fallback to default image
                                InputStream defaultStream = getClass().getResourceAsStream(defaultImagePath);
                                if (defaultStream != null) {
                                    profileImage.setImage(new Image(defaultStream));
                                }
                            }
                        } else {
                            // Load default image
                            InputStream defaultStream = getClass().getResourceAsStream(defaultImagePath);
                            if (defaultStream != null) {
                                profileImage.setImage(new Image(defaultStream));
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading image: " + e.getMessage());
                        // Try one last time with default image
                        try {
                            InputStream defaultStream = getClass().getResourceAsStream(defaultImagePath);
                            if (defaultStream != null) {
                                profileImage.setImage(new Image(defaultStream));
                            }
                        } catch (Exception ex) {
                            System.err.println("Error loading default image: " + ex.getMessage());
                        }
                    }

                    // Configure profile image
                    profileImage.setFitHeight(79);
                    profileImage.setFitWidth(56);
                    profileImage.setPreserveRatio(true);

                    // Create labels
                    Label usernameLabel = new Label(user.getName());
                    usernameLabel.setFont(new Font("Ebrima Bold", 20));
                    usernameLabel.setTextFill(Color.web("#5b3a29"));
                    VBox.setMargin(usernameLabel, new Insets(20, 0, 0, 10));

                    // Status label
                    Label statusLabel = new Label();
                    List<Message> channelMessages = messageService.getMessagesByChannelId(item.getId());
                    if (!channelMessages.isEmpty()) {
                        Message lastMessage = channelMessages.get(channelMessages.size() - 1);
                        String messageContent = truncateMessage(lastMessage.getContent(), 20);

                        if (Objects.equals(lastMessage.getIdUserSender(), loggedInUserId)) {
                            statusLabel.setText("You: " + messageContent);
                        } else {
                            User sender = userService.getUserById(lastMessage.getIdUserSender());
                            String senderName = sender != null ? sender.getName() : "Unknown";
                            statusLabel.setText(senderName + ": " + messageContent);
                        }
                    } else {
                        statusLabel.setText("No messages yet");
                    }
                    statusLabel.setFont(new Font("Ebrima Bold", 20));
                    statusLabel.setTextFill(Color.web("#9e9e9e"));
                    VBox.setMargin(statusLabel, new Insets(0, 0, 0, 10));

                    // Layout
                    VBox userInfoBox = new VBox(usernameLabel, statusLabel);
                    userInfoBox.setPrefWidth(207);

                    HBox channelLayout = new HBox(profileImage, userInfoBox);
                    channelLayout.setSpacing(10);
                    channelLayout.setPrefSize(252, 103);
                    channelLayout.setPadding(new Insets(5, 0, 0, 0));

                    setGraphic(channelLayout);
                }
            }

        });


        // Set up button handlers
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

    public void deleteMessage(ActionEvent event) {
        Message selectedMessage = messageListView.getSelectionModel().getSelectedItem();
        if (selectedMessage == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a message to delete.");
            return;
        }

        try {
            messageService.delete(selectedMessage.getIdMessage());
            messageListView.getItems().remove(selectedMessage);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Message deleted successfully.");
            System.out.println("Message deleted: " + selectedMessage.getIdMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete message: " + e.getMessage());
            System.err.println("Error deleting message: " + e.getMessage());
        }
    }    private boolean isUpdateMode = false;
    private Message messageToUpdate;


    private String generateAIResponse(String question) {
        try {
            JSONObject requestData = new JSONObject();
            requestData.put("inputs", question);

            // Create URL object and connection
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Configure connection
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Send request
            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestData.toString().getBytes());
                os.flush();
            }

            // Read response
            StringBuilder response = new StringBuilder(); // Fixed: Declare StringBuilder
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
            }

            JSONArray responseArray = new JSONArray(response.toString());
            String generatedText = responseArray.getJSONObject(0).getString("generated_text").trim();

            // Extract only the content after </think>
            int thinkTagIndex = generatedText.indexOf("</think>");
            if (thinkTagIndex != -1) {
                return generatedText.substring(thinkTagIndex + 8).trim();
            }
            return generatedText;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating AI response: " + e.getMessage();
        }
    }    private void handleUpdateMessage(String content) {
        if (messageToUpdate == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No message selected for update.");
            return;
        }

        try {
            messageToUpdate.setContent(content);
            messageService.update(messageToUpdate);

            // Refresh the messages
            Long channelId = messageService.getChannelIdBetweenUsers(loggedInUserId, selectedUser.getId());
            List<Message> messages = messageService.getMessagesByChannelId(channelId);
            messageListView.getItems().setAll(messages);

            // Clear update mode
            messageInput.clear();
            isUpdateMode = false;
            messageToUpdate = null;

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update message: " + e.getMessage());
        }
    }

    @FXML
    private void sendMessage() {
        String content = messageInput.getText().trim();
        if (content.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Message cannot be empty.");
            return;
        }

        if (isUpdateMode) {
            handleUpdateMessage(content);
            return;
        }

        Long channelId = messageService.getChannelIdBetweenUsers(loggedInUserId, selectedUser.getId());
        if (channelId == null) {
            showAlert(Alert.AlertType.ERROR, "Channel Error", "No channel found between selected users.");
            return;
        }

        // Create and set timestamp for the message
        Message message = new Message(content, loggedInUserId, selectedUser.getId(), "text", channelId);
        message.setTimesent(new Timestamp(System.currentTimeMillis())); // Set the timestamp

        try {
            // Save message with timestamp
            messageService.add(message);

            // Handle AI response if needed
            if (content.startsWith("/pathfinderAI")) {
                String question = content.substring("/pathfinderAI".length()).trim();
                if (!question.isEmpty()) {
                    String aiResponse = generateAIResponse(question + "Answer this question keep it short!");
                    Message aiMessage = new Message(aiResponse, loggedInUserId, selectedUser.getId(), "text", channelId);
                    aiMessage.setTimesent(new Timestamp(System.currentTimeMillis())); // Set timestamp for AI message
                    messageService.add(aiMessage);
                }
            }

            // Refresh messages
            List<Message> messages = messageService.getMessagesByChannelId(channelId);
            messageListView.getItems().setAll(messages);
            messageListView.scrollTo(messageListView.getItems().size() - 1);
            messageInput.clear();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to send message: " + e.getMessage());
        }
    }@FXML
    public void updateMessage(ActionEvent event) {
        if (!isUpdateMode) {
            Message selectedMessage = messageListView.getSelectionModel().getSelectedItem();
            if (selectedMessage == null) {
                showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a message to update.");
                return;
            }

            // Check if the message belongs to the current user
            if (!selectedMessage.getIdUserSender().equals(loggedInUserId)) {
                showAlert(Alert.AlertType.ERROR, "Permission Error", "You can only update your own messages.");
                return;
            }

            // Enter update mode
            isUpdateMode = true;
            messageToUpdate = selectedMessage;
            messageInput.setText(selectedMessage.getContent());
            messageInput.requestFocus();
            System.out.println("Entering update mode for message ID: " + selectedMessage.getIdMessage());
        } else {
            try {
                messageToUpdate.setContent(messageInput.getText().trim());
                messageService.update(messageToUpdate);
                messageListView.refresh();
                messageInput.clear();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Message updated successfully.");
                System.out.println("Message updated: " + messageToUpdate.getIdMessage());
                isUpdateMode = false;
                messageToUpdate = null;
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating the message.");
                System.err.println("Error updating message: " + e.getMessage());
            }
        }
    }
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private final Map<Long, Image> avatarCache = new ConcurrentHashMap<>();
    private final Image defaultAvatar = loadDefaultAvatar();

    private Image loadDefaultAvatar() {
        try (InputStream stream = getClass().getResourceAsStream("/org/example/pathfinder/view/Sources/pathfinder_logo_compass.png.png")) {
            if (stream == null) {
                System.err.println("Could not find default avatar image");
                return null;
            }
            return new Image(stream);
        } catch (Exception e) {
            System.err.println("Error loading default avatar: " + e.getMessage());
            return null;
        }
    }
    private void displayMessages(List<Message> messages, Long channelId) {
        messageListView.setFixedCellSize(Region.USE_COMPUTED_SIZE);
        messageListView.setCellFactory(param -> new ListCell<Message>() {
            private final HBox messageContainer = new HBox(10);
            private final VBox messageBox = new VBox();
            private final Label contentLabel = new Label();
            private final MenuButton menuButton = new MenuButton("⋮");
            private final ImageView profilePicture = new ImageView();

            {
                contentLabel.setWrapText(true);
                contentLabel.setMaxWidth(300);
                contentLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);
                contentLabel.setMinWidth(50);
                contentLabel.setPadding(new Insets(8));

                // Configure profile picture
                profilePicture.setFitHeight(40);
                profilePicture.setFitWidth(40);
                profilePicture.setPreserveRatio(true);

                // Make profile picture circular
                Circle clip = new Circle(20, 20, 20);
                profilePicture.setClip(clip);

                messageBox.setPadding(new Insets(4, 8, 4, 8));
                messageContainer.setSpacing(8);
                messageContainer.setMaxWidth(Region.USE_PREF_SIZE);
                messageContainer.setAlignment(Pos.CENTER_LEFT);

                menuButton.getStyleClass().add("message-menu");
                menuButton.setVisible(false);

                messageContainer.setOnMouseEntered(e -> menuButton.setVisible(true));
                messageContainer.setOnMouseExited(e -> {
                    if (!menuButton.isShowing()) {
                        menuButton.setVisible(false);
                    }
                });

                contentLabel.setTextOverrun(OverrunStyle.CLIP);
            }

            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                setText(null);

                if (empty || message == null) {
                    setGraphic(null);
                    return;
                }

                contentLabel.setText(message.getContent());
                messageContainer.getChildren().clear();
                messageBox.getChildren().clear();
                menuButton.getItems().clear();

                if (message.getIdUserSender().equals(loggedInUserId)) {
                    setupSenderMessage(message);
                } else {
                    setupReceiverMessage(message);
                }

                messageBox.getChildren().add(messageContainer);
                setGraphic(messageBox);
            }

            private void setupSenderMessage(Message message) {
                messageBox.setAlignment(Pos.CENTER_RIGHT);
                messageContainer.setAlignment(Pos.CENTER_RIGHT);
                contentLabel.setStyle("-fx-background-color: #0084ff; -fx-text-fill: white; -fx-background-radius: 15px;");

                // Create time label
                Label timeLabel = new Label(formatTimestamp(message.getTimesent()));
                timeLabel.setStyle("-fx-text-fill: #9e9e9e; -fx-font-size: 12px;");

                // Create VBox for message and time
                VBox messageAndTime = new VBox(2); // 2 pixels spacing
                messageAndTime.setAlignment(Pos.CENTER_RIGHT);
                messageAndTime.getChildren().addAll(contentLabel, timeLabel);

                // Add menu items
                MenuItem updateItem = new MenuItem("Update");
                MenuItem deleteItem = new MenuItem("Delete");

                updateItem.setOnAction(e -> {
                    getListView().getSelectionModel().select(getIndex());
                    updateMessage(new ActionEvent());
                });

                deleteItem.setOnAction(e -> {
                    getListView().getSelectionModel().select(getIndex());
                    deleteMessage(new ActionEvent());
                });

                menuButton.getItems().addAll(updateItem, deleteItem);
                messageContainer.getChildren().clear(); // Clear existing children
                messageContainer.getChildren().addAll(messageAndTime, menuButton);
            }            private void setupReceiverMessage(Message message) {
                messageBox.setAlignment(Pos.CENTER_LEFT);
                messageContainer.setAlignment(Pos.CENTER_LEFT);
                contentLabel.setStyle("-fx-background-color: #e9ecef; -fx-text-fill: black; -fx-background-radius: 15px;");

                // Create time label
                Label timeLabel = new Label(formatTimestamp(message.getTimesent()));
                timeLabel.setStyle("-fx-text-fill: #9e9e9e; -fx-font-size: 12px;");

                // Create VBox for message and time
                VBox messageAndTime = new VBox(2); // 2 pixels spacing
                messageAndTime.setAlignment(Pos.CENTER_LEFT);
                messageAndTime.getChildren().addAll(contentLabel, timeLabel);

                // Load profile picture
                ApplicationService applicationService = new ApplicationService();
                String url = applicationService.getUserProfilePicture(message.getIdUserSender());

                if (url == null || url.isEmpty()) {
                    url = "/Sources/pathfinder_logo_compass.png";
                }

                try {
                    String finalUrl = url.startsWith("/") ? url : "/" + url;
                    InputStream stream = getClass().getResourceAsStream(finalUrl);

                    if (stream != null) {
                        profilePicture.setImage(new Image(stream));
                    } else {
                        InputStream defaultStream = getClass().getResourceAsStream("/Sources/pathfinder_logo_compass.png");
                        if (defaultStream != null) {
                            profilePicture.setImage(new Image(defaultStream));
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error loading profile picture: " + e.getMessage());
                }

                profilePicture.setFitHeight(40);
                profilePicture.setFitWidth(40);
                profilePicture.setPreserveRatio(true);

                Circle clip = new Circle(20, 20, 20);
                profilePicture.setClip(clip);

                messageContainer.getChildren().clear();
                messageContainer.getChildren().addAll(profilePicture, messageAndTime);
            }
            private String formatTimestamp(java.sql.Timestamp timestamp) {
                if (timestamp == null) return "";

                LocalDateTime messageTime = LocalDateTime.of(
                        timestamp.getYear() + 1900,
                        timestamp.getMonth() + 1,
                        timestamp.getDate(),
                        timestamp.getHours(),
                        timestamp.getMinutes()
                );
                LocalDateTime now = LocalDateTime.now();

                // If message is from today, show only time
                if (messageTime.toLocalDate().equals(now.toLocalDate())) {
                    return messageTime.format(DateTimeFormatter.ofPattern("HH:mm"));
                }

                // If message is from this year, show date without year
                if (messageTime.getYear() == now.getYear()) {
                    return messageTime.format(DateTimeFormatter.ofPattern("MMM d, HH:mm"));
                }

                // For older messages, show full date and time
                return messageTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm"));
            }        });

        messageListView.getItems().setAll(messages.stream()
                .filter(m -> m != null && m.getIdChannel().equals(channelId))
                .collect(Collectors.toList()));

        Platform.runLater(() -> {
            if (!messages.isEmpty()) {
                messageListView.scrollTo(messages.size() - 1);
            }
        });
    }

    private void configureMenuButton(MenuButton menuButton, Message message) {
        menuButton.getItems().clear();

        MenuItem updateItem = new MenuItem("Update");
        MenuItem deleteItem = new MenuItem("Delete");

        // Set up update action
        updateItem.setOnAction(e -> {
            messageListView.getSelectionModel().select(message);
            updateMessage(new ActionEvent());
            menuButton.setVisible(false); // Hide after action
        });

        // Set up delete action
        deleteItem.setOnAction(e -> {
            messageListView.getSelectionModel().select(message);
            deleteMessage(new ActionEvent());
            menuButton.setVisible(false); // Hide after action
        });

        menuButton.getItems().addAll(updateItem, deleteItem);

        // Menu visibility handling
        VBox container = (VBox) menuButton.getParent().getParent();
        container.setOnMouseEntered(e -> menuButton.setVisible(true));

        // Remove the mouse exit handler
        container.setOnMouseExited(null);

        // Toggle visibility on menu button click
        menuButton.setOnMouseClicked(e -> {
            if (!menuButton.isShowing()) {
                menuButton.setVisible(!menuButton.isVisible());
            }
        });

        // Initial state
        menuButton.setVisible(false);
        menuButton.getStyleClass().add("message-menu");
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
    private void handleUserSearch(ActionEvent event) {
        String searchText = searchBox.getText().trim();

        if (searchText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Search Error", "Please enter a username to search.");
            return;
        }

        try {
            User selectedUser = userService.getAllUsers().stream()
                    .filter(user -> user.getName().equalsIgnoreCase(searchText) && user.getId() != loggedInUserId)
                    .findFirst()
                    .orElse(null);

            if (selectedUser == null) {
                showAlert(Alert.AlertType.ERROR, "User Error", "User not found.");
                return;
            }

            this.selectedUser = selectedUser;
            Channel existingChannel = channelService.getChannelBetweenUsers(loggedInUserId, selectedUser.getId());

            if (existingChannel == null) {
                // Create new channel
                Channel newChannel = new Channel();
                newChannel.setUser1Id(loggedInUserId);
                newChannel.setUser2Id(selectedUser.getId());
                channelService.add(newChannel);

                // Refresh channel list
                List<Channel> updatedChannels = channelService.getall(loggedInUserId);
                channelListView.setItems(FXCollections.observableArrayList(updatedChannels));
            } else {
                channelListView.getSelectionModel().select(existingChannel);
                List<Message> messages = messageService.getMessagesByChannelId(existingChannel.getId());
                displayMessages(messages, existingChannel.getId());
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        searchBox.clear();
    }
    private ObservableList<String> suggestions = FXCollections.observableArrayList();

    private void updateSuggestions(String searchText) {
        // Clear the current suggestions
        suggestions.clear();

        // Get all users except current user
        List<String> filteredUsers = userService.getAllUsers()
                .stream()
                .filter(user -> user.getId() != loggedInUserId)
                .map(User::getName)
                .collect(Collectors.toList());

        // If search text is empty, show all users
        if (searchText == null || searchText.trim().isEmpty()) {
            suggestions.addAll(filteredUsers);
        } else {
            // Filter users based on search text
            String searchLower = searchText.toLowerCase().trim();
            filteredUsers.stream()
                    .filter(name -> name.toLowerCase().contains(searchLower))
                    .forEach(suggestions::add);
        }

        // Update UI in a more controlled way
        Platform.runLater(() -> {
            // First update the items
            suggestionList.setItems(FXCollections.observableArrayList(suggestions));

            // Then update the visibility and size
            if (suggestions.isEmpty()) {
                hideDropdown();
            } else {
                showDropdown();
            }
        });
    }

    private void showDropdown() {
        suggestionList.setVisible(true);
        suggestionList.setManaged(true);

        // Make list wider than the search box
        suggestionList.setPrefWidth(searchBox.getWidth() * 1.5);

        // Adjust height based on content
        double itemHeight = 30; // Increased height per item
        double totalHeight = Math.min(suggestions.size() * itemHeight, 300); // Increased max height

        suggestionList.setPrefHeight(totalHeight);
        suggestionList.setMinHeight(totalHeight);
        suggestionList.setMaxHeight(totalHeight);
        suggestionList.toFront();
    }
    private void hideDropdown() {
        suggestionList.setVisible(false);
        suggestionList.setManaged(false);
        suggestionList.setPrefHeight(0);
        suggestionList.setMinHeight(0);
    }
    private void setupMessageInput() {
        messageInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !event.isShiftDown()) {
                event.consume(); // Prevent new line
                sendMessage();
            }
        });
    }

    private void setupSearchBox() {
        suggestionList.setPrefWidth(searchBox.getPrefWidth());
        suggestionList.setMaxHeight(200);
        suggestionList.setItems(suggestions);
        suggestionList.setVisible(false);
        suggestionList.setManaged(false);
        suggestionList.getStyleClass().add("suggestion-list");

        // Show suggestions when clicking on search box
        searchBox.setOnMouseClicked(event -> {
            updateSuggestions(searchBox.getText());
            showDropdown();
        });

        // Text change listener
        searchBox.textProperty().addListener((observable, oldValue, newValue) -> {
            if (suggestionList.isVisible()) {
                updateSuggestions(newValue);
            }
        });

        // Mouse click handler
        suggestionList.setOnMouseClicked(event -> {
            String selected = suggestionList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                searchBox.setText(selected);
                hideDropdown();
                handleUserSearch(new ActionEvent());
            }
        });

        // Key handler
        searchBox.setOnKeyPressed(event -> {
            if (!suggestions.isEmpty()) {
                switch (event.getCode()) {
                    case DOWN:
                        suggestionList.getSelectionModel().selectFirst();
                        suggestionList.requestFocus();
                        event.consume();
                        break;
                    case ENTER:
                        String selected = suggestionList.getSelectionModel().getSelectedItem();
                        if (selected != null) {
                            searchBox.setText(selected);
                            hideDropdown();
                            handleUserSearch(new ActionEvent());
                        }
                        event.consume();
                        break;
                    case ESCAPE:
                        searchBox.clear();
                        hideDropdown();
                        event.consume();
                        break;
                }
            }
        });

        // Focus lost handler
        searchBox.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                PauseTransition delay = new PauseTransition(Duration.millis(200));
                delay.setOnFinished(event -> hideDropdown());
                delay.play();
            }
        });
    }
    private void configureMenuItems(MenuButton menuButton, Message message) {
        MenuItem updateItem = new MenuItem("Update");
        MenuItem deleteItem = new MenuItem("Delete");

        updateItem.setOnAction(e -> {
            messageListView.getSelectionModel().select(message);
            updateMessage(new ActionEvent());
        });

        deleteItem.setOnAction(e -> {
            messageListView.getSelectionModel().select(message);
            deleteMessage(new ActionEvent());
        });

        menuButton.getItems().clear();
        menuButton.getItems().addAll(updateItem, deleteItem);
    }
    public void setFreelancerId(int userId) {
        this.selectedUser = userService.getUserById((long) userId); // Convert int to Long

        if (this.selectedUser != null) {
            userLabel.setText(selectedUser.getName());
            System.out.println("✅ User ID set: " + userId);
        } else {
            System.out.println("❌ User not found with ID: " + userId);
        }
    }



}
