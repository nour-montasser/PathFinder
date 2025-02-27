package org.example.pathfinder.Service;

import org.example.pathfinder.Model.TestResult;
import org.example.pathfinder.App.DatabaseConnection;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TestResultService {
    private Connection cnx;

    // ✅ Initialize database connection in the constructor
    public TestResultService() {
        this.cnx = DatabaseConnection.getInstance().getCnx();
    }


    // ✅ Insert Test Result
    public boolean addTestResult(TestResult testResult) {
        String query = "INSERT INTO test_result (id_user, id_test, result, date, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, testResult.getIdUser());
            statement.setLong(2, testResult.getIdTest());
            statement.setFloat(3, testResult.getResult()); // ✅ `float` instead of `int`
            statement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            statement.setInt(5, testResult.getStatus()); // ✅ Store `1` or `0`

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Get Test Results by User
    public List<TestResult> getResultsByUser(Long userId) {
        List<TestResult> results = new ArrayList<>();
        String query = "SELECT * FROM test_result WHERE id_user = ?";
        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                results.add(mapResultSetToTestResult(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // ✅ Get Test Results by Test ID
    public List<TestResult> getResultsByTest(Long testId) {
        List<TestResult> results = new ArrayList<>();
        String query = "SELECT * FROM test_result WHERE id_test = ?";
        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setLong(1, testId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                results.add(mapResultSetToTestResult(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // ✅ Get Best Score for a User
    public int getBestScore(Long userId, Long testId) {
        String query = "SELECT MAX(result) FROM test_result WHERE id_user = ? AND id_test = ?";
        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setLong(1, userId);
            statement.setLong(2, testId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ✅ Helper Method to Convert ResultSet to TestResult
    private TestResult mapResultSetToTestResult(ResultSet resultSet) throws SQLException {
        return new TestResult(
                resultSet.getLong("id_user"),
                resultSet.getLong("id_test"),
                resultSet.getInt("result"),
                resultSet.getDate("date"),
                resultSet.getInt("status")
        );
    }
    public Map<String, Long> getPassFailCount(Long testId) {
        Map<String, Long> passFailMap = new HashMap<>();
        String query = "SELECT status, COUNT(*) AS count FROM test_result WHERE id_test = ? GROUP BY status";

        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setLong(1, testId);
            ResultSet resultSet = statement.executeQuery();

            long passed = 0, failed = 0;
            while (resultSet.next()) {
                int status = resultSet.getInt("status");
                long count = resultSet.getLong("count"); // Total occurrences, not distinct users
                if (status == 1) {
                    passed = count;
                } else {
                    failed = count;
                }
            }

            passFailMap.put("Passed", passed);
            passFailMap.put("Failed", failed);
            System.out.println("Pass Count: " + passed + ", Fail Count: " + failed);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return passFailMap;
    }

}
