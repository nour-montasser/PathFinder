package org.example.pathfinder.Model;

import java.sql.Timestamp;

public class Message {
    private Long idMessage;
    private String content;
    private Long idUserSender;
    private String media;
    private Long idChannel;
    private Timestamp timesent;


    // Constructor with ID (for loading from database)
    public Message(Long idMessage, String content, Long idUserSender, String media, Long idChannel) {
        this.idMessage = idMessage;
        this.content = content;
        this.idUserSender = idUserSender;
        this.media = media;
        this.idChannel = idChannel;
    }

    // Constructor for new messages (without ID)
    public Message(String content, Long idUserSender, Long idUserReceiver, String media, Long idChannel) {
        this.content = content;
        this.idUserSender = idUserSender;
        this.media = media;
        this.idChannel = idChannel;
    }

    // Getters and setters
    public Long getIdMessage() { return idMessage; }
    public void setIdMessage(Long idMessage) { this.idMessage = idMessage; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getIdUserSender() { return idUserSender; }
    public void setIdUserSender(Long idUserSender) { this.idUserSender = idUserSender; }

    public String getMedia() { return media; }
    public void setMedia(String media) { this.media = media; }

    public Timestamp getTimesent() { return timesent; }
    public void setTimesent(Timestamp timesent) { this.timesent = timesent; }

    public Long getIdChannel() { return idChannel; }
    public void setIdChannel(Long idChannel) { this.idChannel = idChannel; }
}