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

public class JobOfferFormController {

    private JobOfferService jobOfferService;

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

    public JobOfferFormController() {
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

            // Create a JobOffer object with the current timestamp for datePosted
            JobOffer jobOffer = new JobOffer(
                    1L,  // Assume the user ID is 1 for now
                    title,
                    description,
                    type,
                    numberOfSpots,
                    requiredEducation,
                    requiredExperience,
                    skills
            );

            // Call the service to add the job offer
            jobOfferService.add(jobOffer);

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Job Offer Added", "The job offer was successfully added.");

            Stage stage = (Stage) titleField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            // Handle exception and show error message
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while adding the job offer.");
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
        clearForm();
    }
}
