package org.example.pathfinder.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import java.util.List;
import org.example.pathfinder.Model.ServiceOffre;
import org.example.pathfinder.Service.ServiceOffreService;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.pathfinder.Model.ServiceOffre;
import java.sql.Date;



public class ServiceOffreController {

    @FXML
    private DatePicker dateField;

    @FXML
    private ComboBox<String> fieldField;
    @FXML
    private TextField titleField, priceField, requiredExperienceField, requiredEducationField, skillsField;
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
    private final ServiceOffreService serviceOffreService = new ServiceOffreService();
    private ObservableList<ServiceOffre> serviceList = FXCollections.observableArrayList();
    @FXML
    public void initialize() {
        // Configure table columns to match ServiceOffre properties
        idServiceColumn.setCellValueFactory(new PropertyValueFactory<>("id_service"));
        idUserColumn.setCellValueFactory(new PropertyValueFactory<>("id_user"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        // Create a list of options for the dropdown
        ObservableList<String> fieldOptions = FXCollections.observableArrayList(
                "Art", "Computer Science", "Engineering", "Accounting", "Business", "Design", "Health"
        );


        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        experienceColumn.setCellValueFactory(new PropertyValueFactory<>("required_experience"));
        educationColumn.setCellValueFactory(new PropertyValueFactory<>("required_education"));
        skillsColumn.setCellValueFactory(new PropertyValueFactory<>("skills"));
        // Set the items in the ComboBox
        fieldField.setItems(fieldOptions);
        fieldField.setValue(null);
        // Clear all fields to ensure no pre-filled data
        clearFields();
        loadServices();
    }
    @FXML
    private void loadServices() {
        List<ServiceOffre> services = serviceOffreService.getAll();
        serviceList.setAll(services);
        serviceTable.setItems(serviceList);
    }

    @FXML
    private void handleSubmit() {
        try {
            // Validate that required fields are not empty
            if (titleField.getText().isEmpty() || priceField.getText().isEmpty()|| fieldField.getValue() == null) {
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
            String selectedField = fieldField.getValue();
            // Create new ServiceOffre object with user inputs
            ServiceOffre newService = new ServiceOffre(
                    loggedInUserId,                       // Simulated logged-in user ID (int)
                    serviceIdCounter++,                   // Auto-generated service ID (int)
                    titleField.getText(),                 // Title (String)
                    descriptionField.getText(),           // Description (String)
                    java.sql.Date.valueOf(dateField.getValue()),     // Convert String to java.sql.Date
                    selectedField,                 // Field (String)
                    Double.parseDouble(priceField.getText()), // Price (double) - parse from String
                    requiredExperienceField.getText(),    // Required Experience (String)
                    requiredEducationField.getText(),     // Required Education (String)
                    skillsField.getText()                 // Skills (String)
            );

          serviceOffreService.add(newService);
          loadServices();


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
    @FXML
    private void clearFields() {
        titleField.clear();
        descriptionField.clear();
        fieldField.setValue(null);
        priceField.clear();
        dateField.setValue(null);
        requiredExperienceField.clear();
        requiredEducationField.clear();
        skillsField.clear();
    }
    @FXML
    private void handleUpdate() {
        ServiceOffre selected = serviceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a service to update.", Alert.AlertType.WARNING);
            return;
        }

        try {
            selected.setTitle(titleField.getText());
            selected.setDescription(descriptionField.getText());
            // Corrected DatePicker handling
            if (dateField.getValue() != null) {
                selected.setDate_posted(java.sql.Date.valueOf(dateField.getValue()));
            }
            selected.setField(fieldField.getValue());

            selected.setPrice(Double.parseDouble(priceField.getText()));
            selected.setRequired_experience(requiredExperienceField.getText());
            selected.setRequired_education(requiredEducationField.getText());
            selected.setSkills(skillsField.getText());

            serviceOffreService.update(selected);
            loadServices();
        } catch (Exception e) {
            showAlert("Error", "Invalid input. Please check your fields.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDelete() {
        ServiceOffre selected = serviceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a service to delete.",Alert.AlertType.ERROR);
            return;
        }

        serviceOffreService.delete(selected.getId_service());
        loadServices();
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
