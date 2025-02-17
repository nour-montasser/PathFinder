package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
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

    @FXML
    private Button applyButton;

    private boolean isExpanded = false;
    private JobOffer jobOffer;
    private JobOfferService jobOfferService = new JobOfferService();
    private JobOfferListController parentController;


    @FXML
    private Label numberOfSpotsLabel;  // Add a Label for the number of spots
    @FXML
    private Button menuButton;

    private ContextMenu contextMenu;

    @FXML
    private void initialize() {
        // Create the context menu
        contextMenu = new ContextMenu();

        // Create MenuItems
        MenuItem updateItem = new MenuItem("Update");
        updateItem.setOnAction(event -> handleUpdate()); // Handle update action

        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(event -> handleDelete()); // Handle delete action

        // Add items to the context menu
        contextMenu.getItems().addAll(updateItem, deleteItem);

        // Set up the action for the menu button to show the context menu
        menuButton.setOnAction(event -> {
            contextMenu.show(menuButton, javafx.geometry.Side.BOTTOM, 0, 0); // Show context menu below the button
            event.consume(); // Prevent toggleDetails from being triggered
        });
    }



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
        numberOfSpotsLabel.setText("Number of Spots: " + jobOffer.getNumberOfSpots());  // Update the number of spots label

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
            stage.initModality(Modality.APPLICATION_MODAL); // Block interaction with other windows

            // Remove default decorations and prevent resizing
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            stage.setResizable(false);

            // Create an overlay for the transparent effect
            StackPane overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");  // Semi-transparent background

            overlay.getChildren().add(form);

            Scene overlayScene = new Scene(overlay);
            stage.setScene(overlayScene);
            stage.showAndWait();

            // Refresh job offer list after closing the form
            parentController.refreshJobOfferList();
        } catch (IOException e) {
            System.err.println("❌ Error opening Update Job Offer form: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /*@FXML
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
    }*/


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
            currentStage.setMaximized(false); // Temporarily disable maximization
            currentStage.setMaximized(true);
            detailScene.getStylesheets().add(getClass().getResource("/org/example/pathfinder/view/styles.css").toExternalForm());

           /* currentStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
            currentStage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());*/
            currentStage.show();
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
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //--------------------------------------------------------------------------
    @FXML
    public void handleApply() {
        // Check if the number of spots is zero
        if (jobOffer.getNumberOfSpots() == 0) {
            showError("Sorry, there are no available spots for this job offer.");
            return; // Exit the method to prevent opening the application form
        }

        try {
            // Load the application form FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/View/JobOfferApplicationForm.fxml"));
            VBox form = loader.load();

            // Get the controller of the application form
            JobOfferApplicationFormController controller = loader.getController();
            controller.setJobOffer(jobOffer); // Pass the selected job offer

            // Create a new window (Stage) for the application form
            Stage applicationFormStage = new Stage();
            applicationFormStage.setTitle("Job Application Form");
            applicationFormStage.initModality(Modality.APPLICATION_MODAL);  // Make it modal (block interaction with other windows)
            applicationFormStage.setResizable(false);
            applicationFormStage.initStyle(javafx.stage.StageStyle.UNDECORATED);

            // Create a stack pane for the overlay
            StackPane overlay = new StackPane();
            overlay.getChildren().add(form);

            // Set the scene for the application form window
            Scene applicationFormScene = new Scene(overlay);
            applicationFormStage.setScene(applicationFormScene);
            applicationFormStage.showAndWait();
        } catch (IOException e) {
            showError("Error opening application form: " + e.getMessage());
        }
    }




}
