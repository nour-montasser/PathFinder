package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.pathfinder.Model.ApplicationJob;
import org.example.pathfinder.Model.CoverLetter;
import org.example.pathfinder.Model.JobOffer;
import org.example.pathfinder.Model.LoggedUser;
import org.example.pathfinder.Service.ApplicationService;
import org.example.pathfinder.Service.CoverLetterService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class JobOfferApplicationUpdateController {

    private final ApplicationService applicationService;
    private final CoverLetterService coverLetterService;
    private JobOffer jobOffer; // Store the job offer
    private Long selectedCvId; // Store selected CV ID
    private ApplicationJob currentApplication; // Store the current application being updated

    @FXML
    private TextField CoverLetterField;

    @FXML
    private TextArea coverLetterField;

    @FXML
    private Button applyButton;
    @FXML
    private Label cvErrorLabel;

    @FXML
    private Label coverLetterTitleErrorLabel;

    @FXML
    private Label coverLetterErrorLabel;

    @FXML
    private Button cancelButton;

    @FXML
    private ComboBox<String> cvDropdown; // Dropdown to select CV titles

    private long loggedInUserId = LoggedUser.getInstance().getUserId();

    public JobOfferApplicationUpdateController() {
        this.applicationService = new ApplicationService();
        this.coverLetterService = new CoverLetterService();
    }

    /**
     * Set the JobOffer before displaying the form.
     */
    public void setJobOffer(JobOffer jobOffer, Long cvId) {
        this.jobOffer = jobOffer;
        loadCvDropdown(cvId);  // Pass the selected CV ID to the method

        // Fetch and set cover letter (if exists)
        ApplicationJob application = applicationService.getApplicationByJobOfferAndUser(jobOffer.getIdOffer(), loggedInUserId);
        if (application != null) {
            this.currentApplication = application;
            CoverLetter coverLetter = coverLetterService.getCoverLetterByApplication(application.getApplicationId());
            setCoverLetter(coverLetter);
        } else {
            this.currentApplication = null; // No existing application
        }
    }

    public void setCoverLetter(CoverLetter coverLetter) {
        if (coverLetter != null) {
            CoverLetterField.setText(coverLetter.getSubject()); // Set title field
            coverLetterField.setText(coverLetter.getContent()); // Set content field
        }
    }

    private void loadCvDropdown(Long cvId) {
        // Fetch the list of CV titles for the logged-in user
        List<String> cvTitles = applicationService.getUserCVTitles(loggedInUserId);

        cvDropdown.getItems().clear(); // Clear existing items
        cvDropdown.getItems().addAll(cvTitles); // Add fetched titles to dropdown

        // If the cvId is not null, select the CV that was previously used
        if (cvId != null) {
            List<Long> userCvIds = applicationService.getUserCVIds(loggedInUserId);
            int index = userCvIds.indexOf(cvId);
            if (index != -1) {
                cvDropdown.getSelectionModel().select(index); // Pre-select the CV in the dropdown
                selectedCvId = cvId; // Set the selected CV ID to the pre-selected value
            }
        }

        // Add listener to update selected CV ID when the user selects a CV
        cvDropdown.setOnAction(event -> {
            int selectedIndex = cvDropdown.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1) {
                selectedCvId = applicationService.getUserCVIds(loggedInUserId).get(selectedIndex);
            }
        });
    }

    @FXML
    private void handleApplyButtonClick() {
        try {
            if (jobOffer == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "No job offer selected!");
                return;
            }

            // Validate input fields
            if (!isValidForm()) {
                return;
            }

            // Ensure that a CV is selected
            if (selectedCvId == null) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Please select a CV.");
                return;
            }

            String coverLetterTitle = CoverLetterField.getText().trim();
            String coverLetterContent = coverLetterField.getText().trim();

            // If existing application, update it
            currentApplication.setCvId(selectedCvId); // Update CV in existing application
            applicationService.update(currentApplication); // Save updated application

            // Update the cover letter
            CoverLetter coverLetter = coverLetterService.getCoverLetterByApplication(currentApplication.getApplicationId());
            coverLetter.setSubject(coverLetterTitle);
            coverLetter.setContent(coverLetterContent);
            coverLetterService.update(coverLetter); // Update cover letter

            showAlert(Alert.AlertType.INFORMATION, "Success", "Application updated successfully!");

            closeForm();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating the application.");
            e.printStackTrace();
        }
    }

    private boolean isValidForm() {
        boolean isValid = true;

        // Validate CV selection
        if (selectedCvId == null) {
            cvErrorLabel.setText("Please select a CV.");
            isValid = false;
        } else {
            cvErrorLabel.setText("");
        }

        // Validate cover letter title
        if (CoverLetterField.getText().trim().isEmpty()) {
            coverLetterTitleErrorLabel.setText("Cover letter title is required.");
            isValid = false;
        } else {
            coverLetterTitleErrorLabel.setText("");
        }

        // Validate cover letter content
        if (coverLetterField.getText().trim().isEmpty()) {
            coverLetterErrorLabel.setText("Cover letter content is required.");
            isValid = false;
        } else {
            coverLetterErrorLabel.setText("");
        }

        return isValid;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeForm() {
        Stage stage = (Stage) applyButton.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    @FXML
    private void handleCancelButtonClick() {
        closeForm();
    }

    // Missing methods copied from JobOfferApplicationFormController

    private static final String API_KEY = "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"; // Replace with your actual API key
    private static final String API_URL = "https://api-inference.huggingface.co/models/deepseek-ai/DeepSeek-R1-Distill-Qwen-32B"; // Model endpoint

    @FXML
    private void handleGenerateCoverLetter() {
        try {
            // Collect user data (profile, CV, and experience) for the logged-in user
            String userData = coverLetterService.getUserDataForCoverLetter(loggedInUserId);
            String companyName = applicationService.getUserNameById(jobOffer.getIdUser());

            // Get job description from the job offer
            String jobDescription = jobOffer.getDescription();

            // Create the prompt for Hugging Face AI
            String prompt = buildPrompt(userData, jobDescription, companyName);

            // Call Hugging Face API to generate cover letter
            String generatedCoverLetter = generateCoverLetter(prompt);
            int thinkTagIndex = generatedCoverLetter.indexOf("</think>");
            if (thinkTagIndex != -1) {
                generatedCoverLetter = generatedCoverLetter.substring(thinkTagIndex + 8).trim();
            }

            // Display the generated cover letter in the TextArea
            coverLetterField.setText(generatedCoverLetter);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while generating the cover letter.");
            e.printStackTrace();
        }
    }

    private String buildPrompt(String userData, String jobDescription, String companyName) {
        return "Write a professional cover letter using the following details. Focus solely on the content of the cover letter and avoid any filler text or placeholders. Do not include any reasoning or instructions.\n\n" +
                "### Job Description ###\n" +
                jobDescription + "\n\n" +
                "### Company Name ###\n" +
                companyName + "\n\n" +
                "### User Information ###\n" +
                userData + "\n\n" +
                "Write the cover letter directly, starting with a brief introduction stating why the user is interested in the position. Highlight the most relevant experiences and skills based on the job description and user information. End with a polite conclusion, expressing the user's enthusiasm for the position. Answer only with the cover letter and do not include any additional text.";
    }

    private String generateCoverLetter(String prompt) {
        try {
            // Create the request body
            JSONObject requestData = new JSONObject();
            requestData.put("inputs", prompt);

            // Send the request to Hugging Face API
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Write the request data
            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestData.toString().getBytes());
                os.flush();
            }

            // Read the response
            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            JSONArray responseArray = new JSONArray(response.toString());
            String result = responseArray.getJSONObject(0).getString("generated_text").trim();

            if (result.startsWith(prompt)) {
                result = result.substring(prompt.length()).trim();
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating cover letter.";
        }
    }

    @FXML
    private void buttonHover(javafx.event.Event event) {
        Button source = (Button) event.getSource();

        if (source.getId().equals("generateCoverLetterButton")) {
            source.setStyle("-fx-background-color: #66BB6A; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 12 24; -fx-font-weight: bold;");
        } else if (source.getId().equals("applyButton")) {
            source.setStyle("-fx-background-color: #64B5F6; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 12 24; -fx-font-weight: bold;");
        } else if (source.getId().equals("cancelButton")) {
            source.setStyle("-fx-background-color: #FF7043; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 12 24; -fx-font-weight: bold;");
        }
    }

    @FXML
    private void buttonExit(javafx.event.Event event) {
        Button source = (Button) event.getSource();

        if (source.getId().equals("generateCoverLetterButton")) {
            source.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 12 24; -fx-font-weight: bold;");
        } else if (source.getId().equals("applyButton")) {
            source.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 12 24; -fx-font-weight: bold;");
        } else if (source.getId().equals("cancelButton")) {
            source.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 12 24; -fx-font-weight: bold;");
        }
    }
}