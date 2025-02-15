package org.example.pathfinder.Service;

import org.example.pathfinder.Model.Language;
import org.example.pathfinder.App.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class LanguageService implements Services<Language> {
    private final Connection connection;

    public LanguageService() {
        this.connection = DatabaseConnection.getInstance().getCnx();
    }

    @Override
    public void add(Language language) {
        String query = "INSERT INTO languages (id_cv, language_name ,level) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, language.getCvId());
            statement.setString(2, language.getName());
            statement.setString(3, language.getLevel());
            statement.executeUpdate();
            System.out.println("Language added successfully.");
        } catch (Exception e) {
            System.err.println("Error adding Language: " + e.getMessage());
        }
    }

    @Override
    public void update(Language language) {
        String query = "UPDATE languages SET id_cv = ?, language_name = ?, level = ? WHERE id_language = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, language.getCvId());
            statement.setString(2, language.getName());
            statement.setString(3, language.getLevel());
            statement.setInt(4, language.getIdLanguage());
            statement.executeUpdate();
            System.out.println("Language updated successfully.");
        } catch (Exception e) {
            System.err.println("Error updating Language: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        System.out.println("🔍 Attempting to delete Language with ID: " + id);

        if (id <= 0) {
            System.err.println("❌ Invalid Language ID: " + id + " (Deletion Skipped)");
            return;
        }

        String query = "DELETE FROM languages WHERE id_language = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Language deleted successfully (ID: " + id + ").");
            } else {
                System.err.println("⚠️ No language found with ID: " + id + " (Nothing deleted).");
            }

        } catch (Exception e) {
            System.err.println("❌ Error deleting Language (ID: " + id + "): " + e.getMessage());
        }
    }


    @Override
    public Language getById(int id) {
        String query = "SELECT * FROM languages WHERE id_language = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Language(
                        resultSet.getInt("id_language"),
                        resultSet.getInt("id_cv"),
                        resultSet.getString("language_name"),
                        resultSet.getString("level")
                );
            }
        } catch (Exception e) {
            System.err.println("Error retrieving Language: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Language> getAll() {
        List<Language> languages = new ArrayList<>();
        String query = "SELECT * FROM languages";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                languages.add(new Language(
                        resultSet.getInt("id_language"),
                        resultSet.getInt("id_cv"),
                        resultSet.getString("language_name"),
                        resultSet.getString("level")
                ));
            }
        } catch (Exception e) {
            System.err.println("Error retrieving Languages: " + e.getMessage());
        }
        return languages;
    }

    // Fetch all languages associated with a specific CV
    public List<Language> getLanguagesByCV(int cvId) {
        List<Language> languages = new ArrayList<>();
        String query = "SELECT * FROM languages WHERE id_cv = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, cvId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                languages.add(new Language(
                        resultSet.getInt("id_language"),
                        resultSet.getInt("id_cv"),
                        resultSet.getString("language_name"),
                        resultSet.getString("level")
                ));
            }
        } catch (Exception e) {
            System.err.println("Error retrieving Languages for CV: " + e.getMessage());
        }
        return languages;
    }
    public List<Language> getByCvId(int cvId) {
        List<Language> languages = new ArrayList<>();
        String query = "SELECT id_language, id_cv, language_name, level FROM languages WHERE id_cv = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, cvId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int idLanguage = resultSet.getInt("id_language"); // 🔥 Ensure this is correctly fetched
                System.out.println("✅ Retrieved Language ID: " + idLanguage); // Debugging output

                languages.add(new Language(
                        idLanguage, // 🔥 Set the correct ID
                        resultSet.getInt("id_cv"),
                        resultSet.getString("language_name"),
                        resultSet.getString("level")
                ));
            }
        } catch (Exception e) {
            System.err.println("❌ Error retrieving Languages for CV ID " + cvId + ": " + e.getMessage());
        }

        return languages;
    }


}
