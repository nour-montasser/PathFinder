package org.example.pathfinder.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.pathfinder.Model.ApplicationJob;
import org.example.pathfinder.Model.CoverLetter;
import org.example.pathfinder.Model.JobOffer;
import org.example.pathfinder.Service.ApplicationService;
import org.example.pathfinder.Service.CoverLetterService;
import org.example.pathfinder.Service.JobOfferService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class JobOfferApplicationListController {

    @FXML
    private Label jobOfferTitle;

    @FXML
    private Label jobOfferDescription;

    @FXML
    private Label requiredEducationLabel;

    @FXML
    private Label requiredExperienceLabel;

    @FXML
    private Label skillsLabel;

    @FXML
    private ListView<ApplicationJob> applicationsListView;
    @FXML
    private ImageView searchIcon;

    @FXML
    private ComboBox<String> statusFilterComboBox;

    @FXML
    private TextField searchField;

    private ApplicationService applicationJobService;
    private ObservableList<ApplicationJob> displayedApplications;

    private ObservableList<ApplicationJob> applicationsObservableList = FXCollections.observableArrayList();
    private JobOffer jobOffer;
    private ApplicationService applicationService = new ApplicationService();
    private JobOfferService jobOfferService = new JobOfferService();
    private CoverLetterService coverLetterService = new CoverLetterService();

    public void setJobOffer(JobOffer jobOffer) throws SQLException {
        this.jobOffer = jobOffer;
        String imagePath = getClass().getResource("/org/example/pathfinder/view/Sources/pathfinder_logo_compass.png").toString();
        searchIcon.setImage(new Image(imagePath));
        // Set job offer details
        jobOfferTitle.setText(jobOffer.getTitle());
        jobOfferDescription.setText(jobOffer.getDescription());
        requiredEducationLabel.setText("Required Education: " + jobOffer.getRequiredEducation());
        requiredExperienceLabel.setText("Required Experience: " + jobOffer.getRequiredExperience());
        skillsLabel.setText("Skills: " + jobOffer.getSkills());

        loadApplicationsForJobOffer();
    }

    private void loadApplicationsForJobOffer() throws SQLException {
        applicationsObservableList.clear();
        applicationsObservableList.addAll(applicationService.getApplicationsForJobOffer(jobOffer.getIdOffer()));
        applicationsListView.setItems(applicationsObservableList);

        applicationsListView.setCellFactory(param -> new ListCell<ApplicationJob>() {
            @Override
            protected void updateItem(ApplicationJob item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox card = new VBox(5);
                    card.setStyle("-fx-padding: 7; -fx-border-radius: 10; -fx-background-color: white; -fx-border-color: lightgray;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 2, 5); -fx-background-radius: 10;");

                    // Fetch profile picture
                    String profilePhotoPath = applicationService.getUserProfilePicture(item.getIdUser());

                    // Profile Picture
                    ImageView profileImageView = new ImageView();
                    if (profilePhotoPath != null && !profilePhotoPath.isEmpty()) {
                        try {
                            profileImageView.setImage(new Image(profilePhotoPath, 40, 40, true, true));
                        } catch (Exception e) {
                            profileImageView.setImage(getDefaultProfileImage());
                        }
                    } else {
                        profileImageView.setImage(getDefaultProfileImage());
                    }
                    profileImageView.setFitWidth(40);
                    profileImageView.setFitHeight(40);
                    profileImageView.setStyle("-fx-border-radius: 50%; -fx-background-radius: 50%;");

                    String userName = applicationService.getUserNameById(item.getIdUser());
                    Label userLabel = new Label(userName);
                    userLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

                    // MenuButton for actions
                    MenuButton actionsMenu = new MenuButton();
                    actionsMenu.setStyle("-fx-background-color: transparent; -fx-font-size: 16px;");
                    MenuItem showCoverLetterItem = new MenuItem("Show Cover Letter");
                    showCoverLetterItem.setOnAction(event -> handleShowCoverLetter(item));
                    MenuItem showCvItem = new MenuItem("Show CV");
                    actionsMenu.getItems().addAll(showCoverLetterItem, showCvItem);

                    // Align profile image, user name to the left, and menu to the right
                    HBox headerBox = new HBox(10);
                    HBox.setHgrow(userLabel, Priority.ALWAYS);
                    headerBox.setAlignment(Pos.CENTER_LEFT);
                    headerBox.getChildren().addAll(profileImageView, userLabel, actionsMenu);
                    headerBox.setSpacing(10);
                    headerBox.setStyle("-fx-alignment: center-left;");

                    Label statusLabel = new Label("Status: " + item.getStatus());
                    statusLabel.setStyle("-fx-text-fill: " + (item.getStatus().equals("Accepted") ? "#4CAF50" :
                            item.getStatus().equals("Rejected") ? "#f44336" : "#777") + ";");

                    Label dateLabel = new Label("Applied on: " + item.getDateApplication());
                    dateLabel.setStyle("-fx-text-fill: #777;");

                    HBox buttonBox = new HBox(5);
                    buttonBox.setStyle("-fx-alignment: center-right;");

                    if ("pending".equals(item.getStatus())) {
                        Button acceptButton = new Button("Accept");
                        acceptButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 12px;");
                        acceptButton.setOnAction(event -> handleStatusUpdate(item, "Accepted"));

                        Button rejectButton = new Button("Reject");
                        rejectButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 12px;");
                        rejectButton.setOnAction(event -> handleStatusUpdate(item, "Rejected"));

                        buttonBox.getChildren().addAll(acceptButton, rejectButton);
                    }

                    // Add everything to the card
                    card.getChildren().addAll(headerBox, statusLabel, dateLabel, buttonBox);

                    setText(null);
                    setGraphic(card);
                }
            }
        });
    }


        private Image getDefaultProfileImage() {
        URL resource = getClass().getResource("/org/example/pathfinder/view/Sources/default_profile.jpeg");
        if (resource != null) {
            return new Image(resource.toExternalForm(), 40, 40, true, true);
        }
        return new Image("https://via.placeholder.com/40"); // Fallback URL if resource is missing
    }






    private void handleStatusUpdate(ApplicationJob application, String newStatus) {
        if (!"pending".equals(application.getStatus())) {
            showAlert(Alert.AlertType.WARNING, "Status Update Not Allowed", null, "You can't change the status of this application again.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Update Status");
        alert.setHeaderText("Are you sure you want to update the status to " + newStatus + "?");
        alert.setContentText("This action will change the application's status to " + newStatus + ".");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                application.setStatus(newStatus);
                applicationService.update(application);  // Assuming update() is a method in ApplicationService
                applicationsListView.refresh();  // Refresh the ListView to reflect changes

                // Decrement the number of spots available for the job offer if accepted
                if ("Accepted".equals(newStatus)) {
                    decrementJobOfferSpots();
                }

                showAlert(Alert.AlertType.INFORMATION, "Status Updated", null, "The application status has been updated to " + newStatus + ".");
            }
        });
    }

    private void decrementJobOfferSpots() {
        int currentSpots = jobOffer.getNumberOfSpots();
        if (currentSpots > 0) {
            jobOffer.setNumberOfSpots(currentSpots - 1);
            jobOfferService.update(jobOffer);  // Assuming updateJobOffer() method in ApplicationService to update the job offer in DB
        } else {
            showAlert(Alert.AlertType.WARNING, "No Available Spots" ,null,"The job offer has no available spots left.");
        }
    }

    @FXML
    private void handleClose() {
        try {
            // Load the Job Offer List scene
            FXMLLoader jobOfferListLoader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Frontoffice/JobOfferList.fxml"));
            Parent jobOfferListView = jobOfferListLoader.load();

            // Load the FrontOffice (navbar) view
            FXMLLoader frontOfficeLoader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Frontoffice/main-frontoffice.fxml"));
            Parent frontOfficeView = frontOfficeLoader.load();
            FrontOfficeController frontOfficeController = frontOfficeLoader.getController();

            // Create a StackPane to hold both the FrontOffice navbar and the Job Offer List content
            StackPane root = new StackPane();

            // Add the FrontOffice navbar at the top (it will stay fixed)
            root.getChildren().add(frontOfficeView);

            // Add the JobOfferList content as the main content area
            root.getChildren().add(jobOfferListView);

            // Create a new Scene with the combined layout (FrontOffice + JobOffer List)
            Scene newScene = new Scene(root);
            newScene.getStylesheets().add(getClass().getResource("/org/example/pathfinder/view/Frontoffice/styles.css").toExternalForm());

            // Get the current stage (the window)
            Stage stage = (Stage) jobOfferTitle.getScene().getWindow();
            // Set the new scene
            stage.setScene(newScene);
            stage.setMaximized(false); // Temporarily disable maximization
            stage.setMaximized(true);  // Re-enable maximization
            // Show the stage with the new scene
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load the Job Offer List", e.getMessage());
        }
    }





    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleShowCoverLetter(ApplicationJob application) {
        try {
            // Fetch the cover letter associated with the application
            CoverLetter coverLetter = coverLetterService.getCoverLetterByApplication(application.getApplicationId());

            if (coverLetter != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Frontoffice/CoverLetterView.fxml"));
                VBox showCoverLetterView = loader.load();

                CoverLetterViewController controller = loader.getController();
                controller.setCoverLetter(coverLetter);  // Pass the cover letter to the controller

                Stage coverLetterStage = new Stage();
                coverLetterStage.setTitle("Cover Letter");
                coverLetterStage.initModality(Modality.APPLICATION_MODAL);
                coverLetterStage.setResizable(false);
                coverLetterStage.initStyle(javafx.stage.StageStyle.UNDECORATED);

                Scene coverLetterScene = new Scene(showCoverLetterView);
                coverLetterStage.setScene(coverLetterScene);
                coverLetterStage.showAndWait();
            } else {
                showError("No cover letter found for this application.");
            }

        } catch (IOException e) {
            showError("Error displaying cover letter: " + e.getMessage());
        }
    }
    private void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Error", null, message);
    }

    private void showInfo(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Information", null, message);
    }

    @FXML
    public void initialize() {
        applicationJobService = new ApplicationService();
        displayedApplications = FXCollections.observableArrayList();

        statusFilterComboBox.setItems(FXCollections.observableArrayList("All", "Pending", "Accepted", "Rejected"));
        statusFilterComboBox.getSelectionModel().select("All");
        statusFilterComboBox.setOnAction(event -> loadApplications(statusFilterComboBox.getSelectionModel().getSelectedItem()));
        loadApplications("All");

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterApplicationsBySearch(newValue);
        });
    }
    private void filterApplicationsBySearch(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            applicationsListView.setItems(applicationsObservableList);
        } else {
            String lowerCaseSearch = searchText.toLowerCase();
            ObservableList<ApplicationJob> filteredList = applicationsObservableList.filtered(applicationJob ->
                    applicationService.getUserNameById(applicationJob.getIdUser()).toLowerCase().contains(lowerCaseSearch)
            );

            applicationsListView.setItems(filteredList);
        }
    }


    private void loadApplications(String status) {
        List<ApplicationJob> applications = applicationJobService.getByStatus(status);
        displayedApplications.setAll(applications);
        applicationsListView.setItems(displayedApplications);
    }

}
