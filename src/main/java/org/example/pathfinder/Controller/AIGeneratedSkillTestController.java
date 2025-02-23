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
import org.example.pathfinder.Service.AISkillTestService;

import java.io.IOException;
import java.util.List;

public class AIGeneratedSkillTestController {
    @FXML private TextField topicField;
    @FXML private Button generateTestButton;
    @FXML private ListView<String> questionListView;
    @FXML private Button proceedButton;

    private final ObservableList<Question> generatedQuestions = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Ensure ListView is initialized properly
        if (questionListView == null) {
            System.out.println("⚠️ Warning: questionListView is NULL! Check FXML file.");
        }

        // Initially disable the proceed button until questions are generated
        if (proceedButton != null) {
            proceedButton.setDisable(true);
        }
    }

    @FXML
    private void generateSkillTest() {
        String topic = topicField.getText().trim();

        if (topic.isEmpty()) {
            showAlert("Error", "Please enter a topic for the AI-generated test.");
            return;
        }

        // Generate test from AI
        List<Question> questions = AISkillTestService.generateSkillTest(topic);

        if (questions != null && !questions.isEmpty()) {
            generatedQuestions.setAll(questions);

            // ✅ Ensure `questionListView` is initialized before modifying it
            if (questionListView != null) {
                questionListView.getItems().setAll(
                        questions.stream().map(Question::getQuestion).toList()
                );
            } else {
                System.out.println("⚠️ Error: questionListView is NULL in generateSkillTest!");
            }

            showAlert("Success", "AI-generated skill test created successfully! Click 'Proceed' to continue.");

            if (proceedButton != null) {
                proceedButton.setDisable(false); // Enable proceed button after generation
            }

        } else {
            showAlert("Error", "Failed to generate skill test. Try again.");
        }
    }

    @FXML
    private void goToSkillTestScreen() {
        if (generatedQuestions.isEmpty()) {
            showAlert("Error", "No AI-generated questions available. Generate a test first.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/FrontOffice/SkillTest.fxml"));
            Parent root = loader.load();

            // Get SkillTestController instance
            SkillTestController skillTestController = loader.getController();

            // ✅ Pass AI-generated **Question** objects instead of converting them to Strings
            skillTestController.setAIQuestions(FXCollections.observableArrayList(generatedQuestions));

            // Switch to the SkillTest scene
            Stage stage = (Stage) questionListView.getScene().getWindow();
            Scene scene = new Scene(root, 1000, 600);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            showAlert("Error", "Failed to load the Skill Test screen.");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
