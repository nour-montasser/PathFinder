package org.example.pathfinder.Service;

import org.example.pathfinder.App.DatabaseConnection;
import org.example.pathfinder.Model.Profile;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfileService {
    private Connection cnx;

    public ProfileService() {
        cnx = DatabaseConnection.getInstance().getCnx();
    }

    public void add(Profile profile) {
        String req = "INSERT INTO profile (id_user, address, birthday, phone, current_occupation, photo, bio) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setLong(1, profile.getId_user());
            stm.setString(2, profile.getAddress());
            stm.setDate(3, new java.sql.Date(profile.getBirthday().getTime()));
            stm.setString(4, profile.getPhone());
            stm.setString(5, profile.getCurrent_occupation());
            stm.setString(6, profile.getPhoto());
            stm.setString(7, profile.getBio());
            stm.executeUpdate();
            System.out.println("Profile added successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error adding profile: " + e.getMessage(), e);
        }
    }

    public void update(Profile profile) {
        String req = "UPDATE profile SET address = ?, birthday = ?, phone = ?, current_occupation = ?, photo = ?, bio = ? WHERE id_user = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setString(1, profile.getAddress());
            stm.setDate(2, new java.sql.Date(profile.getBirthday().getTime()));
            stm.setString(3, profile.getPhone());
            stm.setString(4, profile.getCurrent_occupation());
            stm.setString(5, profile.getPhoto());
            stm.setString(6, profile.getBio());
            stm.setLong(7, profile.getId_user());
            stm.executeUpdate();
            System.out.println("Profile updated successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error updating profile: " + e.getMessage(), e);
        }
    }

    public void delete(long id_user) {
        String req = "DELETE FROM profile WHERE id_user = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setLong(1, id_user);
            stm.executeUpdate();
            System.out.println("Profile deleted successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting profile: " + e.getMessage(), e);
        }
    }

    public Profile getOne(long id_user) {
        String req = "SELECT * FROM profile WHERE id_user = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setLong(1, id_user);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return new Profile(
                        rs.getInt("id_user"),
                        rs.getString("address"),
                        rs.getDate("birthday"),
                        rs.getString("phone"),
                        rs.getString("current_occupation"),
                        rs.getString("photo"),
                        rs.getString("bio")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving profile: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Profile> getAll() {
        List<Profile> profiles = new ArrayList<>();
        String req = "SELECT * FROM profile";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);
            while (rs.next()) {
                profiles.add(new Profile(
                        rs.getInt("id_user"),
                        rs.getString("address"),
                        rs.getDate("birthday"),
                        rs.getString("phone"),
                        rs.getString("current_occupation"),
                        rs.getString("photo"),
                        rs.getString("bio")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving profiles: " + e.getMessage(), e);
        }
        return profiles;
    }
}