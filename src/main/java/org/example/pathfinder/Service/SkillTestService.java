package org.example.pathfinder.Service;

import org.example.pathfinder.Model.SkillTest;
import org.example.pathfinder.App.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillTestService {
    Connection cnx;

    public SkillTestService() {
        cnx = DatabaseConnection.getInstance().getCnx();
    }
    public void update(SkillTest skillTest) {
        String req = "UPDATE SkillTest SET title = ?, description = ?, duration = ?, id_job_offer = ?, score_required = ? WHERE id_test = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setString(1, skillTest.getTitle());
            stm.setString(2, skillTest.getDescription());
            stm.setLong(3, skillTest.getDuration());
            stm.setLong(4, skillTest.getIdJobOffer());
            stm.setLong(5, skillTest.getScoreRequired());
            stm.setLong(6, skillTest.getIdTest());
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating SkillTest in the database.", e);
        }
    }
    public Map<String, Long> getAllJobOffers() {
        Map<String, Long> jobOffers = new HashMap<>();
        String query = "SELECT id_offer, title FROM job_offer"; // Fixed column names

        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(query)) {
            while (rs.next()) {
                jobOffers.put(rs.getString("title"), rs.getLong("id_offer")); // Match correct column names
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching job offers from the database.", e);
        }

        return jobOffers;
    }




    public long ajouter(SkillTest skillTest) {
        String req = "INSERT INTO SkillTest (title, description, duration, id_job_offer, score_required) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stm = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            stm.setString(1, skillTest.getTitle());
            stm.setString(2, skillTest.getDescription());
            stm.setLong(3, skillTest.getDuration());
            stm.setLong(4, skillTest.getIdJobOffer());
            stm.setLong(5, skillTest.getScoreRequired());

            int affectedRows = stm.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long generatedId = generatedKeys.getLong(1);
                        skillTest.setIdTest(generatedId);
                        return generatedId;  // ✅ Return the newly generated ID
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving SkillTest to the database.", e);
        }
        return -1; // ✅ Return -1 if insertion fails
    }



    public List<SkillTest> getAll() {
        List<SkillTest> skillTests = new ArrayList<>();
        String req = "SELECT * FROM SkillTest";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);
            while (rs.next()) {
                SkillTest st = new SkillTest(
                        rs.getLong("id_test"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getLong("duration"),
                        rs.getLong("id_job_offer"),
                        rs.getLong("score_required")
                );
                skillTests.add(st);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return skillTests;
    }

    public void supprimer(Long id) {
        String req = "DELETE FROM SkillTest WHERE id_test = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setLong(1, id);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
