package org.example.pathfinder.Service;

import org.example.pathfinder.Model.Question;
import org.example.pathfinder.App.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionService {
    private final Connection cnx;

    public QuestionService() {
        cnx = DatabaseConnection.getInstance().getCnx();
    }
    public void assignQuestionToSkillTest(Long questionId, Long skillTestId) {
        String req = "UPDATE questions SET id_test = ? WHERE id_question = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setLong(1, skillTestId);
            stm.setLong(2, questionId);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error assigning question to skill test.", e);
        }
    }


    public void ajouter(Question question) {
        String req = "INSERT INTO questions (question, id_test, responses, correct_response, score) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setString(1, question.getQuestion());
            stm.setLong(2, question.getIdTest());
            stm.setString(3, question.getResponses());
            stm.setString(4, question.getCorrectResponse());
            stm.setInt(5, question.getScore());
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void update(Question question) {
        if (question.getIdQuestion() == null) {
            throw new RuntimeException("Cannot update question with null ID.");
        }

        String req = "UPDATE questions SET question = ?, responses = ?, correct_response = ?, score = ? WHERE id_question = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setString(1, question.getQuestion());
            stm.setString(2, question.getResponses());
            stm.setString(3, question.getCorrectResponse());
            stm.setInt(4, question.getScore());
            stm.setLong(5, question.getIdQuestion()); // Ensure idQuestion is not null

            int rowsUpdated = stm.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("⚠️ No question updated. Check if the ID exists.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating question in the database.", e);
        }
    }


    public List<Question> getAllQuestions() {
        List<Question> questions = new ArrayList<>();
        String req = "SELECT * FROM questions";
        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(req)) {
            while (rs.next()) {
                Question q = new Question(
                        rs.getLong("id_question"),
                        rs.getString("question"),
                        rs.getLong("id_test"),
                        rs.getString("responses"),
                        rs.getString("correct_response"),
                        rs.getInt("score")
                );
                questions.add(q);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return questions;
    }

    public List<Question> getQuestionsForSkillTest(Long skillTestId) {
        List<Question> questions = new ArrayList<>();
        String req = "SELECT * FROM questions WHERE id_test = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setLong(1, skillTestId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Question q = new Question(
                        rs.getLong("id_question"),
                        rs.getString("question"),
                        rs.getLong("id_test"),
                        rs.getString("responses"),
                        rs.getString("correct_response"),
                        rs.getInt("score")
                );
                questions.add(q);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving questions for Skill Test ID: " + skillTestId, e);
        }
        return questions;
    }

    public void supprimer(Long id) {
        String req = "DELETE FROM questions WHERE id_question = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setLong(1, id);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
