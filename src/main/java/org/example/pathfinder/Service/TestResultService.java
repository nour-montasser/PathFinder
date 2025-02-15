package org.example.pathfinder.Service;

import org.example.pathfinder.Model.TestResult;
import org.example.pathfinder.App.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestResultService {
    Connection cnx;

    public TestResultService() {
        cnx = DatabaseConnection.getInstance().getCnx();
        System.out.println("Connexion actuelle : " + cnx);
    }
    public Map<String, Long> getAllJobOffers() {
        Map<String, Long> jobOffers = new HashMap<>();
        String query = "SELECT id_job_offer, job_title FROM JobOffers"; // Adjust table name if needed

        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(query)) {
            while (rs.next()) {
                jobOffers.put(rs.getString("job_title"), rs.getLong("id_job_offer"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching job offers from the database.", e);
        }

        return jobOffers;
    }


    public void ajouter(TestResult testResult) {
        String req = "INSERT INTO TestResult (id_user, id_test, result, status) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setLong(1, testResult.getIdUser());
            stm.setLong(2, testResult.getIdTest());
            stm.setFloat(3, testResult.getResult());
            stm.setBoolean(4, testResult.getStatus());
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TestResult> getAll() {
        List<TestResult> testResults = new ArrayList<>();
        String req = "SELECT * FROM TestResult";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);
            while (rs.next()) {
                TestResult tr = new TestResult(
                        rs.getLong("id_result"),
                        rs.getLong("id_user"),
                        rs.getLong("id_test"),
                        rs.getFloat("result"),
                        rs.getBoolean("status")
                );
                testResults.add(tr);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return testResults;
    }

    public void supprimer(Long id) {
        String req = "DELETE FROM TestResult WHERE id_result = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setLong(1, id);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
