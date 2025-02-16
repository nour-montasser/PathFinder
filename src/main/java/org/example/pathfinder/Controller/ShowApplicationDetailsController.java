package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import org.example.pathfinder.Model.ApplicationService;
import org.example.pathfinder.Service.ApplicationServiceService;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

public class ShowApplicationDetailsController {

    @FXML
    private VBox applicationsContainer; // The container where applications will be added dynamically

    @FXML
    private ScrollPane scrollPane; // To make sure content is scrollable

    private final ApplicationServiceService applicationServiceService = new ApplicationServiceService();
    private int serviceId; // The service ID received from the previous screen

    /**
     * This function is called when navigating from ApplicationServiceController.
     * It sets the service ID and loads applications related to this service.
     */
    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
        loadApplications(); // Fetch and display applications for this service
    }

    /**
     * Fetch applications from the database for the given service and display them dynamically.
     */
    private void loadApplications() {
        applicationsContainer.getChildren().clear(); // Clear previous content

        List<ApplicationService> applications = applicationServiceService.getApplicationsByService(serviceId);

        if (applications.isEmpty()) {
            Label noDataLabel = new Label("No applications available for this service.");
            noDataLabel.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");
            applicationsContainer.getChildren().add(noDataLabel);
            return;
        }

        for (ApplicationService app : applications) {
            applicationsContainer.getChildren().add(createApplicationRow(app));
        }
    }

    /**
     * Create an HBox row for each application, including details and buttons.
     */
    private HBox createApplicationRow(ApplicationService app) {
        HBox row = new HBox(20);
        row.setStyle("-fx-padding: 15; -fx-background-color: " + getStatusColor(app.getStatus()) +
                "; -fx-border-radius: 10; -fx-background-radius: 10;");
        row.setMinHeight(60);

        // Labels for displaying application details


        Label priceLabel = new Label(app.getPriceOffre() > 0 ? "💰 Price Offered: $" + app.getPriceOffre() : "💲 Negotiable");
        priceLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        Label statusLabel = new Label("📌 Status: " + app.getStatus());
        statusLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");


        // Accept Button
        Button acceptButton = new Button("✔ Accept");
        acceptButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 15;");
        acceptButton.setOnAction(e -> updateApplicationStatus(app, "Accepted"));

        // Reject Button
        Button rejectButton = new Button("✖ Reject");
        rejectButton.setStyle("-fx-background-color: #FF3B30; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 15;");
        rejectButton.setOnAction(e -> updateApplicationStatus(app, "Rejected"));

        // Add components to row
        row.getChildren().addAll( priceLabel, statusLabel, acceptButton, rejectButton);

        return row;
    }

    /**
     * Updates the status of an application and refreshes the list.
     */
    private void updateApplicationStatus(ApplicationService app, String newStatus) {
        app.setStatus(newStatus);
        applicationServiceService.update(app); // Update in database
        loadApplications(); // Refresh UI
    }

    /**
     * Get color for different statuses
     */
    private String getStatusColor(String status) {
        switch (status) {
            case "Accepted":
                return "#2E7D32"; // Green
            case "Rejected":
                return "#B71C1C"; // Red
            default:
                return "#424242"; // Dark Grey for Pending
        }
    }

    @FXML
    private Button addServiceButton; // Link to the FXML button

    /**
     * Open the modal to add a new service
     */
    @FXML
    private void openServiceForumModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/ServiceForum.fxml"));
            Parent root = loader.load();

            // Create modal window
            Stage modalStage = new Stage();
            modalStage.setScene(new Scene(root));
            modalStage.setTitle("Add a New Service");
            modalStage.initOwner(addServiceButton.getScene().getWindow()); // Set parent window
            modalStage.setResizable(false);

            // Show modal and wait for it to close
            modalStage.showAndWait();

            // Refresh the service list in the main UI
            loadApplications();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
