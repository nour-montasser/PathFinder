package org.example.pathfinder.Service;

import org.example.pathfinder.Model.Experience;
import org.example.pathfinder.App.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;


public class ExperienceService implements Services<Experience> {
    private final Connection connection;

    public ExperienceService() {
        this.connection = DatabaseConnection.getInstance().getCnx();
    }

    @Override
    public void add(Experience experience) {
        String query = "INSERT INTO experience (id_cv, type, position, location_name, start_date, end_date, description) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, experience.getIdCv());
            statement.setString(2, experience.getType());
            statement.setString(3, experience.getPosition());
            statement.setString(4, experience.getLocationName());
            statement.setString(5, experience.getStartDate());
            statement.setString(6, experience.getEndDate());
            statement.setString(7, experience.getDescription()); // ✅ Now adding description
            statement.executeUpdate();
            System.out.println("Experience added successfully.");
        } catch (Exception e) {
            System.err.println("Error adding Experience: " + e.getMessage());
        }
    }

    @Override
    public void update(Experience experience) {
        String query = "UPDATE experience SET id_cv = ?, type = ?, position = ?, location_name = ?, start_date = ?, end_date = ?, description = ? WHERE id_experience = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, experience.getIdCv());
            statement.setString(2, experience.getType());
            statement.setString(3, experience.getPosition());
            statement.setString(4, experience.getLocationName());
            statement.setString(5, experience.getStartDate());
            statement.setString(6, experience.getEndDate());
            statement.setString(7, experience.getDescription()); // ✅ Now updating description
            statement.setInt(8, experience.getIdExperience());
            statement.executeUpdate();
            System.out.println("Experience updated successfully.");
        } catch (Exception e) {
            System.err.println("Error updating Experience: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM experience WHERE id_experience = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            System.out.println("Experience deleted successfully.");
        } catch (Exception e) {
            System.err.println("Error deleting Experience: " + e.getMessage());
        }
    }

    @Override
    public Experience getById(int id) {
        String query = "SELECT * FROM experience WHERE id_experience = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Experience(
                        resultSet.getInt("id_experience"),
                        resultSet.getInt("id_cv"),
                        resultSet.getString("type"),
                        resultSet.getString("position"),
                        resultSet.getString("location_name"),
                        resultSet.getString("start_date"),
                        resultSet.getString("end_date"),
                        resultSet.getString("description") // ✅ Fetching description from DB
                );
            }
        } catch (Exception e) {
            System.err.println("Error retrieving Experience: " + e.getMessage());
        }
        return null;
    }


    @Override
    public List<Experience> getAll() {
        List<Experience> experiences = new ArrayList<>();
        String query = "SELECT * FROM experience";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                experiences.add(new Experience(
                        resultSet.getInt("id_experience"),
                        resultSet.getInt("id_cv"),
                        resultSet.getString("type"),
                        resultSet.getString("position"),
                        resultSet.getString("location_name"),
                        resultSet.getString("start_date"),
                        resultSet.getString("end_date"),
                        resultSet.getString("description") // ✅ Now including description
                ));
            }
        } catch (Exception e) {
            System.err.println("Error retrieving Experiences: " + e.getMessage());
        }
        return experiences;
    }
    // ✅ **Newly added function: Get all experiences by CV ID**
    public List<Experience> getByCvId(int cvId) {
        List<Experience> experiences = new ArrayList<>();
        String query = "SELECT * FROM experience WHERE id_cv = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, cvId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                experiences.add(new Experience(
                        resultSet.getInt("id_experience"),
                        resultSet.getInt("id_cv"),
                        resultSet.getString("type"),
                        resultSet.getString("position"),
                        resultSet.getString("location_name"),
                        resultSet.getString("start_date"),
                        resultSet.getString("end_date"),
                        resultSet.getString("description")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching experiences by CV ID: " + e.getMessage());
        }
        return experiences;
    }

}
