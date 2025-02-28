package org.example.pathfinder.Model;

import java.util.ArrayList;
import java.util.List;

public class Channel {

    private long id;  // Changed to long for consistency with the database type
    private Long user1Id; // Changed to Long to allow null values and consistency with the database
    private Long user2Id; // Changed to Long to allow null values and consistency with the database
    private List<Message> messages; // Assuming you have a list of messages for each channel

    // Constructor accepting id, user1Id, and user2Id
    public Channel(long id, Long user1Id, Long user2Id) {
        this.id = id;
        this.user1Id = user1Id ;
        this.user2Id = user2Id;
        this.messages = new ArrayList<>();  // Initialize the list of messages
    }

    // Constructor for initializing only id (for empty channel creation)

    public Channel() {

    }

    public Channel(long channelId) {
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(Long user1Id) {
        this.user1Id = user1Id;
    }

    public Long getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(Long user2Id) {
        this.user2Id = user2Id;
    }



    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
