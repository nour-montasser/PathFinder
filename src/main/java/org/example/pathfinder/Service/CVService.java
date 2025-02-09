package org.example.pathfinder.Service;

import org.example.pathfinder.Model.CV;
import org.example.pathfinder.Model.Experience;
import org.example.pathfinder.App.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CVService implements Services<CV> {
    private final Connection connection;

    public CVService() {
        this.connection = DatabaseConnection.getInstance().getCnx();
    }

    @Override
    public void add(CV cv) {
        String query = "INSERT INTO CV (id_user, title, introduction, languages) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, cv.getUserId());
            statement.setString(2, cv.getTitle());
            statement.setString(3, cv.getIntroduction());
            statement.setString(4, cv.getLanguages());
            statement.executeUpdate();
            System.out.println("CV added successfully.");
        } catch (Exception e) {
            System.err.println("Error adding CV: " + e.getMessage());
        }
    }

    @Override
    public void update(CV cv) {
        String query = "UPDATE CV SET id_user = ?, title = ?, introduction = ?, languages = ? WHERE id_cv = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, cv.getUserId());
            statement.setString(2, cv.getTitle());
            statement.setString(3, cv.getIntroduction());
            statement.setString(4, cv.getLanguages());
            statement.setInt(5, cv.getIdCV());
            statement.executeUpdate();
            System.out.println("CV updated successfully.");
        } catch (Exception e) {
            System.err.println("Error updating CV: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM CV WHERE id_cv = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            System.out.println("CV deleted successfully.");
        } catch (Exception e) {
            System.err.println("Error deleting CV: " + e.getMessage());
        }
    }

    @Override
    public CV getById(int id) {
        String queryCV = "SELECT * FROM CV WHERE id_cv = ?";
        String queryExperiences = "SELECT * FROM experience WHERE id_cv = ?";

        try (PreparedStatement statementCV = connection.prepareStatement(queryCV);
             PreparedStatement statementExp = connection.prepareStatement(queryExperiences)) {

            // Fetch the CV
            statementCV.setInt(1, id);
            ResultSet resultSetCV = statementCV.executeQuery();
            if (resultSetCV.next()) {
                CV cv = new CV(
                        resultSetCV.getInt("id_cv"),
                        resultSetCV.getInt("id_user"),
                        resultSetCV.getString("title"),
                        resultSetCV.getString("introduction"),
                        resultSetCV.getString("languages"),
                        resultSetCV.getTimestamp("date_creation")
                );

                // Fetch associated experiences
                statementExp.setInt(1, id);
                ResultSet resultSetExp = statementExp.executeQuery();
                while (resultSetExp.next()) {
                    Experience experience = new Experience(
                            resultSetExp.getInt("id_experience"),
                            resultSetExp.getInt("id_cv"),
                            resultSetExp.getString("type"),
                            resultSetExp.getString("position"),
                            resultSetExp.getString("location_name"),
                            resultSetExp.getString("start_date"),
                            resultSetExp.getString("end_date")
                    );
                    cv.addExperience(experience); // Add experience to the CV
                }

                return cv;
            }
        } catch (Exception e) {
            System.err.println("Error retrieving CV: " + e.getMessage());
        }
        return null;
    }


    @Override
    public List<CV> getAll() {
        List<CV> cvs = new ArrayList<>();
        String queryCV = "SELECT * FROM CV";
        String queryExperiences = "SELECT * FROM experience WHERE id_cv = ?";

        try (PreparedStatement statementCV = connection.prepareStatement(queryCV);
             PreparedStatement statementExp = connection.prepareStatement(queryExperiences)) {

            ResultSet resultSetCV = statementCV.executeQuery();
            while (resultSetCV.next()) {
                CV cv = new CV(
                        resultSetCV.getInt("id_cv"),
                        resultSetCV.getInt("id_user"),
                        resultSetCV.getString("title"),
                        resultSetCV.getString("introduction"),
                        resultSetCV.getString("languages"),
                        resultSetCV.getTimestamp("date_creation")
                );

                // Fetch associated experiences for this CV
                statementExp.setInt(1, cv.getIdCV());
                ResultSet resultSetExp = statementExp.executeQuery();
                while (resultSetExp.next()) {
                    Experience experience = new Experience(
                            resultSetExp.getInt("id_experience"),
                            resultSetExp.getInt("id_cv"),
                            resultSetExp.getString("type"),
                            resultSetExp.getString("position"),
                            resultSetExp.getString("location_name"),
                            resultSetExp.getString("start_date"),
                            resultSetExp.getString("end_date")
                    );
                    cv.addExperience(experience);
                }

                cvs.add(cv); // Add CV to the list
            }
        } catch (Exception e) {
            System.err.println("Error retrieving CVs: " + e.getMessage());
        }
        return cvs;
    }
    public int getLatestCVId() {
        String query = "SELECT MAX(id_cv) FROM CV";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1); // Return the latest inserted CV ID
            }
        } catch (Exception e) {
            System.err.println("Error retrieving latest CV ID: " + e.getMessage());
        }
        return -1; // Return -1 if an error occurs
    }

}
