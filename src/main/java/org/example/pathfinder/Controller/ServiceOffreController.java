package org.example.pathfinder.Controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.pathfinder.Model.ServiceOffre;
import org.example.pathfinder.Service.ServiceOffreService;

import java.sql.Date;
import java.time.LocalDate;

public class ServiceOffreController {

    @FXML
    private TextField titleField, priceField, requiredExperienceField, requiredEducationField, skillsField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private ComboBox<String> fieldField;
    @FXML
    private DatePicker dateField;
    @FXML
    private Button submitNewButton, cancelButton;




    private final int loggedInUserId = 10; // Simulated logged-in user ID
    private final ServiceOffreService serviceOffreService = new ServiceOffreService();
    private ServiceOffre selectedService = null; // Track the selected service for editing

    @FXML
    public void initialize() {

        if (dateField == null) {
            System.err.println("❌ ERROR: dateField is NULL in initialize()!");
        } else {
            System.out.println("✅ dateField is properly initialized!");
        }
        // Initialize dropdown values
        fieldField.setItems(FXCollections.observableArrayList(
                "Art", "Computer Science", "Engineering", "Accounting", "Business", "Design", "Health"
        ));
        fieldField.setValue(null);
        enableDatePicker();

    }

    @FXML
    private void enableDatePicker() {
        dateField.setEditable(false); // Prevent manual typing
        dateField.setShowWeekNumbers(false); // Hide week numbers
    }

    @FXML
    private void handleSubmit() {
        if (!validateInputs()) return;

        try {
            if (selectedService != null) {
                selectedService.setTitle(titleField.getText());
                selectedService.setDescription(descriptionField.getText());
                selectedService.setDate_posted(Date.valueOf(dateField.getValue()));
                selectedService.setField(fieldField.getValue());
                selectedService.setPrice(Double.parseDouble(priceField.getText()));
                selectedService.setRequired_experience(requiredExperienceField.getText());
                selectedService.setRequired_education(requiredEducationField.getText());
                selectedService.setSkills(skillsField.getText());

                serviceOffreService.update(selectedService);
                showAlert("Success", "Service updated successfully!", Alert.AlertType.INFORMATION);
            } else {
                ServiceOffre newService = new ServiceOffre(
                        loggedInUserId, 0, titleField.getText(), descriptionField.getText(),
                        Date.valueOf(dateField.getValue()), fieldField.getValue(),
                        Double.parseDouble(priceField.getText()), requiredExperienceField.getText(),
                        requiredEducationField.getText(), skillsField.getText()
                );

                serviceOffreService.add(newService);
                showAlert("Success", "Service added successfully!", Alert.AlertType.INFORMATION);
            }

            clearFields();
            closeForm();
        } catch (Exception e) {
            showAlert("Error", "Unexpected error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void closeForm() {
        Stage stage = (Stage) submitNewButton.getScene().getWindow();
        stage.close();
    }

    public void loadServiceData(ServiceOffre service) {
        if (service == null) {
            System.err.println("❌ Service is NULL in loadServiceData()");
            return;
        }

        System.out.println("📌 Loading Service: " + service.getTitle());
        System.out.println("📌 Date: " + service.getDate_posted());
        System.out.println("📌 Price: " + service.getPrice());

        titleField.setText(service.getTitle());
        descriptionField.setText(service.getDescription());
        dateField.setValue(service.getDate_posted().toLocalDate());
        fieldField.setValue(service.getField());
        priceField.setText(String.valueOf(service.getPrice()));
        requiredExperienceField.setText(service.getRequired_experience());
        requiredEducationField.setText(service.getRequired_education());
        skillsField.setText(service.getSkills());

        selectedService = service;
    }

    private boolean validateInputs() {
        String errorMessage = "";

        if (dateField.getValue() == null) {
            errorMessage += "❌ Date cannot be empty!\n";
        } else if (!dateField.getValue().equals(LocalDate.now())) {
            errorMessage += "❌ Date must be today's date!\n";
        }

        try {
            double price = Double.parseDouble(priceField.getText());
            if (price < 1.0) {
                errorMessage += "❌ Price must be at least 1.0!\n";
            }
        } catch (NumberFormatException e) {
            errorMessage += "❌ Price must be a numeric value!\n";
        }

        if (!errorMessage.isEmpty()) {
            showAlert("Input Validation Error", errorMessage, Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

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

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
