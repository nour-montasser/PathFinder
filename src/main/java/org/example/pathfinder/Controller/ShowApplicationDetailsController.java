package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import org.example.pathfinder.Model.ApplicationService;
import org.example.pathfinder.Service.ApplicationServiceService;
import org.example.pathfinder.Model.ServiceOffre;
import org.example.pathfinder.Service.ServiceOffreService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality; // ✅ Fixed Import
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

public class ShowApplicationDetailsController {

    @FXML
    private Button endServiceButton;
    @FXML
    private VBox applicationsContainer;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Label serviceTitleLabel, servicePriceLabel, serviceDurationLabel, serviceWorkloadLabel, serviceExperienceLabel;
    @FXML
    private Label serviceDescriptionArea;

    private final ServiceOffreService serviceOffreService = new ServiceOffreService();
    private final ApplicationServiceService applicationServiceService = new ApplicationServiceService();
    private int serviceId;

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
        loadServiceDetails();
        loadApplications();
    }

    private void loadServiceDetails() {
        ServiceOffre service = serviceOffreService.getById(serviceId);
        if (service != null) {
            serviceTitleLabel.setText(service.getTitle());
            servicePriceLabel.setText("💰 $" + service.getPrice());
            serviceDurationLabel.setText("⏳ " + service.getDuration());
            serviceExperienceLabel.setText("🎓 " + service.getExperience_level());
            serviceDescriptionArea.setText(service.getDescription());
        }
    }

    private void loadApplications() {
        applicationsContainer.getChildren().clear();
        List<ApplicationService> applications = applicationServiceService.getApplicationsByService(serviceId);

        // 🔹 Fetch Service Details
        ServiceOffre service = serviceOffreService.getById(serviceId);
        boolean isServiceCompleted = service != null && "Completed".equals(service.getStatus());

        // 🔹 If service is completed, hide "End Service" button permanently
        if (isServiceCompleted) {
            endServiceButton.setVisible(false);
        } else {
            // 🔹 Check if a freelancer has been accepted
            boolean hasAccepted = applications.stream().anyMatch(app -> "Accepted".equals(app.getStatus()));
            endServiceButton.setVisible(hasAccepted);
        }

        // 🔹 If there are no applications, show a message
        if (applications.isEmpty()) {
            Label noDataLabel = new Label("No applications available for this service.");
            noDataLabel.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");
            applicationsContainer.getChildren().add(noDataLabel);
            return;
        }

        // 🔹 Keep only the accepted application, remove rejected ones
        applications.removeIf(app -> "Rejected".equals(app.getStatus()));

        // 🔹 Display applications in the UI
        for (ApplicationService app : applications) {
            applicationsContainer.getChildren().add(createApplicationCard(app));
        }
    }



    private VBox createApplicationCard(ApplicationService app) {
        VBox card = new VBox(10);
        card.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-border-radius: 10; "
                + "-fx-background-radius: 10; -fx-border-color: lightgray; -fx-border-width: 1;");
        card.setMinWidth(600);

        Label priceLabel = new Label("💰 Offered: $" + app.getPriceOffre());
        priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");

        Label statusLabel = new Label("📌 Status: " + app.getStatus());
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: black;");

        // Fetch the service status to check if it's completed
        ServiceOffre service = serviceOffreService.getById(app.getIdService());
        boolean isServiceCompleted = "Completed".equals(service.getStatus());

        // Create buttons
        Button acceptButton = new Button("✔ Accept");
        acceptButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        acceptButton.setOnAction(e -> confirmAndUpdateApplicationStatus(app, "Accepted"));
        acceptButton.setDisable(isServiceCompleted);  // ✅ Disable if service is completed

        Button rejectButton = new Button("✖ Reject");
        rejectButton.setStyle("-fx-background-color: #FF3B30; -fx-text-fill: white;");
        rejectButton.setOnAction(e -> confirmAndUpdateApplicationStatus(app, "Rejected"));
        rejectButton.setDisable(isServiceCompleted);  // ✅ Disable if service is completed

        HBox buttons = new HBox(10, acceptButton, rejectButton);
        card.getChildren().addAll(priceLabel, statusLabel, buttons);
        return card;
    }

    private void confirmAndUpdateApplicationStatus(ApplicationService app, String newStatus) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm " + newStatus);
        confirmation.setHeaderText("Are you sure you want to " + newStatus.toLowerCase() + " this application?");
        confirmation.setContentText("This action cannot be undone.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                updateApplicationStatus(app, newStatus);
            }
        });
    }

    private void updateApplicationStatus(ApplicationService app, String newStatus) {
        app.setStatus(newStatus);
        applicationServiceService.update(app);

        if ("Accepted".equals(newStatus)) {
            rejectOtherApplications(app.getIdService(), app.getIdApplication());
        }

        loadApplications();
    }

    private void rejectOtherApplications(int serviceId, int acceptedAppId) {
        List<ApplicationService> allApps = applicationServiceService.getApplicationsByService(serviceId);

        for (ApplicationService application : allApps) {
            if (application.getIdApplication() != acceptedAppId) {
                application.setStatus("Rejected");
                applicationServiceService.update(application);
            }
        }
    }

    @FXML
    private void handleEndService() {
        List<ApplicationService> applications = applicationServiceService.getApplicationsByService(serviceId);

        ApplicationService acceptedApp = applications.stream()
                .filter(app -> "Accepted".equals(app.getStatus()))
                .findFirst()
                .orElse(null);

        if (acceptedApp != null) {
            openRatingModal(acceptedApp);
        }

        serviceOffreService.updateServiceStatus(serviceId, "Completed");

        // 🔹 Refresh applications list after rating submission
        loadApplications();

        // 🔹 Disable End Service Button
        endServiceButton.setDisable(true);


    }

    private void highlightStars(Label[] stars, int rating, int applicationId) {
        for (int i = 0; i < 5; i++) {
            stars[i].setText(i < rating ? "★" : "☆");
            stars[i].setStyle("-fx-font-size: 30px; -fx-text-fill: " + (i < rating ? "gold" : "black") + "; -fx-cursor: hand;");
        }
        applicationServiceService.updateRating(applicationId, rating);
    }

    private void openRatingModal(ApplicationService app) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Rate Freelancer");

        VBox dialogVBox = new VBox(10);
        dialogVBox.setStyle("-fx-padding: 20px; -fx-background-color: white;");

        Label ratingLabel = new Label("⭐ Rate the freelancer:");
        ratingLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        HBox starBox = new HBox(5);
        Label[] stars = new Label[5];

        for (int i = 0; i < 5; i++) {
            stars[i] = new Label("☆");
            stars[i].setStyle("-fx-font-size: 30px; -fx-cursor: hand;");
            final int starValue = i + 1;
            stars[i].setOnMouseClicked(event -> highlightStars(stars, starValue, app.getIdApplication()));
            starBox.getChildren().add(stars[i]);
        }

        highlightStars(stars, app.getRating(), app.getIdApplication()); // ✅ Fix: Add app.getIdApplication()


        Button submitButton = new Button("Submit Review");
        submitButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        submitButton.setOnAction(e -> {
            int rating = getStarRating(stars);
            applicationServiceService.updateRating(app.getIdApplication(), rating);
            dialog.close();
        });

        dialogVBox.getChildren().addAll(ratingLabel, starBox, submitButton);
        Scene dialogScene = new Scene(dialogVBox, 400, 250);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }



    private int getStarRating(Label[] stars) {
        int rating = 0;
        for (Label star : stars) {
            if ("★".equals(star.getText())) {
                rating++;
            }
        }
        return rating;
    }
}
