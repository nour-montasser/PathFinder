package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.pathfinder.Model.JobOffer;
import org.example.pathfinder.Service.JobOfferService;

public class JobOfferFormController {

    private final JobOfferService jobOfferService;

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
        this.jobOfferService = new JobOfferService(); // Ensure service is initialized
    }

    @FXML
    private void initialize() {
        typeComboBox.getItems().addAll("Full-time", "Part-time", "Contract");
    }

    @FXML
    private void handleSubmitButtonClick() {
        try {
            // Validate inputs
            if (!isValidForm()) {
                return;
            }

            // Extract values
            String title = titleField.getText().trim();
            String description = descriptionField.getText().trim();
            int numberOfSpots = Integer.parseInt(numberOfSpotsField.getText().trim());
            String requiredEducation = requiredEducationField.getText().trim();
            String requiredExperience = requiredExperienceField.getText().trim();
            String type = typeComboBox.getValue();
            String skills = skillsField.getText().trim();

            // Check if a job offer with the same title already exists
            if (jobOfferService.isJobOfferTitleUnique(title)) {
                // If unique, create a new JobOffer object and save it
                JobOffer jobOffer = new JobOffer(
                        1L, // Example user ID, replace with actual logged-in user ID
                        title,
                        description,
                        type,
                        numberOfSpots,
                        requiredEducation,
                        requiredExperience,
                        skills
                );

                jobOfferService.add(jobOffer); // Save job offer

                // Success message
                showAlert(Alert.AlertType.INFORMATION, "Success", "Job offer has been added successfully!");
                closeForm(); // Close form after successful submission
            } else {
                // If not unique, show an error message
                showAlert(Alert.AlertType.ERROR, "Duplicate Job Offer", "A job offer with the same title already exists.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for 'Number of Spots'.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred.");
            e.printStackTrace();
        }
    }

    private boolean isValidForm() {
        // Form validation logic (same as before)
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
        Stage stage = (Stage) titleField.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    @FXML
    private void handleCancelButtonClick() {
        closeForm();
    }
}
