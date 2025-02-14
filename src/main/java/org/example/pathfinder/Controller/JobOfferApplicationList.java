package org.example.pathfinder.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.pathfinder.Model.ApplicationJob;
import org.example.pathfinder.Model.JobOffer;
import org.example.pathfinder.Service.ApplicationService;

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

    private ObservableList<ApplicationJob> applicationsObservableList = FXCollections.observableArrayList();
    private JobOffer jobOffer;
    private ApplicationService applicationService = new ApplicationService();

    public void setJobOffer(JobOffer jobOffer) throws SQLException {
        this.jobOffer = jobOffer;

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
                    card.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-color: white;");

                    Label userLabel = new Label("User ID: " + item.getIdUser());
                    userLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                    Label statusLabel = new Label("Status: " + item.getStatus());
                    statusLabel.setStyle("-fx-text-fill: " + (item.getStatus().equals("Accepted") ? "#4CAF50" : item.getStatus().equals("Rejected") ? "#f44336" : "#777") + ";");

                    Label dateLabel = new Label("Applied on: " + item.getDateApplication());
                    dateLabel.setStyle("-fx-text-fill: #777;");

                    VBox buttonBox = new VBox(10);

                    // Accept and Reject buttons only for Pending status
                    if ("pending".equals(item.getStatus())) {
                        Button acceptButton = new Button("Accept");
                        acceptButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand;");
                        acceptButton.setOnAction(event -> handleStatusUpdate(item, "Accepted"));

                        Button rejectButton = new Button("Reject");
                        rejectButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-cursor: hand;");
                        rejectButton.setOnAction(event -> handleStatusUpdate(item, "Rejected"));

                        HBox actionButtons = new HBox(10, acceptButton, rejectButton);
                        buttonBox.getChildren().add(actionButtons);
                    }

                    // Delete button (always visible)
                    Button deleteButton = new Button("Delete");
                    deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-cursor: hand;");
                    deleteButton.setOnAction(event -> handleDelete(item));

                    buttonBox.getChildren().add(deleteButton);

                    card.getChildren().addAll(userLabel, statusLabel, dateLabel, buttonBox);
                    setGraphic(card);
                }
            }
        });


    }

    private void handleStatusUpdate(ApplicationJob application, String newStatus) {
        if (!"pending".equals(application.getStatus())) {
            showAlert(Alert.AlertType.WARNING, "Status Update Not Allowed", null, "you cant change the status of this application again");
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
                showAlert(Alert.AlertType.INFORMATION, "Status Updated", null, "The application status has been updated to " + newStatus + ".");
            }
        });
    }

    private void handleDelete(ApplicationJob application) {
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
    }

    @FXML
    private void handleClose() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/JobOfferList.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) jobOfferTitle.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
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
