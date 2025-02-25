package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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

public class JobOfferApplicationFormController {

    private final ApplicationService applicationService;
    private final CoverLetterService coverLetterService;
    private JobOffer jobOffer; // Store the job offer
    private Long selectedCvId; // Store selected CV ID
    private long loggedInUserId = LoggedUser.getInstance().getUserId();

    @FXML
    private TextField CoverLetterField;

    @FXML
    private TextArea coverLetterField;

    @FXML
    private Button applyButton;

    @FXML
    private Button cancelButton;

    @FXML
    private ComboBox<String> cvDropdown; // Dropdown to select CV titles

    public JobOfferApplicationFormController() {
        this.applicationService = new ApplicationService();
        this.coverLetterService = new CoverLetterService();
    }

    /**
     * Set the JobOffer before displaying the form.
     */
    public void setJobOffer(JobOffer jobOffer) {
        this.jobOffer = jobOffer;
        loadCvDropdown(); // Load CVs when the form is set
    }

    private void loadCvDropdown() {
        // Assume the logged-in user ID is 1 for now


        // Fetch the list of CV titles for the logged-in user
        List<String> cvTitles = applicationService.getUserCVTitles(loggedInUserId);

        cvDropdown.getItems().clear(); // Clear existing items
        cvDropdown.getItems().addAll(cvTitles); // Add fetched titles to dropdown

        // Add listener  to update selected CV ID when the user selects a CV
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
           // System.out.println("logged"+loggedInUserId);
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

            // Create an ApplicationJob with selected CV
            ApplicationJob application = new ApplicationJob(jobOffer.getIdOffer(),loggedInUserId, selectedCvId);
            applicationService.add(application); // Save application

            // Create a CoverLetter
            CoverLetter coverLetter = new CoverLetter();
            coverLetter.setSubject(coverLetterTitle);
            coverLetter.setContent(coverLetterContent);
            coverLetter.setIdApp(applicationService.getone().getApplicationId());
            coverLetterService.add(coverLetter); // Save cover letter

            showAlert(Alert.AlertType.INFORMATION, "Success", "Application submitted successfully!");

            closeForm();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while applying.");
            e.printStackTrace();
        }
    }

    private boolean isValidForm() {
        if (CoverLetterField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Cover letter title is required.");
            return false;
        }
        if (coverLetterField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Cover letter content is required.");
            return false;
        }
        return true;
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

    private static final String API_KEY = "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"; // Replace with your actual API key
  //distilgpt2
    private static final String API_URL = "https://api-inference.huggingface.co/models/deepseek-ai/DeepSeek-R1-Distill-Qwen-32B"; // Model endpoint
    @FXML
    private void handleGenerateCoverLetter() {
        try {
            // Collect user data (profile, CV, and experience) for the logged-in user
            String userData = coverLetterService.getUserDataForCoverLetter(loggedInUserId); // This is where the data is fetched

            // Get job description from the job offer
            String jobDescription = jobOffer.getDescription();

            // Create the prompt for Hugging Face AI
            String prompt = buildPrompt(userData, jobDescription);

            // Call Hugging Face API to generate cover letter
            String generatedCoverLetter = generateCoverLetter(prompt);

            // Display the generated cover letter in the TextArea
            coverLetterField.setText(generatedCoverLetter);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while generating the cover letter.");
            e.printStackTrace();
        }
    }

    // Method to create the prompt for the Hugging Face model
    private String buildPrompt(String userData, String jobDescription) {
        return "Write a professional and well-structured cover letter based on the following details, use them to fill in the placeholders:\n\n" +
                "### Job Description ###\n" +
                jobDescription + "\n\n" +
                "### User Information ###\n" +
                userData + "\n\n" +
                "### Cover Letter Guidelines ###\n" +
                "- Maintain a professional and formal tone.\n" +
                "- Begin with an introduction stating the user's interest in the position.\n" +
                "- Highlight relevant experiences and skills.\n" +
                "- Conclude with a polite call to action and .\n" +
                "answer only with the cover letter dont add anything else and dont use place holders ";
    }


    // Method to send the request to Hugging Face API
    private String generateCoverLetter(String prompt) {
        try {
            // Create the request body
            JSONObject requestData = new JSONObject();
            requestData.put("inputs", prompt); // Passing the complete prompt to the model

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
            String result= responseArray.getJSONObject(0).getString("generated_text").trim();

            if (result.startsWith(prompt)) {
                result = result.substring(prompt.length()).trim();
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating cover letter."; // In case of error
        }
    }

}
