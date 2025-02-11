package org.example.pathfinder.Service;

import org.example.pathfinder.Model.SkillTest;
import org.example.pathfinder.App.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SkillTestService {
    Connection cnx;

    public SkillTestService() {
        cnx = DatabaseConnection.getInstance().getCnx();
    }

    public void ajouter(SkillTest skillTest) {
        String req = "INSERT INTO SkillTest (title, description, duration, id_job_offer, score_required) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stm = cnx.prepareStatement(req, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stm.setString(1, skillTest.getTitle());
            stm.setString(2, skillTest.getDescription());
            stm.setLong(3, skillTest.getDuration());
            stm.setLong(4, skillTest.getIdJobOffer());
            stm.setLong(5, skillTest.getScoreRequired());

            int affectedRows = stm.executeUpdate();

            // Check if the insertion was successful and retrieve the generated ID
            if (affectedRows > 0) {
                try (var generatedKeys = stm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long generatedId = generatedKeys.getLong(1); // Get the generated ID
                        skillTest.setIdTest(generatedId); // Set the ID in the SkillTest object
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving SkillTest to the database.", e);
        }
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
