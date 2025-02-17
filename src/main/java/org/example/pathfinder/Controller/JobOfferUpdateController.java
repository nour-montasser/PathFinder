package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.example.pathfinder.Model.JobOffer;
import org.example.pathfinder.Service.JobOfferService;

public class JobOfferUpdateController {

    private JobOfferService jobOfferService;
    private JobOffer currentJobOffer; // Holds the current job offer for editing

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField numberOfSpotsField;

    @FXML
    private TextField requiredEducationField;

    @FXML
    private TextField requiredExperienceField;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField skillsField;

    @FXML
    private Button submitButton;

    public JobOfferUpdateController() {
        jobOfferService = new JobOfferService(); // Initialize the service
    }

    @FXML
    private void initialize() {
        typeComboBox.getItems().addAll("Full-time", "Part-time", "Contract");
    }

    @FXML
    private void handleSubmitButtonClick() {
        try {
            // Validate the inputs
            if (!isValidForm()) {
                return; // Stop processing if validation fails
            }

            String title = titleField.getText();
            String description = descriptionField.getText();
            int numberOfSpots = Integer.parseInt(numberOfSpotsField.getText());
            String requiredEducation = requiredEducationField.getText();
            String requiredExperience = requiredExperienceField.getText();
            String type = typeComboBox.getValue();
            String skills = skillsField.getText(); // Assuming skills are comma-separated

            if (!currentJobOffer.getTitle().equals(title) && !jobOfferService.isJobOfferTitleUnique(title)) {
                showAlert(Alert.AlertType.ERROR, "Duplicate Job Offer", "A job offer with the same title already exists.");
                return;
            }

            // Update the JobOffer object with new values
            currentJobOffer.setTitle(title);
            currentJobOffer.setDescription(description);
            currentJobOffer.setNumberOfSpots(numberOfSpots);
            currentJobOffer.setRequiredEducation(requiredEducation);
            currentJobOffer.setRequiredExperience(requiredExperience);
            currentJobOffer.setType(type);
            currentJobOffer.setSkills(skills);

            // Call the service to update the job offer in the database
            jobOfferService.update(currentJobOffer);

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Job Offer Updated", "The job offer was successfully updated.");

            Stage stage = (Stage) titleField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            // Handle exception and show error message
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating the job offer.");
            e.printStackTrace();
        }
    }

    private boolean isValidForm() {
        // Check if any required field is empty
        if (titleField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Title is required.");
            return false;
        }
        if (descriptionField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Description is required.");
            return false;
        }
        if (numberOfSpotsField.getText().isEmpty() || !isNumeric(numberOfSpotsField.getText())) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Number of spots must be a valid number.");
            return false;
        }
        if (requiredEducationField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Required education is required.");
            return false;
        }
        if (requiredExperienceField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Required experience is required.");
            return false;
        }
        if (typeComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Job type is required.");
            return false;
        }
        if (skillsField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Skills are required.");
            return false;
        }

        return true;
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm() {
        titleField.clear();
        descriptionField.clear();
        numberOfSpotsField.clear();
        requiredEducationField.clear();
        requiredExperienceField.clear();
        skillsField.clear();
        typeComboBox.setValue(null);
    }

    @FXML
    private void handleCancelButtonClick() {
        Stage stage = (Stage) descriptionField.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    // This method will be used to populate the form with the existing job offer data
    public void setJobOffer(JobOffer jobOffer) {
        this.currentJobOffer = jobOffer;

        titleField.setText(jobOffer.getTitle());
        descriptionField.setText(jobOffer.getDescription());
        numberOfSpotsField.setText(String.valueOf(jobOffer.getNumberOfSpots()));
        requiredEducationField.setText(jobOffer.getRequiredEducation());
        requiredExperienceField.setText(jobOffer.getRequiredExperience());
        skillsField.setText(jobOffer.getSkills());
        typeComboBox.setValue(jobOffer.getType());
    }
}
