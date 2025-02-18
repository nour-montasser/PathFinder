package org.example.pathfinder.Service;

import org.example.pathfinder.Model.CoverLetter;
import org.example.pathfinder.App.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoverLetterService implements Services<CoverLetter> {

    private Connection cnx;

    public CoverLetterService() {
        cnx = DatabaseConnection.getInstance().getCnx();
    }

    @Override
    public void add(CoverLetter coverLetter) {
        String req = "INSERT INTO CoverLetter (id_app, content, subject) VALUES (?, ?, ?)";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setLong(1, coverLetter.getIdApp());
            stm.setString(2, coverLetter.getContent());
            stm.setString(3, coverLetter.getSubject());
            stm.executeUpdate();
            System.out.println("Cover letter added successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error adding cover letter: " + e.getMessage(), e);
        }
    }


    @Override
    public void update(CoverLetter coverLetter) {
        String req = "UPDATE CoverLetter SET content = ?, subject = ? WHERE id_CoverLetter = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setString(1, coverLetter.getContent());
            stm.setString(2, coverLetter.getSubject());
            stm.setLong(3, coverLetter.getIdCoverLetter()   );
            stm.executeUpdate();
            System.out.println("Cover letter updated successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error updating cover letter: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(long id) {
        String req = "DELETE FROM CoverLetter WHERE id_CoverLetter = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setLong(1, id);
            stm.executeUpdate();
            System.out.println("Cover letter deleted successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting cover letter: " + e.getMessage(), e);
        }
    }

    @Override
    public List<CoverLetter> getall() {
        List<CoverLetter> coverLetters = new ArrayList<>();
        String req = "SELECT * FROM CoverLetter";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);
            while (rs.next()) {
                CoverLetter coverLetter = new CoverLetter(
                        rs.getLong("id_app"),
                        rs.getString("content"),
                        rs.getString("subject")
                );
                coverLetter.setIdCoverLetter(rs.getLong("id_CoverLetter"));
                coverLetters.add(coverLetter);
            }
            return coverLetters;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving cover letters: " + e.getMessage(), e);
        }
    }

    public CoverLetter getById(Long id) {
        CoverLetter coverLetter = null;
        String req = "SELECT * FROM CoverLetter WHERE id_CoverLetter = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setLong(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                coverLetter = new CoverLetter(
                        rs.getLong("id_app"),
                        rs.getString("content"),
                        rs.getString("subject")
                );
                coverLetter.setIdCoverLetter(rs.getLong("id_CoverLetter"));
            }
            return coverLetter;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving cover letter by ID: " + e.getMessage(), e);
        }
    }

    public CoverLetter getone() {
        CoverLetter coverLetter = null;
        String req = "SELECT * FROM CoverLetter LIMIT 1";  // Get the first cover letter (you can change the logic as needed)
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                coverLetter = new CoverLetter(
                        rs.getLong("id_app"),
                        rs.getString("content"),
                        rs.getString("subject")
                );
                coverLetter.setIdCoverLetter(rs.getLong("id_CoverLetter"));
            }
            return coverLetter;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving cover letter: " + e.getMessage(), e);
        }
    }

    public CoverLetter getCoverLetterByApplication(Long applicationId) {
        // Fetch the cover letter for a given application ID from the database
        String query = "SELECT * FROM CoverLetter WHERE id_app = ?";
        CoverLetter coverLetter = null;

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setLong(1, applicationId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                coverLetter = new CoverLetter();
                coverLetter.setSubject(rs.getString("subject"));
                coverLetter.setContent(rs.getString("content"));
                coverLetter.setIdApp(rs.getLong("id_app"));
                coverLetter.setIdCoverLetter(rs.getLong("id_CoverLetter"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving CoverLetter: " + e.getMessage(), e);
        }

        return coverLetter;
    }



}
