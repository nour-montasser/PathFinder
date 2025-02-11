package org.example.pathfinder.Service;

import org.example.pathfinder.Model.Question;
import org.example.pathfinder.App.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionService {
    Connection cnx;

    public QuestionService() {
        cnx = DatabaseConnection.getInstance().getCnx();
    }

    public void ajouter(Question question) {
        String req = "INSERT INTO questions (question, id_test, responses, correct_response, score) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
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

    public List<Question> getAll() {
        List<Question> questions = new ArrayList<>();
        String req = "SELECT * FROM questions";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);
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

    public void supprimer(Long id) {
        String req = "DELETE FROM questions WHERE id_question = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setLong(1, id);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
