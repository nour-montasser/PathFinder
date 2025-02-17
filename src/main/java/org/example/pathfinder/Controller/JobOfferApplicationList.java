package org.example.pathfinder.Controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.example.pathfinder.Model.ApplicationJob;
import org.example.pathfinder.Model.JobOffer;
import org.example.pathfinder.Service.ApplicationService;
import org.example.pathfinder.Service.JobOfferService;

import java.io.IOException;
import java.sql.SQLException;

public class JobOfferApplicationList {

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

    private ObservableList<ApplicationJob> applicationsObservableList = FXCollections.observableArrayList();
    private JobOffer jobOffer;
    private ApplicationService applicationService = new ApplicationService();
    private JobOfferService jobOfferService = new JobOfferService();

    public void setJobOffer(JobOffer jobOffer) throws SQLException {
        this.jobOffer = jobOffer;
        String imagePath = getClass().getResource("/org/example/pathfinder/Sources/pathfinder_logo_compass.png").toString();
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

        // Set the custom cell factory for the ListView
        applicationsListView.setCellFactory(param -> new ListCell<ApplicationJob>() {
            @Override
            protected void updateItem(ApplicationJob item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Create a VBox to represent the card
                    VBox card = new VBox(3);
                    card.setStyle("-fx-padding: 7; -fx-border-radius: 10; -fx-background-color: white; -fx-border-color: lightgray;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 2, 5); -fx-margin: 10 0 10 0;-fx-background-radius: 10;");

                    Label userLabel = new Label("User ID: " + item.getIdUser());
                    userLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

                    Label statusLabel = new Label("Status: " + item.getStatus());
                    statusLabel.setStyle("-fx-text-fill: " + (item.getStatus().equals("Accepted") ? "#4CAF50" :
                            item.getStatus().equals("Rejected") ? "#f44336" : "#777") + ";");

                    Label dateLabel = new Label("Applied on: " + item.getDateApplication());
                    dateLabel.setStyle("-fx-text-fill: #777;");

                    // Create a HBox for the action buttons
                    HBox buttonBox = new HBox(5);
                    buttonBox.setStyle("-fx-alignment: center-right;");

                    // Show the "Accept" and "Reject" buttons only when the status is "pending"
                    if ("pending".equals(item.getStatus())) {
                        Button acceptButton = new Button("Accept");
                        acceptButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 12px;");
                        acceptButton.setOnAction(event -> handleStatusUpdate(item, "Accepted"));

                        Button rejectButton = new Button("Reject");
                        rejectButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 12px;");
                        rejectButton.setOnAction(event -> handleStatusUpdate(item, "Rejected"));

                        buttonBox.getChildren().addAll(acceptButton, rejectButton);
                    }

                    // Create a HBox for the "Delete" button (always visible for "pending" applications)
                    HBox deleteBox = new HBox(5);
                    deleteBox.setStyle("-fx-alignment: center-left;");




                        card.getChildren().addAll(userLabel, statusLabel, dateLabel, buttonBox);


                    setGraphic(card);
                }
            }
        });
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


    /*private void handleDelete(ApplicationJob application) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Application");
        alert.setHeaderText("Are you sure you want to delete this application?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                applicationService.delete(application);
                applicationsObservableList.remove(application);  // Update list
                showAlert(Alert.AlertType.INFORMATION, "Application Deleted", null, "The application has been successfully deleted.");
            }
        });
    }*/

    @FXML
    private void handleClose() {
        try {
            // Load the Job Offer List scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/JobOfferList.fxml"));
            Parent root = loader.load();
            // Get the current stage (the window)
            Stage stage = (Stage) jobOfferTitle.getScene().getWindow();
            // Set the new scene
            Scene newScene = new Scene(root);
            stage.setScene(newScene);
            stage.setMaximized(false); // Temporarily disable maximization
            stage.setMaximized(true);
            // Optional: Set the window size to full screen (maximized) or custom size
            /*stage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
            stage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());*/
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
}
