package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Callback;

public class ChannelController {

    @FXML
    private ListView<ChannelItem> channelListView;

    public void initialize() {
        // Example data
        channelListView.getItems().addAll(
                new ChannelItem("Ashref", "You: Merci", "img/ashref photo.png", true),
                new ChannelItem("Nour", "Salut !", "img/nour.png", false)
        );

        // Set custom cell factory
        channelListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<ChannelItem> call(ListView<ChannelItem> listView) {
                return new ChannelListCell();
            }
        });
    }

    // Model class for channel item
    public static class ChannelItem {
        String username;
        String lastMessage;
        String imagePath;
        boolean hasUnreadMessages;

        public ChannelItem(String username, String lastMessage, String imagePath, boolean hasUnreadMessages) {
            this.username = username;
            this.lastMessage = lastMessage;
            this.imagePath = imagePath;
            this.hasUnreadMessages = hasUnreadMessages;
        }
    }

    // Custom ListCell to display HBox with profile image, username, and message
    private static class ChannelListCell extends ListCell<ChannelItem> {
        private final HBox hbox = new HBox();
        private final ImageView profileImage = new ImageView();
        private final VBox vbox = new VBox();
        private final Label usernameLabel = new Label();
        private final Label lastMessageLabel = new Label();
        private final Circle unreadIndicator = new Circle(6, Color.RED);

        public ChannelListCell() {
            profileImage.setFitHeight(55);
            profileImage.setFitWidth(55);
            profileImage.setPreserveRatio(true);

            usernameLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #5b3a29;");
            lastMessageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #9e9e9e;");
            unreadIndicator.setVisible(false);

            vbox.getChildren().addAll(usernameLabel, lastMessageLabel);
            hbox.getChildren().addAll(profileImage, vbox, unreadIndicator);
            hbox.setSpacing(10);
        }

        @Override
        protected void updateItem(ChannelItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                usernameLabel.setText(item.username);
                lastMessageLabel.setText(item.lastMessage);
                profileImage.setImage(new Image(item.imagePath));
                unreadIndicator.setVisible(item.hasUnreadMessages);
                setGraphic(hbox);
            }
        }
    }
}
