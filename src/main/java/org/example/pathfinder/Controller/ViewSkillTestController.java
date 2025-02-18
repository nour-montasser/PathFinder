package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.pathfinder.Model.Question;
import org.example.pathfinder.Model.SkillTest;

import java.util.List;

public class ViewSkillTestController {

    @FXML private Label skillTestTitle;
    @FXML private Label skillTestDescription;
    @FXML private VBox questionsContainer;
    @FXML private ScrollPane scrollPane; // ✅ Added ScrollPane reference

    private SkillTest currentSkillTest;

    public void setSkillTestData(SkillTest skillTest, List<Question> questions) {
        this.currentSkillTest = skillTest;
        skillTestTitle.setText(skillTest.getTitle());
        skillTestDescription.setText(skillTest.getDescription());

        // Clear any previous questions
        questionsContainer.getChildren().clear();

        for (Question question : questions) {
            VBox questionBox = new VBox(10); // Space between question and answers
            questionBox.setStyle("-fx-padding: 10px; -fx-background-color: #ffffff; -fx-border-color: #ddd; -fx-border-radius: 5px;");

            Label questionLabel = new Label(question.getQuestion());
            questionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            ToggleGroup toggleGroup = new ToggleGroup();
            VBox answersBox = new VBox(5);

            String[] responses = question.getResponses().split(",");
            for (String response : responses) {
                RadioButton radioButton = new RadioButton(response.trim());
                radioButton.setToggleGroup(toggleGroup);
                answersBox.getChildren().add(radioButton);
            }

            questionBox.getChildren().addAll(questionLabel, answersBox);
            questionsContainer.getChildren().add(questionBox);
        }

        scrollPane.applyCss();
        scrollPane.layout();
    }

    @FXML
    private void submitTest() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Submission");
        alert.setHeaderText(null);
        alert.setContentText("Your answers have been submitted!");
        alert.showAndWait();
    }

    @FXML
    private void clearSelection() {
        questionsContainer.getChildren().forEach(node -> {
            if (node instanceof VBox) {
                VBox vbox = (VBox) node;
                vbox.getChildren().stream()
                        .filter(n -> n instanceof VBox)
                        .map(n -> (VBox) n)
                        .flatMap(vb -> vb.getChildren().stream())
                        .filter(n -> n instanceof RadioButton)
                        .map(n -> (RadioButton) n)
                        .forEach(rb -> rb.setSelected(false));
            }
        });
    }
}
