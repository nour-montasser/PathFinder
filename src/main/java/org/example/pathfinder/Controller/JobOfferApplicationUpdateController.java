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
        ApplicationJob application = applicationService.getApplicationByJobOfferAndUser(jobOffer.getIdOffer(), loggedInUserId); // Fetch application by job offer and logged-in user (ID 1 for now)
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
               /* if (coverLetter == null) {
                    coverLetter = new CoverLetter();
                    coverLetter.setIdApp(currentApplication.getApplicationId());
                }*/
                System.out.println(coverLetter);
                coverLetter.setSubject(coverLetterTitle);
                coverLetter.setContent(coverLetterContent);
            System.out.println(coverLetter);
                coverLetterService.update(coverLetter); // Update cover letter


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
}
