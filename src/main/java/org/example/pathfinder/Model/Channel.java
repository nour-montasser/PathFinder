package org.example.pathfinder.Model;

import java.util.ArrayList;
import java.util.List;

public class Channel {
    private int id;
    private Integer user1Id; // Using Integer to allow null values
    private Integer user2Id; // Using Integer to allow null values
    private List<Message> messages; // Assuming you have a list of messages for each channel

    // Constructor, getters, and setters

    public Channel(int id) {
        this.id = id;
        this.messages = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(Integer user1Id) {
        this.user1Id = user1Id;
    }

    public Integer getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(Integer user2Id) {
        this.user2Id = user2Id;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
