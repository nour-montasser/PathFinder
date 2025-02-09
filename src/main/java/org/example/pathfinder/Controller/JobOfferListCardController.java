package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.pathfinder.Model.JobOffer;
import org.example.pathfinder.Service.JobOfferService;

import java.io.File;
import java.io.IOException;

public class JobOfferListCardController {

    @FXML
    private ImageView companyImage;

    @FXML
    private Label titleLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private VBox additionalDetails;

    @FXML
    private Label requiredEducationLabel;

    @FXML
    private Label requiredExperienceLabel;

    @FXML
    private Label skillsLabel;

    @FXML
    private Button updateButton;

    private boolean isExpanded = false;  // Track the state of the card
    private JobOffer jobOffer;  // The job offer that this card will display

    // JobOfferService instance to handle database operations
    private JobOfferService jobOfferService = new JobOfferService();

    public void setJobOffer(JobOffer jobOffer) {
        this.jobOffer = jobOffer;  // Store the job offer

        // Set the image (replace with a dynamic URL or file path as needed)
        File imageFile = new File("C:\\Users\\nourm\\Documents\\esprit\\3eme\\Project\\PathFinder\\src\\main\\resources\\Sources\\pathfinder_logo_compass.png.png");
        if (imageFile.exists()) {
            companyImage.setImage(new Image(imageFile.toURI().toString()));
        }

        // Set job offer details
        titleLabel.setText(jobOffer.getTitle());
        descriptionLabel.setText(jobOffer.getDescription());
        requiredEducationLabel.setText("Required Education: " + jobOffer.getRequiredEducation());
        requiredExperienceLabel.setText("Required Experience: " + jobOffer.getRequiredExperience());
        skillsLabel.setText("Skills: " + jobOffer.getSkills());

        // Hide additional details initially
        additionalDetails.setVisible(false);
        additionalDetails.setManaged(false);

        // Set the event on the entire VBox card to toggle details when clicked
        additionalDetails.getParent().setOnMouseClicked(event -> toggleDetails());
    }


    private void toggleDetails() {
        isExpanded = !isExpanded;
        additionalDetails.setVisible(isExpanded);
        additionalDetails.setManaged(isExpanded);
    }

    @FXML
    public void handleDelete() {
        // Ask for confirmation before deleting
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Job Offer");
        alert.setHeaderText("Are you sure you want to delete this job offer?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Call the service method to delete the job offer
                jobOfferService.delete(jobOffer);

                // Show a confirmation alert
                Alert deleteAlert = new Alert(Alert.AlertType.INFORMATION);
                deleteAlert.setTitle("Job Offer Deleted");
                deleteAlert.setHeaderText("Job offer deleted");
                deleteAlert.setContentText("The job offer has been successfully deleted.");
                deleteAlert.showAndWait();

                // Optionally, update the UI by removing the card from the list
                // You can call a method in the parent controller to refresh the list
                // ((ParentController) this.getParent()).refreshJobOfferList();
            }
        });
    }

    @FXML
    public void handleUpdate() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/View/JobOfferUpdate.fxml"));

            // Load the FXML file
            VBox form = loader.load();

            // Get the controller from the loader and pass the current job offer
            JobOfferUpdateController updateController = loader.getController();
            updateController.setJobOffer(jobOffer);  // Pass the job offer to the controller

            // Set up the stage for the modal dialog
            Stage stage = new Stage();
            stage.setTitle("Update Job Offer");
            stage.initModality(Modality.APPLICATION_MODAL);  // Make it a modal window
            stage.setScene(new Scene(form));
            stage.showAndWait();  // Show the dialog and block the parent window
        } catch (IOException e) {
            System.err.println("Error opening Update Job Offer form: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
