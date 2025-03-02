package org.example.pathfinder.Controller;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.example.pathfinder.Model.*;
import org.example.pathfinder.Service.ApplicationService;
import org.example.pathfinder.Service.CoverLetterService;


import org.example.pathfinder.Service.QuestionService;
import org.example.pathfinder.Service.SkillTestService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class JobOfferApplicationFormController {

    private final ApplicationService applicationService;
    private final CoverLetterService coverLetterService;
    private JobOffer jobOffer; // Store the job offer
    private Long selectedCvId; // Store selected CV ID
    private long loggedInUserId = LoggedUser.getInstance().getUserId();
    private SkillTestService skillTestService = new SkillTestService();
    private QuestionService questionService = new QuestionService();

    @FXML
    private TextField CoverLetterField;

    @FXML
    private TextArea coverLetterField;

    @FXML
    private Button applyButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label cvErrorLabel;

    @FXML
    private Label coverLetterTitleErrorLabel;

    @FXML
    private Label coverLetterErrorLabel;

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
        //  applyAnimation();
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
            String coverLetterContent = coverLetterField.getText().trim().replace("\n", " ");


            // Create an ApplicationJob with selected CV
            ApplicationJob application = new ApplicationJob(jobOffer.getIdOffer(), loggedInUserId, selectedCvId);
            applicationService.add(application); // Save application

            // Create a CoverLetter
            CoverLetter coverLetter = new CoverLetter();
            coverLetter.setSubject(coverLetterTitle);
            coverLetter.setContent(coverLetterContent);
            coverLetter.setIdApp(applicationService.getone().getApplicationId());
            coverLetterService.add(coverLetter); // Save cover letter

            showAlert(Alert.AlertType.INFORMATION, "Success", "Application submitted successfully!");

            // Ask the user if they want to download the cover letter as a PDF
           /* Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Download Cover Letter");
            alert.setHeaderText("Do you want to download the cover letter as a PDF?");
            alert.setContentText("Click OK to save the cover letter as a PDF.");

            if (alert.showAndWait().orElse(null) == ButtonType.OK) {
                // Use FileChooser to let the user choose the save location
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Cover Letter as PDF");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
                File file = fileChooser.showSaveDialog(applyButton.getScene().getWindow());

                if (file != null) {
                    // Generate and save the PDF
                    generatePdf(coverLetterTitle,coverLetterContent, file.getAbsolutePath());
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Cover letter saved as PDF successfully!");
                }
            }*/
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Frontoffice/CoverLetterTemplates.fxml"));
                Parent root = loader.load();
                CoverLetterTemplatesController controller = loader.getController();

                controller.setSubjectAndContent(CoverLetterField.getText(), coverLetterField.getText() , coverLetterService.getLatestCoverLetterId());

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Choose a Template");
                stage.initModality(Modality.APPLICATION_MODAL);  // Make it modal
                stage.setResizable(false);
                stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load template selection.");
            }

            closeForm();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while applying.");
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

    private static final String API_KEY = "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"; // Replace with your actual API key
  //distilgpt2
    private static final String API_URL = "https://api-inference.huggingface.co/models/deepseek-ai/DeepSeek-R1-Distill-Qwen-32B"; // Model endpoint
    @FXML
    private void handleGenerateCoverLetter() {
        try {
            // Collect user data (profile, CV, and experience) for the logged-in user
            String userData = coverLetterService.getUserDataForCoverLetter(loggedInUserId); // This is where the data is fetched
            String companyName = applicationService.getUserNameById(jobOffer.getIdUser());
            // Get job description from the job offer
            String jobDescription = jobOffer.getDescription();

            // Create the prompt for Hugging Face AI
            String prompt = buildPrompt(userData, jobDescription, companyName);

            // Call Hugging Face API to generate cover letter
            String generatedCoverLetter = generateCoverLetter(prompt);
            int thinkTagIndex = generatedCoverLetter.indexOf("</think>");
            if (thinkTagIndex != -1) {
                // Extract the part of the string after "</think>"
                generatedCoverLetter = generatedCoverLetter.substring(thinkTagIndex + 8).trim();
            }


            // Display the generated cover letter in the TextArea
            coverLetterField.setText(generatedCoverLetter);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while generating the cover letter.");
            e.printStackTrace();
        }
    }

    // Method to create the prompt for the Hugging Face model
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
    @FXML
    private Button generateCoverLetterButton;
   /* private void applyAnimation() {
        // Translate the button to the right with an animation
        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setNode(generateCoverLetterButton);
        translateTransition.setFromX(0);
        translateTransition.setToX(300);
        translateTransition.setDuration(Duration.seconds(3));
        translateTransition.setCycleCount(TranslateTransition.INDEFINITE);
        translateTransition.setAutoReverse(true);
        translateTransition.play();
    }*/
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

/*
    public void generatePdf(String subject, String content, String filePath) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
                contentStream.beginText();
                contentStream.setLeading(14.5f); // Line spacing
                contentStream.newLineAtOffset(50, 700); // Starting position

                // Add the subject
                contentStream.setFont(PDType1Font.TIMES_BOLD, 14);
                wrapText(contentStream, "Subject: " + subject, 500);
                contentStream.newLine();
                contentStream.newLine();

                // Add formatted content
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
                String formattedContent = "Dear Hiring Manager,\n\n" + content + "\n\nSincerely,\n[Your Name]";

                // Display the text with automatic line wrapping
                wrapText(contentStream, formattedContent, 500);

                contentStream.endText(); // End the text block
            }

            // Save the PDF to the specified path
            document.save(filePath);

            // Open the PDF file after saving
            openPdfFile(filePath);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate PDF.");
        }
    }*/
}
