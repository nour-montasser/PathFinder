package org.example.pathfinder.Controller;
import javafx.fxml.FXML;

import javafx.scene.control.Button;

import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.pathfinder.Model.Channel;
import org.example.pathfinder.Service.ChannelService;

import javafx.scene.control.Button;


public class ChannelBackOfficeController {
    @FXML
    private ListView<String> channelListView; // ListView for Channels
    @FXML
    private Button deleteButton;

    private final ChannelService channelService = new ChannelService();
    private ObservableList<String> channelList;

    @FXML
    public void initialize() {
        loadChannels();
    }

    private void loadChannels() {
        channelList = FXCollections.observableArrayList();
        for (Channel channel : channelService.getall(null)) {
            String channelText = String.format("ID: %d | User1: %d | User2: %d | Rating: %d",
                    channel.getId(), channel.getUser1Id(), channel.getUser2Id());
            channelList.add(channelText);
        }
        channelListView.setItems(channelList);
    }

    @FXML
    private void deleteSelectedChannel() {
        String selectedChannelText = channelListView.getSelectionModel().getSelectedItem();
        if (selectedChannelText != null) {
            String[] parts = selectedChannelText.split("\\|");
            long channelId = Long.parseLong(parts[0].split(":")[1].trim());
            channelService.delete(channelId);
            channelList.remove(selectedChannelText);
        } else {
            showAlert("No selection", "Please select a channel to delete.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
