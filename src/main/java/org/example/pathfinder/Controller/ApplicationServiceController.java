package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;
import java.io.IOException;
import java.util.List;
import org.example.pathfinder.Model.ServiceOffre;
import org.example.pathfinder.Service.ServiceOffreService;

public class ApplicationServiceController {

    @FXML
    private GridPane servicesGrid; // Grid for displaying services

    @FXML
    private Button addServiceButton; // Floating "Add Service" button

    private final ServiceOffreService serviceOffreService = new ServiceOffreService();

    @FXML
    public void initialize() {
        loadServices();
    }

    private void loadServices() {
        servicesGrid.getChildren().clear();
        List<ServiceOffre> services = serviceOffreService.getAll();

        int column = 0;
        int row = 0;

        for (ServiceOffre service : services) {
            StackPane serviceCard = createServiceCard(service);
            servicesGrid.add(serviceCard, column++, row);

            if (column == 3) { // 3 cards per row
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
                createLabel("Service: " + service.getTitle(), 18, "#512E1B", true), // Seal Brown
                createLabel("Price: $" + service.getPrice(), 16, "#98BFD1", true), // Sky Blue
                createLabel("Description: " + service.getDescription(), 14, "white", false),
                createLabel("Posted on: " + service.getDate_posted(), 14, "white", false),
                createLabel("From: $" + service.getPrice(), 14, "#98BFD1", true), // Price Offered
                createDetailsButton(service)
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
        Button detailsButton = new Button("View Details");
        detailsButton.setStyle("-fx-background-color: #98BFD1; " + // Sky Blue
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-border-radius: 8px; " +
                "-fx-padding: 8px 16px;");
        detailsButton.setOnAction(event -> openApplicationDetails(service.getId_service()));
        return detailsButton;
    }

    private void openApplicationDetails(int serviceId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/showApplicationDetails.fxml"));
            Parent root = loader.load();

            ShowApplicationDetailsController controller = loader.getController();
            controller.setServiceId(serviceId);

            Stage stage = (Stage) servicesGrid.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Application Details");
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
            modalStage.initOwner(addServiceButton.getScene().getWindow());
            modalStage.setResizable(false);
            modalStage.showAndWait();

            loadServices();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
