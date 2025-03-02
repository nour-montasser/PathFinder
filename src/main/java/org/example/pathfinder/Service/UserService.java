package org.example.pathfinder.Service;

import org.example.pathfinder.App.DatabaseConnection;
import org.example.pathfinder.Model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    private Connection cnx;

    public UserService() {
        cnx = DatabaseConnection.getInstance().getCnx();
    }

    public void addUser(User user) {
        String req = "INSERT INTO app_user (name, email, password,role) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement stm = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, user.getName());
            stm.setString(2, user.getEmail());
            stm.setString(3, user.getPassword());
            stm.setInt(4, user.getRole().equals("COMPANY") ? 1 : user.getRole().equals("SEEKER") ? 2 : 3);
            stm.executeUpdate();
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getInt(1));
            }
            System.out.println("User added successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error adding user: " + e.getMessage(), e);
        }
    }

    public void updateUser(User user) {
        String req = "UPDATE app_user SET name = ?, email = ?, role = ?, password = ? WHERE id_user = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setString(1, user.getName());
            stm.setString(2, user.getEmail());
            stm.setInt(3, user.getRole().equals("COMPANY") ? 1 : user.getRole().equals("SEEKER") ? 2 : 3);
            stm.setString(4, user.getPassword());
            stm.setLong(5, user.getId());
            stm.executeUpdate();
            System.out.println("User updated successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user: " + e.getMessage(), e);
        }
    }

    public void changeUserNameByEmail(String email,String name) {
        String req = "UPDATE app_user SET name = ? WHERE email = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setString(1, name);
            stm.setString(2, email);
            stm.executeUpdate();
            System.out.println("User updated successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user: " + e.getMessage(), e);
        }
    }

    public void deleteUser(long userId) {
        String req = "DELETE FROM app_user WHERE id_user = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setLong(1, userId);
            stm.executeUpdate();
            System.out.println("User deleted successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user: " + e.getMessage(), e);
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String req = "SELECT * FROM app_user";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);
            while (rs.next()) {
                User user = new User(
                        rs.getInt("id_user"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getInt("role") == 1 ? "COMPANY" :rs.getInt("role") == 2 ? "SEEKER" : "ADMIN",
                        rs.getString("password")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving users: " + e.getMessage(), e);
        }
        return users;
    }

    public User getUserById(long userId) {
        String req = "SELECT * FROM app_user WHERE id_user = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setLong(1, userId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id_user"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getInt("role") == 1 ? "COMPANY" :rs.getInt("role") == 2 ? "SEEKER" : "ADMIN",
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user: " + e.getMessage(), e);
        }
        return null;
    }
    // Authentication method
    public User authenticateUser(String email, String password) throws SQLException {
        String query = "SELECT * FROM app_user WHERE email = ? AND password = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, password); // Compare with hashed password
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id_user"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getInt("role") == 1 ? "COMPANY" :rs.getInt("role") == 2 ? "SEEKER" : "ADMIN");
                    return user; // Successful login
                }
            }
        }
        return null; // Authentication failed
    }
    public User getUserByEmail(String email) throws SQLException {
        String query = "SELECT * FROM app_user WHERE email = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id_user"));
                    user.setName(rs.getString("name")); // Ensure column names match your database
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getInt("role") == 1 ? "COMPANY" :rs.getInt("role") == 2 ? "SEEKER" : "ADMIN");
                    return user;
                }
            }
        }
        return null; // Return null if no user is found
    }

    public boolean verifyOldPassword(long userId, String oldPassword) {
        String query = "SELECT password FROM app_user WHERE id_user = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setLong(1, userId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(oldPassword); // Compare plaintext (consider hashing)
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error verifying password: " + e.getMessage(), e);
        }
        return false;
    }

    public void updatePassword(long userId, String newPassword) {
        String req = "UPDATE app_user SET password = ? WHERE id_user = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setString(1, newPassword);
            stm.setLong(2, userId);
            stm.executeUpdate();
            System.out.println("Password updated successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error updating password: " + e.getMessage(), e);
        }
    }

    public boolean isEmailTaken(String email) {
        String query = "SELECT COUNT(*) FROM app_user WHERE email = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // If count > 0, email is taken
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking email existence: " + e.getMessage(), e);
        }
        return false;
    }
    public List<User> getall(Long userId) {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM app_user";

        // If you need to filter users based on userId
        if (userId != null) {
            query += " WHERE id_user = ?";
        }

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            if (userId != null) {
                stmt.setLong(1, userId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    long idUser = rs.getLong("id_user");
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    String password = rs.getString("password");
                    String role = rs.getString("role");

                    User user = new User(idUser, name, email, role, password);
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return users;
    }


}