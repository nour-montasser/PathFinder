package org.example.pathfinder.Model;

import java.sql.Timestamp;


public class Channel {
    private Long id_channel;        // Channel ID
    private Long id_user1;          // First user ID
    private Long id_user2;          // Second user ID
    private Long rating;            // Rating (optional)
    private Timestamp timeCreated;    // Timestamp of when the channel was created

    // Constructor
    public Channel(Long id_channel, Long id_user1, Long id_user2, Long rating, Timestamp timeCreated) {
        this.id_channel = id_channel;
        this.id_user1 = id_user1;
        this.id_user2 = id_user2;
        this.rating = rating;
        this.timeCreated = timeCreated;
    }

    // Getters and Setters
    public Long getIdChannel() {
        return id_channel;
    }

    public void setIdChannel(Long id_channel) {
        this.id_channel = id_channel;
    }

    public Long getIdUser1() {
        return id_user1;
    }

    public void setIdUser1(Long id_user1) {
        this.id_user1 = id_user1;
    }

    public Long getIdUser2() {
        return id_user2;
    }

    public void setIdUser2(Long id_user2) {
        this.id_user2 = id_user2;
    }

    public Long getRating() {
        return rating;
    }

    public void setRating(Long rating) {
        this.rating = rating;
    }

    public Timestamp getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Timestamp timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Override
    public String toString() {
        return "Channel{id_channel=" + id_channel +
                ", id_user1=" + id_user1 +
                ", id_user2=" + id_user2 +
                ", rating=" + rating +
                ", timeCreated=" + timeCreated +
                '}';
    }
}
