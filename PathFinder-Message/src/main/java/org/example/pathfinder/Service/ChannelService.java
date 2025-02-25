package org.example.pathfinder.Service;

import org.example.pathfinder.App.DatabaseConnection;
import org.example.pathfinder.Model.Channel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChannelService implements Services<Channel> {
    private Connection cnx;

    public ChannelService() {
        cnx = DatabaseConnection.getInstance().getCnx();
    }

    @Override
    public void add(Channel channel) {
        String query = "INSERT INTO channel (id_user1, id_user2) VALUES (?, ?)";
        try (PreparedStatement stmt = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, channel.getUser1Id());
            stmt.setLong(2, channel.getUser2Id());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    channel.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while creating channel: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Channel channel) {
        String query = "UPDATE channel SET id_user1 = ?, id_user2 = ? WHERE id_channel = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setLong(1, channel.getUser1Id());
            stmt.setLong(2, channel.getUser2Id());
            stmt.setLong(3, channel.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating channel: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(long id) {
        String query = "DELETE FROM channel WHERE id_channel = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            System.out.println("Channel deleted successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting channel: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Channel> getall() {
        List<Channel> channels = new ArrayList<>();
        String query = "SELECT * FROM channel";
        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Channel channel = new Channel(rs.getLong("id_channel"),
                        rs.getLong("id_user1"),
                        rs.getLong("id_user2"));
                channels.add(channel);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving channels: " + e.getMessage(), e);
        }
        return channels;
    }

    @Override
    public Channel getone() {
        String query = "SELECT * FROM channel LIMIT 1";
        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return new Channel(
                        rs.getLong("id_channel"),
                        rs.getLong("id_user1"),
                        rs.getLong("id_user2"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving a channel: " + e.getMessage(), e);
        }
        return null;
    }

    public Channel getChannelBetweenUsers(Long user1Id, Long user2Id) {
        String checkChannelQuery = "SELECT * FROM channel WHERE (id_user1= ? AND id_user2 = ?) OR (id_user1 = ? AND id_user2 = ?)";
        try (PreparedStatement stmt = cnx.prepareStatement(checkChannelQuery)) {
            stmt.setLong(1, user1Id);
            stmt.setLong(2, user2Id);
            stmt.setLong(3, user2Id);
            stmt.setLong(4, user1Id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Channel(
                            rs.getLong("id_channel"),
                            rs.getLong("id_user1"),
                            rs.getLong("id_user2"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long getOrCreateChannel(Long selectedUserId) {
        String checkChannelQuery = "SELECT id_channel FROM channel WHERE (id_user1 = 1 AND id_user2 = ?) OR (id_user1 = ? AND id_user2 = 1)";
        try (PreparedStatement stmt = cnx.prepareStatement(checkChannelQuery)) {
            stmt.setLong(1, selectedUserId);
            stmt.setLong(2, selectedUserId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id_channel");  // Existing channel found
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // If no existing channel, create a new one
        String createChannelQuery = "INSERT INTO channel (id_user1, id_user2) VALUES (1, ?)";
        try (PreparedStatement createStmt = cnx.prepareStatement(createChannelQuery, Statement.RETURN_GENERATED_KEYS)) {
            createStmt.setLong(1, selectedUserId);
            int rowsAffected = createStmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = createStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getLong(1);  // New channel created
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;  // Return null if creation fails
    }

}
