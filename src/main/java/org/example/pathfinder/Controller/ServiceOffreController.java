package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.pathfinder.Model.ServiceOffre;
import java.sql.Date;


public class ServiceOffreController {

    @FXML
    private TextField titleField, fieldField, priceField, requiredExperienceField, requiredEducationField, skillsField,dateField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TableView<ServiceOffre> serviceTable;
    @FXML
    private TableColumn<ServiceOffre, Integer> idServiceColumn, idUserColumn;
    @FXML
    private TableColumn<ServiceOffre, String> titleColumn, descriptionColumn, fieldColumn, experienceColumn, educationColumn, skillsColumn;
    @FXML
    private TableColumn<ServiceOffre, Double> priceColumn;

    // Auto-increment counter for service ID
    private int serviceIdCounter = 1;

    // Simulated logged-in user ID (replace with your session management logic)
    private final int loggedInUserId = 10;

    @FXML
    public void initialize() {
        // Configure table columns to match ServiceOffre properties
        idServiceColumn.setCellValueFactory(new PropertyValueFactory<>("id_service"));
        idUserColumn.setCellValueFactory(new PropertyValueFactory<>("id_user"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        fieldColumn.setCellValueFactory(new PropertyValueFactory<>("field"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        experienceColumn.setCellValueFactory(new PropertyValueFactory<>("required_experience"));
        educationColumn.setCellValueFactory(new PropertyValueFactory<>("required_education"));
        skillsColumn.setCellValueFactory(new PropertyValueFactory<>("skills"));

        // Clear all fields to ensure no pre-filled data
        clearFields();
    }

    @FXML
    private void handleSubmit() {
        try {
            // Validate that required fields are not empty
            if (titleField.getText().isEmpty() || priceField.getText().isEmpty()) {
                showAlert("Validation Error", "Title and Price are required fields.", Alert.AlertType.WARNING);
                return;
            }

            // Parse price input
            double price;
            try {
                price = Double.parseDouble(priceField.getText());
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid numeric value for the price.", Alert.AlertType.ERROR);
                return;
            }

            // Create new ServiceOffre object with user inputs
            ServiceOffre newService = new ServiceOffre(
                    loggedInUserId,                       // Simulated logged-in user ID (int)
                    serviceIdCounter++,                   // Auto-generated service ID (int)
                    titleField.getText(),                 // Title (String)
                    descriptionField.getText(),           // Description (String)
                    Date.valueOf(dateField.getText()),    // Convert String to java.sql.Date
                    fieldField.getText(),                 // Field (String)
                    Double.parseDouble(priceField.getText()), // Price (double) - parse from String
                    requiredExperienceField.getText(),    // Required Experience (String)
                    requiredEducationField.getText(),     // Required Education (String)
                    skillsField.getText()                 // Skills (String)
            );

            // Add the new service to the TableView
            serviceTable.getItems().add(newService);

            // Clear input fields
            clearFields();

            // Show confirmation
            showAlert("Success", "Service added successfully!", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Error", "An unexpected error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Clear all input fields after a service is submitted or canceled.
     */
    private void clearFields() {
        titleField.clear();
        descriptionField.clear();
        fieldField.clear();
        priceField.clear();
        dateField.clear();
        requiredExperienceField.clear();
        requiredEducationField.clear();
        skillsField.clear();
    }

    /**
     * Utility method to display alerts.
     *
     * @param title   The title of the alert.
     * @param message The content of the alert.
     * @param type    The type of the alert.
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
