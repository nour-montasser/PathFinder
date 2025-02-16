package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.pathfinder.Model.ServiceOffre;
import org.example.pathfinder.Service.ServiceOffreService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationServiceController {

    @FXML
    private GridPane servicesGrid; // Grid for displaying services

    @FXML
    private Button addServiceButton; // Floating "Add Service" button

    @FXML
    private TextField searchField; // Search bar

    private final ServiceOffreService serviceOffreService = new ServiceOffreService();
    private List<ServiceOffre> allServices; // Store all services initially

    @FXML
    public void initialize() {
        loadServices(); // Load services at startup
        searchField.textProperty().addListener((obs, oldVal, newVal) -> onSearchTextChanged(newVal));
    }

    private void loadServices() {
        allServices = serviceOffreService.getAll();
        displayServices(allServices);
    }

    private void displayServices(List<ServiceOffre> services) {
        servicesGrid.getChildren().clear();
        int column = 0, row = 0;

        for (ServiceOffre service : services) {
            StackPane serviceCard = createServiceCard(service);
            servicesGrid.add(serviceCard, column++, row);

            if (column == 3) {
                column = 0;
                row++;
            }
        }
    }

    private StackPane createServiceCard(ServiceOffre service) {
        StackPane card = new StackPane();
        card.setStyle("-fx-background-color: #6A8283; " + // Slate Gray
                "-fx-border-radius: 10px; " +
                "-fx-padding: 15px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 10, 0, 0, 5); " +
                "-fx-min-width: 250px; " +
                "-fx-min-height: 200px;");

        VBox content = new VBox(10);
        content.getChildren().addAll(
                createLabel("Service: " + service.getTitle(), 18, "white", true), // White text
                createLabel("Price: $" + service.getPrice(), 16, "#98BFD1", true), // Sky Blue
                createLabel("Description: " + service.getDescription(), 14, "white", false),
                createLabel("Posted on: " + service.getDate_posted(), 14, "white", false),
                createDetailsButton(service),
                createMenuButton(service) // ✅ Restored Edit/Delete menu
        );

        card.getChildren().add(content);
        return card;
    }

    private Label createLabel(String text, int fontSize, String color, boolean bold) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + color + "; " +
                "-fx-font-size: " + fontSize + "px; " +
                (bold ? "-fx-font-weight: bold;" : ""));
        return label;
    }

    private Button createDetailsButton(ServiceOffre service) {
        Button detailsButton = new Button("Show Details");
        detailsButton.setStyle("-fx-background-color: #98BFD1; " + // Sky Blue
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-border-radius: 8px; " +
                "-fx-padding: 8px 16px;");
        detailsButton.setOnAction(event -> openApplicationDetails(service.getId_service()));
        return detailsButton;
    }

    private MenuButton createMenuButton(ServiceOffre service) {
        MenuButton menuButton = new MenuButton("⋮");
        MenuItem editItem = new MenuItem("Edit");
        MenuItem deleteItem = new MenuItem("Delete");

        menuButton.getItems().addAll(editItem, deleteItem);
        editItem.setOnAction(event -> handleEditService(service));
        deleteItem.setOnAction(event -> handleDeleteService(service));

        return menuButton;
    }

    private void openApplicationDetails(int serviceId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/showApplicationDetails.fxml"));
            Parent root = loader.load();

            ShowApplicationDetailsController controller = loader.getController();
            controller.setServiceId(serviceId);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Application Details");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openServiceForumModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/ServiceForum.fxml"));
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.setScene(new Scene(root));
            modalStage.setTitle("Add a New Service");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(addServiceButton.getScene().getWindow());
            modalStage.setResizable(false);
            modalStage.showAndWait();

            loadServices();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditService(ServiceOffre service) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/ServiceForum.fxml"));
            Parent root = loader.load();

            ServiceOffreController controller = loader.getController();
            controller.loadServiceData(service);

            Stage modalStage = new Stage();
            modalStage.setScene(new Scene(root));
            modalStage.setTitle("Edit Service");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.showAndWait();

            loadServices();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteService(ServiceOffre service) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Service: " + service.getTitle());
        alert.setContentText("Are you sure you want to delete this service?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                serviceOffreService.delete(service.getId_service());
                loadServices();
            }
        });
    }

    private void onSearchTextChanged(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            displayServices(allServices);
            return;
        }

        List<ServiceOffre> filteredServices = allServices.stream()
                .filter(service -> service.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                        service.getDescription().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());

        displayServices(filteredServices);
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
