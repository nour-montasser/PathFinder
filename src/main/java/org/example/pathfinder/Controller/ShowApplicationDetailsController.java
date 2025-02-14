package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.pathfinder.Model.ApplicationService;
import org.example.pathfinder.Service.ApplicationServiceService;

import java.util.List;

public class ShowApplicationDetailsController {

    @FXML
    private VBox applicationsContainer; // The container where applications will be added dynamically

    private final ApplicationServiceService applicationServiceService = new ApplicationServiceService();
    private int serviceId; // The service ID received from the previous screen

    /**
     * This function is called when navigating from ApplicationServiceController.
     * It sets the service ID and loads applications related to this service.
     *
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
            noDataLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
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
        HBox row = new HBox(15);
        row.setStyle("-fx-padding: 10; -fx-background-color: #3A3A3A; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Labels for displaying application details
        Label appIdLabel = new Label("ID: " + app.getIdApplication());
        appIdLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Label priceLabel = new Label("Price: $" + app.getPriceOffre());
        priceLabel.setStyle("-fx-text-fill: white;");

        Label statusLabel = new Label("Status: " + app.getStatus());
        statusLabel.setStyle("-fx-text-fill: white;");

        Label serviceIdLabel = new Label("Service ID: " + app.getIdService());
        serviceIdLabel.setStyle("-fx-text-fill: white;");

        // Accept Button
        Button acceptButton = new Button("Accept");
        acceptButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        acceptButton.setOnAction(e -> updateApplicationStatus(app, "Accepted"));

        // Reject Button
        Button rejectButton = new Button("Reject");
        rejectButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        rejectButton.setOnAction(e -> updateApplicationStatus(app, "Rejected"));

        // Add components to row
        row.getChildren().addAll(appIdLabel, priceLabel, statusLabel, serviceIdLabel, acceptButton, rejectButton);

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
}
