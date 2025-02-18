package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
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

    @FXML
    private ComboBox<String> sortDropdown; // Sorting dropdown menu

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

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> onSearchTextChanged(newVal));
        }

        setupSortingDropdown(); // Initialize sorting dropdown
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
        int column = 0, row = 0;

        for (ServiceOffre service : services) {
            StackPane serviceCard = createServiceCard(service);
            servicesGrid.add(serviceCard, column++, row);

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
                "-fx-min-width: 300px; " +
                "-fx-max-width: 300px; " +
                "-fx-min-height: 220px; " +
                "-fx-max-height: 220px;");

        VBox content = new VBox(10);
        content.getChildren().addAll(
                createLabel("Service: " + service.getTitle(), 18, "#512E1B", true),
                createLabel("Price: $" + service.getPrice(), 16, "#98BFD1", true),
                createLabel("Posted on: " + service.getDate_posted(), 14, "black", false),
                createApplyButton(service)
        );

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
     * Creates an "Apply" button for clients.
     */
    private Button createApplyButton(ServiceOffre service) {
        Button applyButton = new Button("Apply");
        applyButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px;");
        applyButton.setOnAction(event -> openApplicationModal(service));
        return applyButton;
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

        if (offeredPrice < service.getPrice()) {
            showErrorAlert("Error", "Your offered price must be at least $" + service.getPrice());
            return;
        }

        ApplicationService application = new ApplicationService(
                0, offeredPrice, 0, "Pending", service.getId_service()
        );

        try {
            applicationServiceService.add(application);
            showSuccessAlert("Success", "Your application has been submitted!");
        } catch (Exception e) {
            showErrorAlert("Database Error", "Failed to submit application: " + e.getMessage());
        }
    }

    private void setupSortingDropdown() {
        if (sortDropdown == null) {
            System.out.println("⚠ sortDropdown is NULL! Check your FXML file.");
            return;
        }
        sortDropdown.getItems().addAll("Price: Low to High", "Price: High to Low", "Newest First", "Oldest First");
        sortDropdown.setValue("Price: Low to High");
        sortDropdown.setOnAction(event -> sortServices());
    }

    private void sortServices() {
        String selectedSort = sortDropdown.getValue();

        switch (selectedSort) {
            case "Price: Low to High":
                allServices = serviceOffreService.getAllSortedByPrice();
                break;
            case "Price: High to Low":
                allServices = serviceOffreService.getAllSortedByPriceDesc();
                break;
            case "Newest First":
                allServices = serviceOffreService.getAllSortedByDateDesc();
                break;
            case "Oldest First":
                allServices = serviceOffreService.getAllSortedByDate();
                break;
        }

        displayServices(allServices);
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

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
}
