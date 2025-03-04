package org.example.pathfinder.Service;

import org.example.pathfinder.App.DatabaseConnection;
import org.example.pathfinder.Model.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageService implements Services<Message> {
    private Connection cnx;

    public MessageService() {
        cnx = DatabaseConnection.getInstance().getCnx();

    }
    private List<Message> allMessages = new ArrayList<>();


    @Override
    public void add(Message message) {
        String req = "INSERT INTO Message (content, id_user_sender, media, timesent, id_channel) VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setString(1, message.getContent());
            stm.setLong(2, message.getIdUserSender());
            stm.setString(3, message.getMedia());
            stm.setTimestamp(4, message.getTimesent());
            stm.setLong(5, message.getIdChannel());

            int rowsAffected = stm.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Message sent successfully.");
            } else {
                System.out.println("Message insertion failed.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error adding message: " + e.getMessage(), e);
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
    public void delete(long id) {
        String req = "DELETE FROM Message WHERE id_message = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setLong(1, id);
            int rowsAffected = stm.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Message deleted successfully.");
            } else {
                System.out.println("Message not found.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting message: " + e.getMessage(), e);
        }
    }




    public List<Message> getall(Long userId) {
        List<Message> messages = new ArrayList<>();
        String req = "SELECT m.* FROM Message m " +
                "INNER JOIN Channel c ON m.id_channel = c.id_channel " +
                "WHERE c.id_user1 = ? OR c.id_user2 = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setLong(1, userId);
            stm.setLong(2, userId);
            ResultSet rs = stm.executeQuery();

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
    @Override
    public List<Message> getall() {
        return null;
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

    public List<Message> getMessagesByChannelId(Long channelId) {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM message WHERE id_channel = ?";

        try (PreparedStatement stm = cnx.prepareStatement(query)) {
            stm.setLong(1, channelId);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                Message message = new Message(
                        rs.getLong("id_message"),     // first parameter is now idMessage
                        rs.getString("content"),
                        rs.getLong("id_user_sender"),
                        rs.getString("media"),
                        rs.getLong("id_channel")
                );
                message.setTimesent(rs.getTimestamp("timesent"));

                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
    public long getChannelIdBetweenUsers(long userId, long selectedUserId) {
        String req = "SELECT id_channel " +
                "FROM Channel " +
                "WHERE (id_user1 = ? AND id_user2 = ?) OR (id_user1 = ? AND id_user2 = ?)";

        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setLong(1, userId);  // Set the first userId
            stm.setLong(2, selectedUserId);  // Set the second selectedUserId
            stm.setLong(3, selectedUserId);  // Set the second userId first
            stm.setLong(4, userId);  // Set the first userId second

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                return rs.getLong("id_channel");  // Return the channelId
            } else {
                throw new RuntimeException("No channel found between the two users.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving channel ID: " + e.getMessage(), e);
        }
    }
    public List<Long> getChannelsForUser(Long userId) {
        List<Long> channels = new ArrayList<>();
        String req = "SELECT id_channel FROM Channel WHERE id_user1 = ? OR id_user2 = ?";

        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setLong(1, userId);
            stm.setLong(2, userId);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                channels.add(rs.getLong("id_channel"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting channels for user: " + e.getMessage(), e);
        }
        return channels;
    }






}
