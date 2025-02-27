package org.example.pathfinder.Controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import org.example.pathfinder.Model.JobOffer;
import org.example.pathfinder.Model.LoggedUser;
import org.example.pathfinder.Service.ApplicationService;
import org.example.pathfinder.Service.JobOfferService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class JobOfferListCardController {

    @FXML
    private ImageView companyImage;

    @FXML
    private Label titleLabel;
    @FXML
    private Label addressLabel;

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
    private Button menuButton;

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
    private ApplicationService applicationService = new ApplicationService();
    private JobOfferListController parentController;
    private String loggedUserRole = LoggedUser.getInstance().getRole();
    private long loggedUserid = LoggedUser.getInstance().getUserId();


    @FXML
    private Label numberOfSpotsLabel;  // Add a Label for the number of spots

    private ContextMenu contextMenu;
    @FXML
    private void initialize() {
        menuButton.setVisible(false);
        // Hide apply button and actions menu if the role is "COMPANY"
        if (loggedUserRole.equals("COMPANY")) {
            applyButton.setVisible(false);

            contextMenu = new ContextMenu();
            // Create MenuItems
            MenuItem updateItem = new MenuItem("Update");
            updateItem.setOnAction(event -> handleUpdate()); // Handle update action
            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(event -> handleDelete()); // Handle delete action

            contextMenu.getItems().addAll(updateItem, deleteItem);

            menuButton.setOnAction(event -> {
                if (!contextMenu.isShowing()) {
                    contextMenu.show(menuButton, javafx.geometry.Side.BOTTOM, 0, 0); // Show context menu below the button
                    event.consume(); // Prevent other actions from firing
                }
            });
        } else if (loggedUserRole.equals("SEEKER")) {
            applyButton.setVisible(true);   // Make apply button visible for seekers
            menuButton.setVisible(false); // Hide actions menu for seekers
            titleLabel.setOnMouseClicked(event -> showError("You cannot view the details as a job seeker."));
        }
    }




    public void setJobOffer(JobOffer jobOffer) {
        this.jobOffer = jobOffer;
        String url = applicationService.getUserProfilePicture(jobOffer.getIdUser());
        File imageFile = new File(url);
        if (imageFile.exists()) {
            companyImage.setImage(new Image(imageFile.toURI().toString()));
        }

        titleLabel.setText(jobOffer.getTitle());
        descriptionLabel.setText(jobOffer.getDescription());
        requiredEducationLabel.setText("Required Education: " + jobOffer.getRequiredEducation());
        requiredExperienceLabel.setText("Required Experience: " + jobOffer.getRequiredExperience());
        skillsLabel.setText("Skills: " + jobOffer.getSkills());
        numberOfSpotsLabel.setText("Number of Spots: " + jobOffer.getNumberOfSpots());
        addressLabel.setText("Address: "+jobOffer.getAddress());
        additionalDetails.setVisible(false);
        additionalDetails.setManaged(false);

        // Ensure menu button only appears if the logged-in user is the owner of the job offer
        if (loggedUserRole.equals("COMPANY")) {
            applyButton.setVisible(false);
            menuButton.setVisible(jobOffer.getIdUser() == loggedUserid);
            if(jobOffer.getIdUser() == loggedUserid) {titleLabel.setOnMouseClicked(event -> openJobOfferDetailScene()); menuButton.setOnAction(event -> {
                if (!contextMenu.isShowing()) {
                    contextMenu.show(menuButton, javafx.geometry.Side.BOTTOM, 0, 0); // Show context menu below the button
                    event.consume(); // Prevent other actions from firing
                }
            });}
        } else {
            applyButton.setVisible(true);
            menuButton.setVisible(false);
        }
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
                jobOfferService.delete(jobOffer.getIdOffer());

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Frontoffice/JobOfferUpdate.fxml"));
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
            // Charger la vue principale (navbar + contentArea)
            FXMLLoader frontOfficeLoader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Frontoffice/main-frontoffice.fxml"));
            Parent frontOfficeView = frontOfficeLoader.load();
            FrontOfficeController frontOfficeController = frontOfficeLoader.getController();

            // Charger la page des détails de l'offre d'emploi
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Frontoffice/JobOfferApplicationList.fxml"));
            Parent jobOfferDetailView = loader.load();

            // Obtenir le contrôleur et lui passer l'offre d'emploi
            JobOfferApplicationListController controller = loader.getController();
            controller.setJobOffer(jobOffer);

            // Injecter la vue des détails dans le contentArea
            frontOfficeController.loadView(jobOfferDetailView); // Méthode déjà ajoutée à FrontOfficeController

            // Obtenir la fenêtre actuelle (Stage)
            Stage currentStage = (Stage) skillsLabel.getScene().getWindow();

            // Recréer la scène avec le layout mis à jour
            Scene detailScene = new Scene(frontOfficeView);
            detailScene.getStylesheets().add(getClass().getResource("/org/example/pathfinder/view/Frontoffice/styles.css").toExternalForm());


            // Appliquer la nouvelle scène et forcer le redimensionnement
            currentStage.setScene(detailScene);
            currentStage.setTitle("Job Offer Details");
            currentStage.setMaximized(false);
            currentStage.setMaximized(true);
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
        // Get the logged user ID and role
        String loggedUserRole = LoggedUser.getInstance().getRole();
        long loggedUserId = LoggedUser.getInstance().getUserId();  // Assuming you have a method to get user ID

        // Check if the logged user is a seeker
        if (!loggedUserRole.equals("SEEKER")) {
            showError("You do not have permission to apply for jobs.");
            return;  // Prevent further processing
        }

        // Check if the user has already applied for the job
        if (applicationService.hasUserAppliedForJob(jobOffer, loggedUserId)) {
            showError("You have already applied for this job.");
            return;  // Exit the method to prevent opening the application form
        }

        // Check if the number of spots is zero
        if (jobOffer.getNumberOfSpots() == 0) {
            showError("Sorry, there are no available spots for this job offer.");
            return;  // Exit the method to prevent opening the application form
        }

        // Existing apply logic here
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Frontoffice/JobOfferApplicationForm.fxml"));
            VBox form = loader.load();

            JobOfferApplicationFormController controller = loader.getController();
            controller.setJobOffer(jobOffer); // Pass the selected job offer

            Stage applicationFormStage = new Stage();
            applicationFormStage.setTitle("Job Application Form");
            applicationFormStage.initModality(Modality.APPLICATION_MODAL);  // Make it modal
            applicationFormStage.setResizable(false);
            applicationFormStage.initStyle(javafx.stage.StageStyle.UNDECORATED);

            StackPane overlay = new StackPane();
            overlay.getChildren().add(form);

            Scene applicationFormScene = new Scene(overlay);
            applicationFormStage.setScene(applicationFormScene);
            applicationFormStage.showAndWait();
        } catch (IOException e) {
            System.out.println( "error opening form"+ e.getMessage());
        }
    }

    public void onMouseEnterTitle(Event event) {
        Label source = (Label) event.getSource();
        source.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0078D4;");
    }

    // Handle mouse exit event for title label
    public void onMouseExitTitle(Event event) {
        Label source = (Label) event.getSource();
        source.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");
    }

    // Handle mouse enter event for buttons
    public void onMouseEnterButton(Event event) {
        Button source = (Button) event.getSource();
        source.setStyle("-fx-background-color: #5B3F29; -fx-text-fill: white; -fx-font-size: 14px; ");
    }

    // Handle mouse exit event for buttons
    public void onMouseExitButton(Event event) {
        Button source = (Button) event.getSource();
        source.setStyle("-fx-background-color: #3B261DFF; -fx-text-fill: white; -fx-font-size: 14px;");
    }





        // Handle mouse enter event for the card (VBox)
        public void onCardHover(Event event) {
            VBox cardContainer = (VBox) event.getSource(); // Get the VBox container (card)

            // Get the width and height of the container to calculate the center
            double centerX = cardContainer.getBoundsInLocal().getWidth() / 2;
            double centerY = cardContainer.getBoundsInLocal().getHeight() / 2;

            // Create a scaling transformation and set the pivot point to the center
            Scale scale = new Scale(1.05, 1.05, centerX, centerY);  // Scale by 5% larger
            cardContainer.getTransforms().add(scale);  // Apply the scale transformation
        }

        // Handle mouse exit event for the card (VBox)
        public void onCardExit(Event event) {
            VBox cardContainer = (VBox) event.getSource(); // Get the VBox container (card)
            cardContainer.getTransforms().clear();  // Remove any applied transformations, returning to normal size
        }



}
