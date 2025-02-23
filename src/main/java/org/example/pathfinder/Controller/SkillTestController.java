package org.example.pathfinder.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.pathfinder.Model.SkillTest;
import org.example.pathfinder.Model.Question;
import org.example.pathfinder.Service.SkillTestService;
import org.example.pathfinder.Service.QuestionService;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SkillTestController {
    private final SkillTestService skillTestService = new SkillTestService();
    private final QuestionService questionService = new QuestionService();
    private final ObservableList<SkillTest> skillTestList = FXCollections.observableArrayList();
    private final ObservableList<Question> questionList = FXCollections.observableArrayList();
    private SkillTest selectedSkillTest;

    @FXML private TextField titleField, descriptionField, durationField, scoreRequiredField;
    @FXML private ComboBox<String> jobOfferComboBox;
    @FXML private ListView<SkillTest> skillTestListView;
    private Map<String, Long> jobOfferMap;
    @FXML private ListView<String> questionListView;
    @FXML private TextField searchField;
    private FilteredList<SkillTest> filteredSkillTestList;
    @FXML private ComboBox<String> sortComboBox;

    @FXML
    public void initialize() {
        skillTestList.addAll(skillTestService.getAll());
        filteredSkillTestList = new FilteredList<>(skillTestList, s -> true);
        skillTestListView.setItems(filteredSkillTestList);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredSkillTestList.setPredicate(skillTest -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                return skillTest.getTitle().toLowerCase().contains(newValue.toLowerCase());
            });
        });

        sortComboBox.getItems().addAll("Alphabetical", "By Score");
        sortComboBox.setOnAction(event -> sortQuestions());

        skillTestListView.setCellFactory(param -> new ListCell<SkillTest>() {
            @Override
            protected void updateItem(SkillTest skillTest, boolean empty) {
                super.updateItem(skillTest, empty);
                setText((empty || skillTest == null) ? null : skillTest.getTitle());
            }
        });

        skillTestListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                selectedSkillTest = skillTestListView.getSelectionModel().getSelectedItem();
                if (selectedSkillTest != null) {
                    loadSkillTestForEditing(selectedSkillTest);
                    loadQuestionsForSkillTest(selectedSkillTest.getIdTest());
                }
            }
        });

        jobOfferMap = skillTestService.getAllJobOffers();
        jobOfferComboBox.setItems(FXCollections.observableArrayList(jobOfferMap.keySet()));
        questionListView.setItems(FXCollections.observableArrayList());
    }

    private void sortQuestions() {
        if (selectedSkillTest == null || questionList.isEmpty()) {
            return;
        }

        String selectedOption = sortComboBox.getValue();
        if (selectedOption == null) return;

        if (selectedOption.equals("Alphabetical")) {
            questionList.sort(Comparator.comparing(Question::getQuestion));
        } else if (selectedOption.equals("By Score")) {
            questionList.sort(Comparator.comparing(Question::getScore).reversed());
        }

        questionListView.getItems().setAll(questionList.stream().map(Question::getQuestion).toList());
    }

    @FXML
    public void setQuestions(List<Question> questions) {
        if (questions != null && !questions.isEmpty()) {
            this.questionList.setAll(questions);
            questionListView.getItems().setAll(
                    questionList.stream().map(Question::getQuestion).collect(Collectors.toList())
            );
            System.out.println("✅ Loaded " + questions.size() + " questions.");
        } else {
            System.out.println("⚠️ No questions received.");
        }
    }

    /**
     * ✅ Converts AI-generated question strings into Question objects
     */
    public void setAIQuestions(List<Question> aiQuestions) {
        if (aiQuestions == null || aiQuestions.isEmpty()) {
            showAlert("Error", "No AI-generated questions found.");
            return;
        }

        this.questionList.setAll(aiQuestions);
        questionListView.getItems().setAll(
                questionList.stream().map(Question::getQuestion).collect(Collectors.toList())
        );
        System.out.println("✅ AI Questions received and added to the test.");
    }

    @FXML
    public void addSkillTest() {
        try {
            String title = titleField.getText().trim();
            String description = descriptionField.getText().trim();
            long duration = Long.parseLong(durationField.getText().trim());
            String selectedJobOffer = jobOfferComboBox.getSelectionModel().getSelectedItem();
            long scoreRequired = Long.parseLong(scoreRequiredField.getText().trim());

            if (title.isEmpty() || description.isEmpty() || selectedJobOffer == null) {
                showAlert("Error", "Please fill all fields correctly.");
                return;
            }
            if (skillTestService.exists(title)) {
                showAlert("Error", "A Skill Test with this title already exists!");
                return;
            }

            long jobOfferId = jobOfferMap.get(selectedJobOffer);
            SkillTest skillTest = new SkillTest(null, title, description, duration, jobOfferId, scoreRequired);
            long skillTestId = skillTestService.ajouter(skillTest);

            if (skillTestId > 0) {
                skillTest.setIdTest(skillTestId);
                skillTestList.add(skillTest);

                // Save AI-generated or manually added questions
                for (Question q : questionList) {
                    q.setIdTest(skillTestId);
                    questionService.ajouter(q);
                }

                System.out.println("✅ Skill Test added and questions linked successfully!");
            }

            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numeric values.");
        }
    }

    private void loadQuestionsForSkillTest(Long skillTestId) {
        if (skillTestId == null) {
            showAlert("Error", "Invalid Skill Test selected.");
            return;
        }

        List<Question> questions = questionService.getQuestionsForSkillTest(skillTestId);
        questionList.setAll(questions);
        questionListView.getItems().setAll(questionList.stream().map(Question::getQuestion).toList());
    }

    @FXML
    public void updateSkillTest() {
        if (selectedSkillTest == null) {
            showAlert("Error", "No skill test selected for update.");
            return;
        }

        try {
            selectedSkillTest.setTitle(titleField.getText().trim());
            selectedSkillTest.setDescription(descriptionField.getText().trim());
            selectedSkillTest.setDuration(Long.parseLong(durationField.getText().trim()));
            selectedSkillTest.setScoreRequired(Long.parseLong(scoreRequiredField.getText().trim()));

            String selectedJobOffer = jobOfferComboBox.getSelectionModel().getSelectedItem();
            if (selectedJobOffer != null) {
                selectedSkillTest.setIdJobOffer(jobOfferMap.get(selectedJobOffer));
            }

            skillTestService.update(selectedSkillTest);

            int index = skillTestList.indexOf(selectedSkillTest);
            if (index != -1) {
                skillTestList.set(index, selectedSkillTest);
            }

            skillTestListView.getSelectionModel().clearSelection();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numeric values.");
        }
    }
    @FXML
    public void deleteSkillTest() {
        SkillTest selectedTest = skillTestListView.getSelectionModel().getSelectedItem();

        if (selectedTest == null) {
            showAlert("Error", "Please select a Skill Test to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete this skill test?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                skillTestService.supprimer(selectedTest.getIdTest()); // Remove from DB
                skillTestList.remove(selectedTest); // Remove from UI ListView
            }
        });
    }

    @FXML
    public void deleteQuestion() {
        String selectedQuestionText = questionListView.getSelectionModel().getSelectedItem();

        if (selectedQuestionText == null) {
            showAlert("Error", "Please select a question to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete this question?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Question questionToDelete = questionList.stream()
                        .filter(q -> q.getQuestion().equals(selectedQuestionText))
                        .findFirst()
                        .orElse(null);

                if (questionToDelete != null) {
                    questionService.supprimer(questionToDelete.getIdQuestion()); // ✅ Delete from DB
                    questionList.remove(questionToDelete); // ✅ Remove from ObservableList
                    questionListView.getItems().remove(selectedQuestionText); // ✅ Remove from UI ListView
                }
            }
        });
    }
    @FXML
    public void viewTest() {
        SkillTest selectedTest = skillTestListView.getSelectionModel().getSelectedItem();

        if (selectedTest == null) {
            showAlert("Error", "Please select a Skill Test to view its questions.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/FrontOffice/ViewSkillTest.fxml"));
            Parent root = loader.load();

            ViewSkillTestController controller = loader.getController();
            List<Question> questions = questionService.getQuestionsForSkillTest(selectedTest.getIdTest());

            controller.setSkillTestData(selectedTest, questions);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Skill Test Preview");
            stage.show();

        } catch (IOException e) {
            showAlert("Error", "Failed to load Skill Test.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void loadSkillTestForEditing(SkillTest skillTest) {
        selectedSkillTest = skillTest;

        titleField.setText(skillTest.getTitle());
        descriptionField.setText(skillTest.getDescription());
        durationField.setText(String.valueOf(skillTest.getDuration()));
        scoreRequiredField.setText(String.valueOf(skillTest.getScoreRequired()));

        jobOfferComboBox.setValue(getJobOfferName(skillTest.getIdJobOffer()));

        // Load questions related to the selected skill test
        loadQuestionsForSkillTest(skillTest.getIdTest());
    }

    /**
     * Retrieves the job offer name for a given ID.
     */
    private String getJobOfferName(Long idJobOffer) {
        return jobOfferMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(idJobOffer))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    /**
     * ✅ Clears all form fields and resets UI elements.
     */
    @FXML
    public void clearFields() {
        titleField.clear();
        descriptionField.clear();
        durationField.clear();
        jobOfferComboBox.getSelectionModel().clearSelection();
        scoreRequiredField.clear();
        selectedSkillTest = null;

        // Clear questions
        questionList.clear();
        questionListView.getItems().clear();
    }

}
