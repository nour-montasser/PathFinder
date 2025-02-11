package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.pathfinder.Model.Question;
import org.example.pathfinder.Model.SkillTest;
import org.example.pathfinder.Service.QuestionService;

import org.example.pathfinder.Service.SkillTestService;

import java.util.ArrayList;
import java.util.List;

public class SkillTestController {
    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private TextField durationField;
    @FXML private TextField jobOfferIdField;
    @FXML private TextField scoreRequiredField;
    @FXML private ListView<String> skillTestList;

    private final SkillTestService skillTestService = new SkillTestService();
    private final QuestionService questionService = new QuestionService();


    @FXML
    public void addSkillTest() {
        try {
            // Trim input fields to remove extra spaces
            String title = titleField.getText().trim();
            String description = descriptionField.getText().trim();

            // Convert input values to numbers
            Long duration = Long.parseLong(durationField.getText().trim());
            Long jobOfferId = Long.parseLong(jobOfferIdField.getText().trim());
            Long scoreRequired = Long.parseLong(scoreRequiredField.getText().trim());

            // Create the skill test
            SkillTest skillTest = new SkillTest(null, title, description, duration, jobOfferId, scoreRequired);

            // Save the skill test to the database and get the ID back
            skillTestService.ajouter(skillTest);  // Now skillTest will have its ID set after insertion

            if (skillTest.getIdTest() == null) {
                System.err.println("⚠️ Error: Skill Test ID is still null after saving!");
                showAlert("Error", "Could not retrieve Skill Test ID after saving.");
                return;
            }

            System.out.println("✅ Skill Test created with ID: " + skillTest.getIdTest());

            // Now link each question to this skill test
            if (receivedQuestions != null && !receivedQuestions.isEmpty()) {
                for (Question q : receivedQuestions) {
                    q.setIdTest(skillTest.getIdTest()); // Assign the newly created test's ID
                    questionService.ajouter(q); // Save the question (you need a similar service to save questions)
                    System.out.println("📌 Added question: " + q.getQuestion());
                }
            } else {
                System.out.println("⚠️ No questions were linked to this skill test.");
            }

            // Refresh the list
            refreshList();

            // Show success message
            showAlert("Success", "Skill Test and Questions added successfully!");
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid number format! " + e.getMessage());
            showAlert("Error", "Please enter valid numeric values.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            showAlert("Error", "Invalid input. Please enter valid data.");
        }
    }

    @FXML
    public void deleteSkillTest() {
        String selectedItem = skillTestList.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Long id = Long.parseLong(selectedItem.split(" - ")[0]); // Extract ID from list
            skillTestService.supprimer(id);
            refreshList();
        }
    }

    @FXML
    public void refreshList() {
        List<SkillTest> skillTests = skillTestService.getAll();
        skillTestList.getItems().clear();
        for (SkillTest st : skillTests) {
            skillTestList.getItems().add(st.getIdTest() + " - " + st.getTitle());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private List<Question> receivedQuestions = new ArrayList<>();

    // Method to receive questions from QuestionController
    public void setQuestions(List<Question> questions) {
        this.receivedQuestions = questions;
        System.out.println("✅ Received " + questions.size() + " questions for the skill test.");
    }
}
