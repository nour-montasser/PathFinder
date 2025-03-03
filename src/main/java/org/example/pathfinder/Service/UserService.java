package org.example.pathfinder.Service;

import org.example.pathfinder.App.DatabaseConnection;
import org.example.pathfinder.Model.Profile;
import org.example.pathfinder.Model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private Connection cnx;

    public UserService() {
        cnx = DatabaseConnection.getInstance().getCnx();
    }

    public void addUser(User user) {
        String req = "INSERT INTO app_user (name, email, password,role, image) VALUES (?, ?, ?, ?,?)";
        try {
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

            PreparedStatement stm = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, user.getName());
            stm.setString(2, user.getEmail());
            stm.setString(3, hashedPassword);
            stm.setInt(4, user.getRole().equals("COMPANY") ? 1 : user.getRole().equals("SEEKER") ? 2 : 3);
            stm.setString(5, user.getImage());

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

    public void updateImage(String id,String image) {
        String req = "UPDATE app_user SET image = ? WHERE id_user = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setString(1, image);
            stm.setString(2, id);
            stm.executeUpdate();
            System.out.println("Image updated successfully.");
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
                        null,
                        rs.getString("image")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving users: " + e.getMessage(), e);
        }
        return users;
    }

    // Authentication method
    public User authenticateUser(String email, String password) throws SQLException {
        String query = "SELECT * FROM app_user WHERE email = ?"; // Only query by email
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Retrieve the hashed password from the database
                    String storedPassword = rs.getString("password");

                    // Verify the plain-text password against the stored hash
                    if (BCrypt.checkpw(password, storedPassword)) {
                        User user = new User();
                        user.setId(rs.getInt("id_user"));
                        user.setName(rs.getString("name"));
                        user.setEmail(rs.getString("email"));
                        user.setRole(rs.getInt("role") == 1 ? "COMPANY" : rs.getInt("role") == 2 ? "SEEKER" : "ADMIN");
                        user.setImage(rs.getString("image"));
                        return user; // Successful login
                    }
                }
            }
        }
        return null; // Authentication failed
    }

    public User getUserByEmail(String email) throws SQLException {
        String query = "SELECT id_user, name, email, role, image FROM app_user WHERE email = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id_user"));
                    user.setName(rs.getString("name")); // Ensure column names match your database
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getInt("role") == 1 ? "COMPANY" :rs.getInt("role") == 2 ? "SEEKER" : "ADMIN");
                    user.setImage(rs.getString("image"));
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
                // Verify the old password using BCrypt
                return BCrypt.checkpw(oldPassword, storedPassword);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error verifying password: " + e.getMessage(), e);
        }
        return false;
    }
    public void updatePassword(long userId, String newPassword) {
        String req = "UPDATE app_user SET password = ? WHERE id_user = ?";
        try {
            // Hash the new password before storing it
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setString(1, hashedPassword);
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

    public int getNbOfUsers() {
        String query = "SELECT COUNT(*) AS total_users FROM app_user";
        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt("total_users");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving total number of users: " + e.getMessage(), e);
        }
        return 0; // Return 0 if no users are found or an error occurs
    }
    public int getNbOfUsersWithRole(int role) {
        String query = "SELECT COUNT(*) AS users_with_role FROM app_user WHERE role = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, role);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("users_with_role");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving number of users with role: " + e.getMessage(), e);
        }
        return 0; // Return 0 if no users are found or an error occurs
    }

    public List<User> getAllUsersSorted(String sortBy, String sortOrder) {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM app_user ORDER BY " + sortBy + " " + sortOrder;

        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                User user = new User(
                        rs.getInt("id_user"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getInt("role") == 1 ? "COMPANY" : rs.getInt("role") == 2 ? "SEEKER" : "ADMIN",
                        rs.getString("password"),
                        rs.getString("image")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving sorted users: " + e.getMessage(), e);
        }

        return users;
    }

    public int getUserCountByAgeGroup(int minAge, int maxAge) {
        String query = "SELECT p.birthday FROM app_user u JOIN profile p ON u.id_user = p.id_user";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                Date birthday = rs.getDate("birthday");
                if (birthday != null) {
                    Profile profile = new Profile();
                    profile.setBirthday(birthday);
                    long age = profile.getAge();
                    if (age >= minAge && age <= maxAge) {
                        count++;
                    }
                }
            }
            return count;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting users by age group: " + e.getMessage(), e);
        }
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
                        rs.getString("password"),
                        rs.getString("image")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user: " + e.getMessage(), e);
        }
        return null;
    }
}
