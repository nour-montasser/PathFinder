package org.example.pathfinder.Service;

import org.example.pathfinder.App.DatabaseConnection;
import org.example.pathfinder.Model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService implements Services<User> {
    private Connection cnx;

    public UserService() {
        cnx = DatabaseConnection.getInstance().getCnx();
    }


    @Override
    public void add(User user) {
        String query = "INSERT INTO app_user (name, email, password, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword()); // Ideally, hash the password here
            stmt.setLong(4, user.getRole()); // Corrected to use Long for role
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(User user) {
        String query = "UPDATE app_user SET name = ?, email = ?, password = ?, role = ? WHERE id_user = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword()); // Ideally, hash the password here
            stmt.setLong(4, user.getRole()); // Corrected to use Long for role
            stmt.setLong(5, user.getId()); // Corrected to use Long for id
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(long id) {
        String query = "DELETE FROM app_user WHERE id_user = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setLong(1, id); // Corrected to use Long for id
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> getall() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM app_user";

        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                long idUser = rs.getLong("id_user");  // Corrected to Long for id
                String name = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");
                long role = rs.getLong("role");  // Corrected to Long for role

                User user = new User(idUser, name, email, role, password); // Corrected constructor parameters
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return users;
    }
    public Long getUserIdByUsername(String username) {
        // Use the existing method to get the user by username
        User user = getUserByUsername(username);
        if (user != null) {
            return user.getId();
        } else {
            return -1L;  // Return -1 if user not found
        }
    }

    @Override
    public User getone() {
        return null;
    }

    // New method to get user by ID
    public User getUserById(long id) { // Corrected parameter to Long
        User user = null;
        String query = "SELECT * FROM app_user WHERE id_user = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setLong(1, id); // Corrected to Long for id
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    String password = rs.getString("password");
                    long role = rs.getLong("role"); // Corrected to Long for role

                    user = new User(id, name, email, role, password); // Corrected constructor parameters
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }
    public User getUserByUsername(String username) {
        User user = null;
        String query = "SELECT * FROM app_user WHERE name = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id_user");
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    String password = rs.getString("password");
                    long role = rs.getLong("role");

                    user = new User(id, name, email, role, password);  // Adjust constructor as needed
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;


    }
    public String getProfilePictureById(long id) {
        String query = "SELECT photo FROM profile WHERE id_user = ?";
        String s="C:\\Users\\User\\Downloads\\PathFinder-CV\\PathFinder-CV\\src\\main\\resources\\img\\pathfinder_logo_compass.png.png" ;// Default profile picture

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    s = rs.getString("photo"); // Assign value to s
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return s; // Return either the retrieved photo or the default
    }

}




