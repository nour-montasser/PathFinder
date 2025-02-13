package org.example.pathfinder.Service;

import org.example.pathfinder.Model.Certificate;
import org.example.pathfinder.App.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CertificateService implements Services<Certificate> {
    private final Connection connection;

    public CertificateService() {
        this.connection = DatabaseConnection.getInstance().getCnx();
    }

    @Override
    public void add(Certificate certificate) {
        String query = "INSERT INTO certificate (id_cv, title, description, media, issued_by, issue_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, certificate.getIdCv());
            statement.setString(2, certificate.getTitle());
            statement.setString(3, certificate.getDescription()); // Adding description
            statement.setString(4, certificate.getMedia());
            statement.setString(5, certificate.getAssociation());
            statement.setDate(6, certificate.getDate()); // Use java.sql.Date
            statement.executeUpdate();
            System.out.println("Certificate added successfully.");
        } catch (Exception e) {
            System.err.println("Error adding Certificate: " + e.getMessage());
        }
    }

    @Override
    public void update(Certificate certificate) {
        String query = "UPDATE certificate SET id_cv = ?, title = ?, description = ?, media = ?, issued_by = ?, issue_date = ? WHERE id_certificate = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, certificate.getIdCv());
            statement.setString(2, certificate.getTitle());
            statement.setString(3, certificate.getDescription()); // Updating description
            statement.setString(4, certificate.getMedia());
            statement.setString(5, certificate.getAssociation());
            statement.setDate(6, certificate.getDate());
            statement.setInt(7, certificate.getIdCertificate());
            statement.executeUpdate();
            System.out.println("Certificate updated successfully.");
        } catch (Exception e) {
            System.err.println("Error updating Certificate: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM certificate WHERE id_certificate = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            System.out.println("Certificate deleted successfully.");
        } catch (Exception e) {
            System.err.println("Error deleting Certificate: " + e.getMessage());
        }
    }

    @Override
    public Certificate getById(int id) {
        String query = "SELECT * FROM certificate WHERE id_certificate = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Certificate(
                        resultSet.getInt("id_certificate"),
                        resultSet.getInt("id_cv"),
                        resultSet.getString("title"),
                        resultSet.getString("description"), // Retrieving description
                        resultSet.getString("media"),
                        resultSet.getString("issued_by"),
                        resultSet.getDate("issue_date")
                );
            }
        } catch (Exception e) {
            System.err.println("Error retrieving Certificate: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Certificate> getAll() {
        List<Certificate> certificates = new ArrayList<>();
        String query = "SELECT * FROM certificate";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                certificates.add(new Certificate(
                        resultSet.getInt("id_certificate"),
                        resultSet.getInt("id_cv"),
                        resultSet.getString("title"),
                        resultSet.getString("description"), // Retrieving description
                        resultSet.getString("media"),
                        resultSet.getString("issued_by"),
                        resultSet.getDate("issue_date")
                ));
            }
        } catch (Exception e) {
            System.err.println("Error retrieving Certificates: " + e.getMessage());
        }
        return certificates;
    }

    public List<Certificate> getCertificatesByCV(int cvId) {
        List<Certificate> certificates = new ArrayList<>();
        String query = "SELECT * FROM certificate WHERE id_cv = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, cvId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                certificates.add(new Certificate(
                        resultSet.getInt("id_certificate"),
                        resultSet.getInt("id_cv"),
                        resultSet.getString("title"),
                        resultSet.getString("description"), // Retrieving description
                        resultSet.getString("media"),
                        resultSet.getString("issued_by"),
                        resultSet.getDate("issue_date")
                ));
            }
        } catch (Exception e) {
            System.err.println("Error retrieving Certificates for CV: " + e.getMessage());
        }
        return certificates;
    }
}
