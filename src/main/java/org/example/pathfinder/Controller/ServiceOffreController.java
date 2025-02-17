package org.example.pathfinder.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.pathfinder.Model.ServiceOffre;
import org.example.pathfinder.Service.ServiceOffreService;

import javafx.geometry.Side;


import java.io.IOException;
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
    private TableColumn<ServiceOffre, Void> actionColumn;
    @FXML
    private Button submitNewButton;

    private final int loggedInUserId = 10; // Simulated logged-in user ID
    private final ServiceOffreService serviceOffreService = new ServiceOffreService();
    private final ObservableList<ServiceOffre> serviceList = FXCollections.observableArrayList();

    private ServiceOffre selectedService = null; // Track the selected service for editing

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

        // Initialize dropdown values
        fieldField.setItems(FXCollections.observableArrayList(
                "Art", "Computer Science", "Engineering", "Accounting", "Business", "Design", "Health"
        ));
        fieldField.setValue(null);

        // Load services and setup actions
        loadServices();
        setupActionColumn();
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button optionsButton = new Button("⋮");
            private final ContextMenu contextMenu = new ContextMenu();

            {
                optionsButton.setStyle("-fx-font-size: 14px; -fx-background-color: #6A8283; -fx-text-fill: white;");

                MenuItem editItem = new MenuItem("Edit");
                editItem.setOnAction(event -> handleEdit(getTableView().getItems().get(getIndex())));

                MenuItem deleteItem = new MenuItem("Delete");
                deleteItem.setOnAction(event -> handleDelete(getTableView().getItems().get(getIndex())));

                contextMenu.getItems().addAll(editItem, deleteItem);
                optionsButton.setOnAction(event -> contextMenu.show(optionsButton, Side.BOTTOM, 0, 0));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(optionsButton);
                }
            }
        });
    }

    @FXML
    private void handleSubmit() {
        if (!validateInputs()) return;

        try {
            // 🔥 If we are editing, update the service instead of adding
            if (selectedService != null) {
                selectedService.setTitle(titleField.getText());
                selectedService.setDescription(descriptionField.getText());
                selectedService.setDate_posted(Date.valueOf(dateField.getValue()));
                selectedService.setField(fieldField.getValue());
                selectedService.setPrice(Double.parseDouble(priceField.getText()));
                selectedService.setRequired_experience(requiredExperienceField.getText());
                selectedService.setRequired_education(requiredEducationField.getText());
                selectedService.setSkills(skillsField.getText());

                // ✅ Call the update method instead of add
                serviceOffreService.update(selectedService);
                showAlert("Success", "Service updated successfully!", Alert.AlertType.INFORMATION);
            } else {
                // ✅ If there's no selected service, it's a new one
                ServiceOffre newService = new ServiceOffre(
                        loggedInUserId,
                        0, // ID is auto-generated in DB
                        titleField.getText(),
                        descriptionField.getText(),
                        Date.valueOf(dateField.getValue()),
                        fieldField.getValue(),
                        Double.parseDouble(priceField.getText()),
                        requiredExperienceField.getText(),
                        requiredEducationField.getText(),
                        skillsField.getText()
                );

                serviceOffreService.add(newService);
                showAlert("Success", "Service added successfully!", Alert.AlertType.INFORMATION);
            }

            loadServices();
            clearFields();
            Stage stage = (Stage) submitNewButton.getScene().getWindow();
            stage.close(); // Close the form after submission
        } catch (Exception e) {
            showAlert("Error", "Unexpected error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleEdit(ServiceOffre service) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/ServiceForum.fxml"));
            Parent root = loader.load();

            ServiceOffreController controller = loader.getController();
            controller.loadServiceData(service);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Service");
            stage.showAndWait();

            loadServices();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedService == null) {
            showAlert("Warning", "No service selected for update.", Alert.AlertType.WARNING);
            return;
        }

        if (!validateInputs()) return;

        selectedService.setTitle(titleField.getText());
        selectedService.setDescription(descriptionField.getText());
        selectedService.setDate_posted(Date.valueOf(dateField.getValue()));
        selectedService.setField(fieldField.getValue());
        selectedService.setPrice(Double.parseDouble(priceField.getText()));
        selectedService.setRequired_experience(requiredExperienceField.getText());
        selectedService.setRequired_education(requiredEducationField.getText());
        selectedService.setSkills(skillsField.getText());

        serviceOffreService.update(selectedService);
        loadServices();
    }



    @FXML
    private void handleDelete(ServiceOffre selected) {
        if (selected == null) {
            showAlert("Warning", "Please select a service to delete.", Alert.AlertType.WARNING);
            return;
        }

        // Ask for confirmation before deleting
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Service: " + selected.getTitle());
        confirmation.setContentText("Are you sure you want to delete this service? This action cannot be undone.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                serviceOffreService.delete(selected.getId_service());
                loadServices(); // Refresh the service list
                showAlert("Success", "Service deleted successfully!", Alert.AlertType.INFORMATION);
            }
        });
    }



    public void loadServiceData(ServiceOffre service) {
        if (service == null) return;

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


    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void loadServices() {
        List<ServiceOffre> services = serviceOffreService.getAllSortedByPrice(); // ✅ Fetch sorted services
        serviceList.setAll(services);
        serviceTable.setItems(serviceList);
    }
    @FXML
    private void sortServicesByPrice() {
        List<ServiceOffre> sortedServices = serviceOffreService.getAllSortedByPrice();
        serviceList.setAll(sortedServices);
        serviceTable.setItems(serviceList);
    }


}
