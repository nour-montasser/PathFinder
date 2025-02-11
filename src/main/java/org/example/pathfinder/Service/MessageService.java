package org.example.pathfinder.Service;

import org.example.pathfinder.Model.Message;
import org.example.pathfinder.App.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageService implements Services<Message> {
    private Connection cnx;

    public MessageService() {
        cnx = DatabaseConnection.getInstance().getCnx();
    }

    @Override
    public void add(Message message) {
        // Debugging: print content before adding
        System.out.println("Attempting to add message with content: '" + message.getContent() + "'");

        // Validate content
        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty.");
        }

        String req = "INSERT INTO Message (content, id_user_sender, media, timesent, id_channel) VALUES (?, ?, ?, ?, ?)";

        try {
            // Prepare the statement
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setString(1, message.getContent()); // content
            stm.setLong(2, message.getIdUserSender()); // id_user_sender
            stm.setString(3, message.getMedia()); // media
            stm.setTimestamp(4, message.getTimesent()); // timesent
            stm.setLong(5, message.getIdChannel()); // id_channel

            // Execute the update
            int rowsAffected = stm.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Message sent successfully.");
            } else {
                System.out.println("Message insertion failed.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error adding message: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            System.out.println("Input validation failed: " + e.getMessage());
        }
    }



    @Override
    public void update(Message message) {
        String req = "UPDATE Message SET content = ?, media = ?, timesent = ? WHERE id_message = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setString(1, message.getContent());
            stm.setString(2, message.getMedia());
            stm.setTimestamp(3, message.getTimesent());
            stm.setLong(4, message.getIdMessage());
            stm.executeUpdate();
            System.out.println("Message updated successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error updating message: " + e.getMessage(), e);
        }
    }



    @Override
    public void delete(Message message) {
        String req = "DELETE FROM Message WHERE id_message = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setLong(1, message.getIdMessage());
            stm.executeUpdate();
            System.out.println("Message deleted successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting message: " + e.getMessage(), e);
        }
    }

    public List<Message> getall() {
        List<Message> messages = new ArrayList<>();
        String req = "SELECT * FROM Message";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);
            // public Message( String content,Long id_message, Long id_user_sender, String media, Long id_channel)
            while (rs.next()) {
                Message message = new Message(
                        rs.getString("content"),
                        rs.getLong("id_message"),
                        rs.getLong("id_user_sender"),
                        rs.getString("media"),
                        rs.getLong("id_channel")
                );
                message.setIdMessage(rs.getLong("id_message"));
                messages.add(message);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving messages: " + e.getMessage(), e);
        }
        return messages;
    }

    public Message getone() {
        String req = "SELECT * FROM Message LIMIT 1";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);
            if (rs.next()) {
                Message message = new Message(
                        rs.getString("content"),
                        rs.getLong("id_message"),
                        rs.getLong("id_user_sender"),
                        rs.getString("media"),
                        rs.getLong("id_channel")
                );
                message.setIdMessage(rs.getLong("id_message"));
                return message;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving message: " + e.getMessage(), e);
        }
        return null;
    }
}
