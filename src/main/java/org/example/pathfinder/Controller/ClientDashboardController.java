package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.example.pathfinder.Model.ApplicationService;
import org.example.pathfinder.Model.ServiceOffre;
import org.example.pathfinder.Service.ApplicationServiceService;
import org.example.pathfinder.Service.ServiceOffreService;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ClientDashboardController {

    @FXML
    private GridPane servicesGrid; // Grid to display services

    @FXML
    private TextField searchField; // Search bar

    private final ServiceOffreService serviceOffreService = new ServiceOffreService();
    private final ApplicationServiceService applicationServiceService = new ApplicationServiceService();
    private List<ServiceOffre> allServices; // Store all services initially

    /**
     * Initializes the client dashboard.
     */
    @FXML
    public void initialize() {
        if (servicesGrid == null) {
            System.out.println("Error: servicesGrid is null. Check your FXML file.");
            return;
        }

        loadServices(); // Load services at startup

        // ✅ Add dynamic search listener
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> onSearchTextChanged(newVal));
        }
    }

    /**
     * Load all services from the database and display them.
     */
    private void loadServices() {
        allServices = serviceOffreService.getAll(); // Fetch all services from database
        displayServices(allServices); // Show services in the grid
    }

    /**
     * Display services in a grid layout.
     */
    private void displayServices(List<ServiceOffre> services) {
        if (servicesGrid == null) {
            System.out.println("Error: servicesGrid is null.");
            return;
        }

        servicesGrid.getChildren().clear();

        int column = 0;
        int row = 0;

        for (ServiceOffre service : services) {
            servicesGrid.add(createServiceCard(service), column++, row);

            if (column == 3) { // 3 cards per row
                column = 0;
                row++;
            }
        }
    }

    /**
     * Creates a UI card for displaying a service offer.
     */
    private StackPane createServiceCard(ServiceOffre service) {
        StackPane card = new StackPane();
        card.setStyle("-fx-background-color: white; " +
                "-fx-border-radius: 10px; " +
                "-fx-padding: 15px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 10, 0, 0, 5); " +
                "-fx-min-width: 250px; " +
                "-fx-min-height: 200px;");

        VBox content = new VBox(10);

        Label titleLabel = createLabel("Service: " + service.getTitle(), 18, "#512E1B", true);
        Label priceLabel = createLabel("Starting Price: $" + service.getPrice(), 16, "#98BFD1", true);
        Label descriptionLabel = createLabel("Description: " + service.getDescription(), 14, "black", false);
        Label dateLabel = createLabel("Posted on: " + service.getDate_posted(), 14, "black", false);

        Button applyButton = new Button("Apply");
        applyButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px;");
        applyButton.setOnAction(event -> openApplicationModal(service));

        content.getChildren().addAll(titleLabel, priceLabel, descriptionLabel, dateLabel, applyButton);
        card.getChildren().add(content);
        return card;
    }

    /**
     * Helper method to create a formatted label.
     */
    private Label createLabel(String text, int fontSize, String color, boolean bold) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + color + "; " +
                "-fx-font-size: " + fontSize + "px; " +
                (bold ? "-fx-font-weight: bold;" : ""));
        return label;
    }

    /**
     * Opens a modal window to apply for a service by entering the price.
     */
    private void openApplicationModal(ServiceOffre service) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Apply for Service");

        VBox dialogVBox = new VBox(10);
        dialogVBox.setStyle("-fx-padding: 20px;");

        Label infoLabel = new Label("Enter your offered price:");
        TextField priceField = new TextField();
        priceField.setPromptText("Enter price...");

        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        submitButton.setOnAction(event -> {
            handleSubmitApplication(service, priceField.getText());
            dialog.close();
        });

        dialogVBox.getChildren().addAll(infoLabel, priceField, submitButton);

        Scene dialogScene = new Scene(dialogVBox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    /**
     * Handles submitting an application when the user clicks "Submit".
     */
    private void handleSubmitApplication(ServiceOffre service, String priceText) {
        if (priceText.isEmpty()) {
            showErrorAlert("Error", "Please enter a valid price!");
            return;
        }

        double offeredPrice;
        try {
            offeredPrice = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            showErrorAlert("Error", "Invalid price format! Enter a numeric value.");
            return;
        }

        // ✅ Validate that the offered price is at least the service's starting price
        if (offeredPrice < service.getPrice()) {
            showErrorAlert("Error", "Your offered price must be at least $" + service.getPrice());
            return;
        }

        // Create new application entry
        ApplicationService application = new ApplicationService(
                0, // Auto-incremented ID
                offeredPrice,
                0, // Default user ID, update when user authentication is available
                "Pending",
                service.getId_service()
        );

        // ✅ Save the application
        try {
            applicationServiceService.add(application);
            showSuccessAlert("Success", "Your application has been submitted!");
        } catch (Exception e) {
            showErrorAlert("Database Error", "Failed to submit application: " + e.getMessage());
        }
    }

    /**
     * Handles search filtering for services.
     */
    private void onSearchTextChanged(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            displayServices(allServices); // Show all services when search is empty
            return;
        }

        List<ServiceOffre> filteredServices = allServices.stream()
                .filter(service -> service.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                        service.getDescription().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());

        displayServices(filteredServices);
    }

    /**
     * Show an error message in an alert box.
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show a success message in an alert box.
     */
    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



}
