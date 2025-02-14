package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.concurrent.Task;
import org.example.pathfinder.Model.ApplicationService;
import org.example.pathfinder.Model.ServiceOffre;
import org.example.pathfinder.Service.ApplicationServiceService;
import org.example.pathfinder.Service.ServiceOffreService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

import java.util.List;

public class ApplicationServiceController {

    @FXML
    private VBox servicesContainer; // Container for service offers

    private final ServiceOffreService serviceOffreService = new ServiceOffreService();
    private final ApplicationServiceService applicationServiceService = new ApplicationServiceService();

    @FXML
    public void initialize() {
        if (servicesContainer == null) {
            System.err.println("servicesContainer is NULL. Check FXML file.");
        } else {
            loadServices();
        }
    }

    private void loadServices() {
        servicesContainer.getChildren().clear(); // Clear previous content

        List<ServiceOffre> services = serviceOffreService.getAll(); // Fetch services

        for (ServiceOffre service : services) {
            VBox serviceCard = new VBox(10);
            serviceCard.setStyle("-fx-background-color: #3E3E3E; -fx-padding: 15; -fx-border-radius: 10; -fx-background-radius: 10;");

            Label serviceLabel = new Label("Service: " + service.getTitle() + " | Price: $" + service.getPrice());
            serviceLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

            Button applyButton = new Button("Apply for a Service");
            applyButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
            applyButton.setEffect(new DropShadow(10, Color.GRAY)); // Adding shadow effect to the button

            VBox applicationForm = createApplicationForm(service);
            applicationForm.setVisible(false); // Initially hidden

            applyButton.setOnAction(e -> {
                // Smooth animation for form visibility toggle
                fadeToggleVisibility(applicationForm);
            });

            serviceCard.getChildren().addAll(serviceLabel, applyButton, applicationForm);
            servicesContainer.getChildren().add(serviceCard);
            serviceCard.setOnMouseClicked(event -> openApplicationDetails(service.getId_service()));

        }
    }

    private VBox createApplicationForm(ServiceOffre service) {
        VBox form = new VBox(10);
        form.setStyle("-fx-background-color: #505050; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Label and button styles
        Label applicationLabel = new Label("You are applying for this service.");
        applicationLabel.setStyle("-fx-text-fill: white;");

        Button submitButton = new Button("Submit Application");
        submitButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        submitButton.setEffect(new DropShadow(5, Color.GRAY)); // Shadow for the button

        submitButton.setOnAction(e -> handleApplicationSubmission(service, submitButton, form));

        form.getChildren().addAll(applicationLabel, submitButton);
        return form;
    }

    private void handleApplicationSubmission(ServiceOffre service, Button submitButton, VBox form) {
        // Disable the submit button to avoid multiple clicks
        submitButton.setDisable(true);

        // Validate if necessary (e.g., check if fields are not empty)
        // For now, no validation since the price isn't required
        if (service == null) {
            showErrorMessage("Invalid service selected.");
            return;
        }

        // Run the database operation on a separate thread to avoid UI freezing
        Task<Void> submitTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ApplicationService newApp = new ApplicationService(0, 0.0, 1, "Pending", service.getId_service());

                try {
                    // Simulate database operation
                    applicationServiceService.add(newApp);

                    // After successful submission, update the UI on the main thread
                    javafx.application.Platform.runLater(() -> {
                        form.setVisible(false);
                        showConfirmationMessage(service);
                    });
                } catch (Exception e) {
                    javafx.application.Platform.runLater(() -> showErrorMessage("Error submitting application. Please try again."));
                }

                return null;
            }
        };

        // Run the task in the background
        new Thread(submitTask).start();
    }

    private void fadeToggleVisibility(VBox applicationForm) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), applicationForm);
        fadeTransition.setFromValue(applicationForm.isVisible() ? 1 : 0);
        fadeTransition.setToValue(applicationForm.isVisible() ? 0 : 1);
        fadeTransition.setOnFinished(e -> applicationForm.setVisible(!applicationForm.isVisible()));
        fadeTransition.play();
    }

    private void showConfirmationMessage(ServiceOffre service) {
        Label confirmationLabel = new Label("Your application for the service '" + service.getTitle() + "' has been submitted!");
        confirmationLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px; -fx-font-weight: bold;");
        servicesContainer.getChildren().add(confirmationLabel);

        // Fade out the confirmation message after a few seconds
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(2), confirmationLabel);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> servicesContainer.getChildren().remove(confirmationLabel));
        fadeOut.play();
    }

    private void showErrorMessage(String message) {
        Label errorLabel = new Label(message);
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px; -fx-font-weight: bold;");
        servicesContainer.getChildren().add(errorLabel);

        // Fade out the error message after a few seconds
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(2), errorLabel);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> servicesContainer.getChildren().remove(errorLabel));
        fadeOut.play();
    }
    private void openApplicationDetails(int serviceId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/showApplicationDetails.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the service ID
            ShowApplicationDetailsController controller = loader.getController();
            controller.setServiceId(serviceId); // Send service ID

            // Get the current stage from any component in the scene
            Stage stage = (Stage) servicesContainer.getScene().getWindow();

            // Set new scene in the same stage
            stage.setScene(new Scene(root));
            stage.setTitle("Application Details");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
