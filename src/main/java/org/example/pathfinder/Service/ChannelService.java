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
        String query = "INSERT INTO channels (id_user1,id_user2) VALUES (?, ?)";
        try (PreparedStatement stmt = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, channel.getUser1Id());  // Corrected to Long
            stmt.setLong(2, channel.getUser2Id());  // Corrected to Long

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                long channelId = rs.getLong(1);  // Updated to use Long
                channel.setId(channelId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while creating channel: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Channel channel) {
        String query = "UPDATE channels SET id_user1 = ?, id_user2 = ? WHERE id_channel = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setLong(1, channel.getUser1Id());  // Corrected to Long
            stmt.setLong(2, channel.getUser2Id());  // Corrected to Long
            stmt.setLong(3, channel.getId());  // Corrected to Long

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating channel: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Channel channel) {
        String query = "DELETE FROM channel WHERE id_channel = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setLong(1, channel.getId());  // Corrected to Long
            stmt.executeUpdate();
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
                long channelId = rs.getLong("id_channel");
                long user1Id = rs.getLong("id_user1");
                long user2Id = rs.getLong("id_user2");

                Channel channel = new Channel(channelId, user1Id, user2Id);
                channel.setUser1Id(user1Id);
                channel.setUser2Id(user2Id);

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
                long channelId = rs.getLong("id_channel");
                long user1Id = rs.getLong("id_user1");
                long user2Id = rs.getLong("id_user2");

                Channel channel = new Channel(channelId);
                channel.setUser1Id(user1Id);  // No casting needed
                channel.setUser2Id(user2Id);  // No casting needed

                return channel;
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

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Assuming your Channel class has setters or can be set manually
                Channel channel = new Channel(
                        rs.getLong("id_channel"),
                        rs.getLong("id_user1"),
                        rs.getLong("id_user2")
                );
                channel.setId(rs.getLong("id_channel"));
                channel.setUser1Id(rs.getLong("id_user1"));
                channel.setUser2Id(rs.getLong("id_user2"));

                return channel;  // Return the existing channel
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception (logging, throwing custom exception, etc.)
        }
        return null;  // Return null if no channel is found
    }



    public Long getOrCreateChannel(Long selectedUserId) {
        String checkChannelQuery = "SELECT id_channel FROM channel WHERE (id_user1 = 1 AND id_user2 = ?) OR (id_user1 = ? AND id_user2 = 1)";
        try (PreparedStatement stmt = cnx.prepareStatement(checkChannelQuery)) {
            stmt.setLong(1, selectedUserId);
            stmt.setLong(2, selectedUserId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Existing channel found: " + rs.getLong("id_channel"));
                    return rs.getLong("id_channel");  // Return the existing channel ID
                } else {
                    System.out.println("No existing channel. Creating a new one...");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // If no existing channel is found, create a new one
        System.out.println("Inserting new channel with user1 = 1 and user2 = " + selectedUserId);

        String createChannelQuery = "INSERT INTO channel (id_user1, id_user2) VALUES (1, ?)";
        try (PreparedStatement createStmt = cnx.prepareStatement(createChannelQuery, Statement.RETURN_GENERATED_KEYS)) {
            createStmt.setLong(1, selectedUserId);
            int rowsAffected = createStmt.executeUpdate();

            // Check if the insert was successful
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = createStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        System.out.println("New channel created with ID: " + generatedKeys.getLong(1));
                        return generatedKeys.getLong(1);  // Return the newly created channel ID
                    } else {
                        System.out.println("Failed to retrieve the generated channel ID.");
                    }
                }
            } else {
                System.out.println("Channel creation failed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;  // Return null in case of failure
    }





}
