package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import org.example.pathfinder.Model.ApplicationJob;
import org.example.pathfinder.Model.JobOffer;
import org.example.pathfinder.Service.ApplicationService;
import org.example.pathfinder.Service.JobOfferService;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

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
    private MenuButton actionsMenu;

    @FXML
    private MenuItem updateMenuItem;

    @FXML
    private MenuItem deleteMenuItem;

    @FXML
    private MenuItem applyMenuItem;


    @FXML
    private VBox cardContainer;

    private boolean isExpanded = false;
    private JobOffer jobOffer;
    private JobOfferService jobOfferService = new JobOfferService();
    private JobOfferListController parentController;



    public void setJobOffer(JobOffer jobOffer) {
        this.jobOffer = jobOffer;

        File imageFile = new File("C:\\Users\\nourm\\Documents\\esprit\\3eme\\Project\\PathFinder\\src\\main\\resources\\org\\example\\pathfinder\\Sources\\pathfinder_logo_compass.png");
        if (imageFile.exists()) {
            companyImage.setImage(new Image(imageFile.toURI().toString()));
        }

        titleLabel.setText(jobOffer.getTitle());
        descriptionLabel.setText(jobOffer.getDescription());
        requiredEducationLabel.setText("Required Education: " + jobOffer.getRequiredEducation());
        requiredExperienceLabel.setText("Required Experience: " + jobOffer.getRequiredExperience());
        skillsLabel.setText("Skills: " + jobOffer.getSkills());

        additionalDetails.setVisible(false);
        additionalDetails.setManaged(false);

        titleLabel.setOnMouseClicked(event -> openJobOfferDetailScene());
        cardContainer.setOnMouseClicked(event -> toggleDetails());
    }

    public void setParentController(JobOfferListController parentController) {
        this.parentController = parentController;
    }

    private void toggleDetails() {
        isExpanded = !isExpanded;
        additionalDetails.setVisible(isExpanded);
        additionalDetails.setManaged(isExpanded);
    }

    @FXML
    public void handleDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Job Offer");
        alert.setHeaderText("Are you sure you want to delete this job offer?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                jobOfferService.delete(jobOffer);

                ObservableList<JobOffer> jobOffers = parentController.getJobOffers();
                jobOffers.remove(jobOffer);

                showAlert(Alert.AlertType.INFORMATION, "Job Offer Deleted", "Job offer deleted", "The job offer has been successfully deleted.");
                parentController.refreshJobOfferList();
            }
        });
    }

    @FXML
    public void handleUpdate() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/View/JobOfferUpdate.fxml"));
            VBox form = loader.load();

            JobOfferUpdateController updateController = loader.getController();
            updateController.setJobOffer(jobOffer);

            Stage stage = new Stage();
            stage.setTitle("Update Job Offer");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(form));
            stage.showAndWait();

            parentController.refreshJobOfferList();
        } catch (IOException e) {
            System.err.println("Error opening Update Job Offer form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleApply() {
        try {
            Long loggedInUserId = 1L;

            ApplicationJob applicationJob = new ApplicationJob(jobOffer.getIdOffer(), loggedInUserId);
            ApplicationService applicationService = new ApplicationService();
            applicationService.add(applicationJob);

            showAlert(Alert.AlertType.INFORMATION, "Application Successful", "Application Submitted", "Your application for the job offer '" + jobOffer.getTitle() + "' has been submitted.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Application Failed", "Error Submitting Application", "An error occurred while submitting your application: " + e.getMessage());
        }
    }

    private void openJobOfferDetailScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/View/JobOfferApplicationList.fxml"));
            Scene detailScene = new Scene(loader.load());

            JobOfferApplicationList controller = loader.getController();
            controller.setJobOffer(jobOffer);

            // Get the current stage and set the new scene
            Stage currentStage = (Stage) skillsLabel.getScene().getWindow();
            currentStage.setScene(detailScene);
            currentStage.setTitle("Job Offer Details");
            currentStage.setWidth(Screen.getPrimary().getBounds().getWidth());
            currentStage.setHeight(Screen.getPrimary().getBounds().getHeight());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to load details", "An error occurred while loading the job offer details.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
