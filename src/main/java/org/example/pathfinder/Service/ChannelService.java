package org.example.pathfinder.Service;

import org.example.pathfinder.Model.Channel;
import org.example.pathfinder.Model.Message;

import java.util.List;
import java.util.ArrayList;

public class ChannelService {

    // In-memory list to store channels (replace with your database logic)
    private List<Channel> channels = new ArrayList<>();

    // Temporary static user IDs (use these until the user module is available)
    private static final int DEFAULT_USER1_ID = 1;
    private static final int DEFAULT_USER2_ID = 2;

    // Create a new channel
    public void createChannel(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel must not be null");
        }

        // If user IDs are not provided, use the default values
        if (channel.getUser1Id() == null) {
            channel.setUser1Id(DEFAULT_USER1_ID);
        }
        if (channel.getUser2Id() == null) {
            channel.setUser2Id(DEFAULT_USER2_ID);
        }

        channels.add(channel); // Simulate saving to a database
    }

    // Get all channels
    public List<Channel> getAllChannels() {
        return channels;
    }

    // Get a channel by ID
    public Channel getChannelById(int channelId) {
        return channels.stream()
                .filter(channel -> channel.getId() == channelId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Channel not found with ID: " + channelId));
    }

    // Add a message to a channel
    public void addMessageToChannel(int channelId, Message message) {
        Channel channel = getChannelById(channelId);
        channel.getMessages().add(message); // Assuming Channel has a list of messages
    }

    // Update a channel
    public void updateChannel(Channel updatedChannel) {
        Channel existingChannel = getChannelById(updatedChannel.getId());
        existingChannel.setUser1Id(updatedChannel.getUser1Id());
        existingChannel.setUser2Id(updatedChannel.getUser2Id());
        // Update other fields as necessary
    }

    // Delete a channel by ID
    public void deleteChannel(int channelId) {
        Channel channel = getChannelById(channelId);
        channels.remove(channel); // Simulate deleting from a database
    }
}
