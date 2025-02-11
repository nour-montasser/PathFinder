package tn.esprit.demo.Service;

import tn.esprit.demo.DB_Connection.DBConnection;
import tn.esprit.demo.Modele.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceUser implements Crud_User<User> {
    private Connection cnx;

    public ServiceUser() {
        cnx = DBConnection.getInstance().getCnx();
    }

    // Method to hash password using SHA-256


    @Override
    public void insertUser(User user) throws SQLException {
        String query = "INSERT INTO app_user (name, email, password, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword()); // Hash password
            stmt.setInt(4, user.getRole().equals("COMPANY") ? 1 : 2); // Corrected role check
            stmt.executeUpdate();
        }
    }

    @Override
    public void updateUser(User user) throws SQLException {
        String query = "UPDATE app_user SET name = ?, email = ?, password = ? WHERE id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword()); // Hash password
            stmt.setInt(4, user.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void displayAllUser() {
        String query = "SELECT * FROM app_user";
        try (Statement stmt = cnx.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Name: " + rs.getString("name")); // Corrected column name
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("-------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUser(User user) throws SQLException {
        String query = "DELETE FROM app_user WHERE id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, user.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public List<User> selectAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM app_user";
        try (Statement stmt = cnx.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name")); // Corrected column name
                user.setEmail(rs.getString("email"));
                users.add(user);
            }
        }
        return users;
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
                    user.setRole(rs.getInt("role") == 1 ? "COMPANY" : "SEEKER");
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
                    user.setRole(rs.getInt("role") == 1 ? "COMPANY" : "SEEKER");
                    return user;
                }
            }
        }
        return null; // Return null if no user is found
    }

}
