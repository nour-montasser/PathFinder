package org.example.pathfinder.Service;

import org.example.pathfinder.Model.Language;
import org.example.pathfinder.App.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LanguageService implements Services<Language> {
    private final Connection connection;

    public LanguageService() {
        this.connection = DatabaseConnection.getInstance().getCnx();
    }

    @Override
    public void add(Language language) {
        String query = "INSERT INTO languages (id_cv, language_name ,test level) VALUES (?, ?, ?)";
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
        String query = "DELETE FROM languages WHERE id_language = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            System.out.println("Language deleted successfully.");
        } catch (Exception e) {
            System.err.println("Error deleting Language: " + e.getMessage());
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
}
