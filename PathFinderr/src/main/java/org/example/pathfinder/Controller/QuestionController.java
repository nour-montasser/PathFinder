package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.pathfinder.Model.Question;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuestionController {
    private List<Question> tempQuestions = new ArrayList<>();

    @FXML private TextField questionField;
    @FXML private TextField responseField;
    @FXML private TextField correctResponseField;
    @FXML private TextField scoreField;
    @FXML private ListView<String> questionListView;

    @FXML
    public void addQuestion() {
        try {
            String questionText = questionField.getText().trim();
            String responses = responseField.getText().trim();
            String correctResponse = correctResponseField.getText().trim();
            int score = Integer.parseInt(scoreField.getText().trim());

            if (questionText.isEmpty() || responses.isEmpty() || correctResponse.isEmpty() || scoreField.getText().isEmpty()) {
                showAlert("Error", "Please fill all fields correctly.");
                return;
            }

            // Create a new Question object
            Question question = new Question(null, questionText, null, responses, correctResponse, score);
            tempQuestions.add(question);

            // Update UI list
            questionListView.getItems().add(questionText);

            // Clear input fields for next entry
            questionField.clear();
            responseField.clear();
            correctResponseField.clear();
            scoreField.clear();
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid number for the score.");
        }
    }

    @FXML
    public void goToSkillTestScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/SkillTest.fxml"));
            Parent root = loader.load();

            // Pass the list of questions to SkillTestController
            SkillTestController skillTestController = loader.getController();
            skillTestController.setQuestions(tempQuestions);

            // Switch scenes
            Stage stage = (Stage) questionListView.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Error", "Failed to load the Skill Test screen.");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
