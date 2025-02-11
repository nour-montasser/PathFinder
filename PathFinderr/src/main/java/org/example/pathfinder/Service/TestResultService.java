package org.example.pathfinder.Service;

import org.example.pathfinder.Model.TestResult;
import org.example.pathfinder.App.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TestResultService {
    Connection cnx;

    public TestResultService() {
        cnx = DatabaseConnection.getInstance().getCnx();
        System.out.println("Connexion actuelle : " + cnx);
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
