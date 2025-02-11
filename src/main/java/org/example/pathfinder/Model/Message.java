package org.example.pathfinder.Model;

import java.sql.Timestamp;


public class Message {
    private Long id_message;         // Message ID
    private String content;          // Content of the message
    private Long id_user_sender;     // ID of the user who sent the message
    private String media;            // Optional media (like a file or image URL)
    private Timestamp timesent;        // Timestamp of when the message was sent (using Instant for better time handling)
    private Long id_channel;         // ID of the channel where the message was sent

    // Constructor
    public Message( String content,Long id_message, Long id_user_sender, String media, Long id_channel) {
        this.id_message = id_message;
        this.content = content;
        this.id_user_sender = id_user_sender;
        this.media = media;
         this.timesent = new Timestamp(System.currentTimeMillis()); // default current timestamp

        this.id_channel = id_channel;
    }



    // Getters and Setters
    public Long getIdMessage() {
        return id_message;
    }

    public void setIdMessage(Long id_message) {
        this.id_message = id_message;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getIdUserSender() {
        return id_user_sender;
    }

    public void setIdUserSender(Long id_user_sender) {
        this.id_user_sender = id_user_sender;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public Timestamp getTimesent() {
        return timesent;
    }

    public void setTimesent(Timestamp timesent) {
        this.timesent = timesent;
    }

    public Long getIdChannel() {
        return id_channel;
    }

    public void setIdChannel(Long id_channel) {
        this.id_channel = id_channel;
    }

    // ToString method for easy representation of the Message object
    @Override
    public String toString() {
        return "Message{id_message=" + id_message +
                ", content='" + content + '\'' +
                ", id_user_sender=" + id_user_sender +
                ", media='" + media + '\'' +
                ", timesent=" + timesent +
                ", id_channel=" + id_channel +
                '}';
    }

}
