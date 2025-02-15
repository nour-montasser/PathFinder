package org.example.pathfinder.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.pathfinder.Model.Question;
import org.example.pathfinder.Service.QuestionService;

import java.io.IOException;
import java.util.List;

public class QuestionController {
    private ObservableList<Question> questionList = FXCollections.observableArrayList();

    @FXML private TextField questionField;
    @FXML private TextField responseField;
    @FXML private TextField correctResponseField;
    @FXML private TextField scoreField;
    @FXML private ListView<String> questionListView;
    private final QuestionService questionService = new QuestionService();

    @FXML
    public void initialize() {
        questionListView.setItems(FXCollections.observableArrayList()); // Bind ListView to ObservableList
    }

    @FXML
    public void addQuestion() {
        try {
            String questionText = questionField.getText().trim();
            String responses = responseField.getText().trim();
            String correctResponse = correctResponseField.getText().trim();
            int score = Integer.parseInt(scoreField.getText().trim());

            if (questionText.isEmpty() || responses.isEmpty() || correctResponse.isEmpty()) {
                showAlert("Error", "Please fill all fields correctly.");
                return;
            }

            // Create a new Question object (ID is initially null)
            Question question = new Question(null, questionText, null, responses, correctResponse, score);

            questionList.add(question);
            questionListView.getItems().add(questionText);
            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid number for the score.");
        }
    }

    @FXML
    public void goToSkillTestScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/SkillTest.fxml"));
            Parent root = loader.load();

            // Pass the list of questions to the SkillTestController
            SkillTestController skillTestController = loader.getController();
            skillTestController.setQuestions(FXCollections.observableArrayList(questionList)); // Pass questions in memory

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

    @FXML
    public void clearFields() {
        questionField.clear();
        responseField.clear();
        correctResponseField.clear();
        scoreField.clear();
    }
}
