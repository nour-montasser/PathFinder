package org.example.pathfinder.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.pathfinder.Model.ServiceOffre;
import org.example.pathfinder.Service.ServiceOffreService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

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
    @FXML
    private Button submitNewButton;

    private final int loggedInUserId = 10; // Simulated logged-in user ID
    private final ServiceOffreService serviceOffreService = new ServiceOffreService();
    private final ObservableList<ServiceOffre> serviceList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configure table columns
        idServiceColumn.setCellValueFactory(new PropertyValueFactory<>("id_service"));
        idUserColumn.setCellValueFactory(new PropertyValueFactory<>("id_user"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        fieldColumn.setCellValueFactory(new PropertyValueFactory<>("field"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        experienceColumn.setCellValueFactory(new PropertyValueFactory<>("required_experience"));
        educationColumn.setCellValueFactory(new PropertyValueFactory<>("required_education"));
        skillsColumn.setCellValueFactory(new PropertyValueFactory<>("skills"));

        // Initialize field dropdown options
        fieldField.setItems(FXCollections.observableArrayList(
                "Art", "Computer Science", "Engineering", "Accounting", "Business", "Design", "Health"
        ));
        fieldField.setValue(null);

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
        if (!validateInputs()) return;

        try {
            // Create a new ServiceOffre object
            ServiceOffre newService = new ServiceOffre(
                    loggedInUserId,                          // Simulated user ID
                    0,                                      // Auto-incremented in DB
                    titleField.getText(),
                    descriptionField.getText(),
                    Date.valueOf(dateField.getValue()),     // Convert LocalDate to SQL Date
                    fieldField.getValue(),
                    Double.parseDouble(priceField.getText()),
                    requiredExperienceField.getText(),
                    requiredEducationField.getText(),
                    skillsField.getText()
            );

            // Save the new service offer
            serviceOffreService.add(newService);
            loadServices();
            clearFields();

            // Close modal if applicable
            Stage stage = (Stage) submitNewButton.getScene().getWindow();
            stage.close();

            showAlert("Success", "Service added successfully!", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Error", "Unexpected error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUpdate() {
        ServiceOffre selected = serviceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a service to update.", Alert.AlertType.WARNING);
            return;
        }

        if (!validateInputs()) return;

        try {
            selected.setTitle(titleField.getText());
            selected.setDescription(descriptionField.getText());
            selected.setDate_posted(Date.valueOf(dateField.getValue()));
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
            showAlert("Warning", "Please select a service to delete.", Alert.AlertType.ERROR);
            return;
        }

        serviceOffreService.delete(selected.getId_service());
        loadServices();
    }

    /**
     * **🚀 Input Validation Method**
     */
    private boolean validateInputs() {
        String errorMessage = "";

        // 🚀 Validate Date (Must be today)
        if (dateField.getValue() == null) {
            errorMessage += "❌ Date cannot be empty!\n";
        } else if (!dateField.getValue().equals(LocalDate.now())) {
            errorMessage += "❌ Date must be today's date!\n";
        }

        // 🚀 Validate Price (Must be a positive number)
        try {
            double price = Double.parseDouble(priceField.getText());
            if (price < 1.0) {
                errorMessage += "❌ Price must be at least 1.0!\n";
            }
        } catch (NumberFormatException e) {
            errorMessage += "❌ Price must be a numeric value!\n";
        }

        // 🚀 Validate Description (Min 10, Max 500 characters)
        String description = descriptionField.getText().trim();
        if (description.length() < 10 || description.length() > 500) {
            errorMessage += "❌ Description must be between 10 and 500 characters!\n";
        }

        // 🚀 Validate Skills (Only letters & commas, no numbers)
        String skills = skillsField.getText().trim();
        if (!skills.matches("[a-zA-Z, ]+")) {
            errorMessage += "❌ Skills must contain only letters and commas!\n";
        }

        // ❌ Show error messages if there are any issues
        if (!errorMessage.isEmpty()) {
            showAlert("Input Validation Error", errorMessage, Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

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

    /**
     * **🛑 Utility Method for Alerts**
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
