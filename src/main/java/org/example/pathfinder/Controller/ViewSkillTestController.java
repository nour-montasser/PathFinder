package org.example.pathfinder.Controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.pathfinder.Model.*;
import org.example.pathfinder.Service.ApplicationService;
import org.example.pathfinder.Service.TestResultService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.VBox;
import java.util.Map;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class ViewSkillTestController {

    @FXML private Label skillTestTitle;
    @FXML private Label skillTestDescription;
    @FXML private VBox questionsContainer;
    @FXML private ScrollPane scrollPane;

    private SkillTest currentSkillTest;
    private List<Question> questions;
    private TestResultService testResultService = new TestResultService(); // ✅ Ensure service is initialized
    @FXML private VBox chartContainer; // Make sure to add this in the FXML file
    private long loggedInUserId = LoggedUser.getInstance().getUserId();

    public void setSkillTestData(SkillTest skillTest, List<Question> questions) {
        this.currentSkillTest = skillTest;
        this.questions = questions; // ✅ Store questions
        skillTestTitle.setText(skillTest.getTitle());
        skillTestDescription.setText(skillTest.getDescription());
        loadPassFailChart(skillTest.getIdTest());
        // Clear any previous questions
        questionsContainer.getChildren().clear();

        for (Question question : questions) {
            VBox questionBox = new VBox(10);
            questionBox.setStyle("-fx-padding: 10px; -fx-background-color: #ffffff; -fx-border-color: #ddd; -fx-border-radius: 5px;");

            Label questionLabel = new Label(question.getQuestion());
            questionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            ToggleGroup toggleGroup = new ToggleGroup();
            VBox answersBox = new VBox(5);

            String[] responses = question.getResponses().split(",");
            for (String response : responses) {
                RadioButton radioButton = new RadioButton(response.trim());
                radioButton.setToggleGroup(toggleGroup);
                radioButton.setUserData(response.trim()); // ✅ Store answer as UserData
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
        if (currentSkillTest == null || questions == null || questions.isEmpty()) {
            showAlert("Error", "No skill test available.");
            return;
        }

        int score = 0;

        int totalPossibleScore = 0;
        int answeredQuestions = 0;

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            VBox questionBox = (VBox) questionsContainer.getChildren().get(i);
            VBox answersBox = (VBox) questionBox.getChildren().get(1);

            for (javafx.scene.Node node : answersBox.getChildren()) {
                if (node instanceof RadioButton) {
                    RadioButton radioButton = (RadioButton) node;
                    if (radioButton.isSelected()) {
                        answeredQuestions++;
                        if (radioButton.getUserData().equals(question.getCorrectResponse())) {
                            score += question.getScore(); // ✅ Add question score if correct
                        }
                    }
                }
            }
            totalPossibleScore += question.getScore();
        }

        int statusInt = (score >= (totalPossibleScore * 0.5)) ? 1 : 0;

        // ✅ Save result in database
        TestResult testResult = new TestResult(
                loggedInUserId, // TODO: Replace with actual user ID
                currentSkillTest.getId(),
                score,
                Date.valueOf(LocalDate.now()),
                statusInt
        );

        boolean resultSaved = testResultService.addTestResult(testResult);

        if (resultSaved) {
            showAlert("Success", "Your test result has been saved! Score: " + score + "/" + totalPossibleScore );
        } else {
            showAlert("Error", "Failed to save test result.");
        }
        if (statusInt==0){
            ApplicationService applicationService = new ApplicationService();
            ApplicationJob application = applicationService.getApplicationByUserAndSkillTest(loggedInUserId,  currentSkillTest.getId());
            application.setStatus("Rejected");
            applicationService.update(application);
        }
        //if result =1 => pending applincation
        //if result == 0 => application rejected by application

        ((Stage) skillTestDescription.getScene().getWindow()).close();
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void loadPassFailChart(Long skillTestId) {
        Map<String, Long> passFailData = testResultService.getPassFailCount(skillTestId);

        long passed = passFailData.getOrDefault("Passed", 0L);
        long failed = passFailData.getOrDefault("Failed", 0L);

        System.out.println("🟢 Passed in Chart: " + passed);
        System.out.println("🔴 Failed in Chart: " + failed);

        // 🔹 Create PieChart data
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

        PieChart.Data passedData = new PieChart.Data("Passed", passed);
        PieChart.Data failedData = new PieChart.Data("Failed", failed);

        if (passed > 0) {
            pieData.add(passedData);
        }

        if (failed > 0) {
            pieData.add(failedData);
        }

        // 🔹 Create a new PieChart with controlled size
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Pass vs Fail Statistics");
        pieChart.setData(pieData);
        pieChart.setLegendVisible(false);

        // ✅ Set the preferred size (Adjust these values as needed)
        pieChart.setPrefSize(250, 200);  // Width x Height

        // ✅ Change colors programmatically
        Platform.runLater(() -> {
            for (PieChart.Data data : pieChart.getData()) {
                if (data.getName().equals("Passed")) {
                    data.getNode().setStyle("-fx-pie-color: #dbb48f;");  // Green for Passed
                } else if (data.getName().equals("Failed")) {
                    data.getNode().setStyle("-fx-pie-color: #a66357;");  // Red for Failed
                }
            }

            chartContainer.getChildren().clear(); // Remove old chart
            chartContainer.getChildren().add(pieChart); // Add new chart
        });
    }





}
