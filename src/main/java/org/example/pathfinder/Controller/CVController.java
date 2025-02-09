package org.example.pathfinder.Controller;
import org.example.pathfinder.Model.Experience;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class CVController {
    // CV Fields
    @FXML
    private TextField titleField;
    @FXML
    private TextArea introductionField;
    @FXML
    private ChoiceBox<String> languagesDropdown;
    @FXML
    private TextArea cvPreview;

    // Experience Fields
    @FXML
    private VBox experienceContainer;
    @FXML
    private StackPane experienceModalOverlay;
    @FXML
    private VBox experienceModal;
    @FXML
    private ChoiceBox<String> typeDropdown;
    @FXML
    private TextField positionField;
    @FXML
    private TextField locationField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;

    private final List<Experience> experiences = new ArrayList<>();


    @FXML
    public void initialize() {
        // Populate dropdowns
        languagesDropdown.setItems(FXCollections.observableArrayList("English", "French", "Spanish", "German", "Chinese"));
        languagesDropdown.setValue("English");

        typeDropdown.setItems(FXCollections.observableArrayList("Academic", "Internship"));
        typeDropdown.setValue("Academic");

        // Set default start date (today)
        startDatePicker.setValue(LocalDate.now());

        // Restrict Start Date (Minimum 1940-01-01)
        startDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isBefore(LocalDate.of(1940, 1, 1)) || date.isAfter(LocalDate.now()));
            }
        });

        // Restrict End Date (Cannot be earlier than Start Date and not in the future)
        endDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (startDatePicker.getValue() != null) {
                    setDisable(date.isBefore(startDatePicker.getValue()) || date.isAfter(LocalDate.now()));
                } else {
                    setDisable(date.isAfter(LocalDate.now())); // If no start date is selected, only prevent future dates
                }
            }
        });

        // Update End Date whenever Start Date changes
        startDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                endDatePicker.setValue(newDate.plusDays(1)); // Ensure end date is at least one day after start
                endDatePicker.setDayCellFactory(picker -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        setDisable(date.isBefore(newDate) || date.isAfter(LocalDate.now())); // Restrict range
                    }
                });
            }
        });
    }
    @FXML
    private void showExperienceModal() {
        experienceModalOverlay.setVisible(true);
    }
    @FXML
    private void hideExperienceModalCancel() {
        experienceModalOverlay.setVisible(false);
        clearExperienceForm();
    }
    @FXML
    private void hideExperienceModal(MouseEvent event) {
        if (!experienceModal.isVisible()) return;
        experienceModalOverlay.setVisible(false);
        clearExperienceForm();
    }
    @FXML
    private void consumeClick(MouseEvent event) {
        event.consume(); // Stops event from closing modal
    }
    @FXML
    private void addExperience() {
        String type = typeDropdown.getValue();
        String position = positionField.getText();
        String location = locationField.getText();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (position.isEmpty() || location.isEmpty() || startDate == null || endDate == null) {
            showAlert(Alert.AlertType.WARNING, "All fields must be filled out!");
            return;
        }

        if (endDate.isBefore(startDate)) {
            showAlert(Alert.AlertType.WARNING, "End Date must be after Start Date.");
            return;
        }

        Experience experience = new Experience();
        experience.setType(type);
        experience.setPosition(position);
        experience.setLocationName(location);
        experience.setStartDate(startDate.toString());
        experience.setEndDate(endDate.toString());

        experiences.add(experience);

        Label experienceBox = new Label(type + ": " + position + " at " + location + " (" + startDate + " to " + endDate + ")");
        experienceContainer.getChildren().add(experienceBox);

        updateCVPreview();
        experienceModalOverlay.setVisible(false);
        clearExperienceForm();
    }

    @FXML
    private void submitCV() {
        updateCVPreview(); // Refresh preview before submission
        showAlert(Alert.AlertType.INFORMATION, "CV Submitted!");
    }

    private void updateCVPreview() {
        StringBuilder preview = new StringBuilder();
        preview.append("Title: ").append(titleField.getText()).append("\n");
        preview.append("Introduction: ").append(introductionField.getText()).append("\n");
        preview.append("Languages: ").append(languagesDropdown.getValue()).append("\n\n");

        preview.append("Experiences:\n");
        for (Experience exp : experiences) {
            preview.append("- ").append(exp.getType()).append(": ").append(exp.getPosition())
                    .append(" at ").append(exp.getLocationName())
                    .append(" (").append(exp.getStartDate()).append(" to ").append(exp.getEndDate()).append(")\n");
        }

        cvPreview.setText(preview.toString());
    }

    private void clearExperienceForm() {
        positionField.clear();
        locationField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.showAndWait();
    }
}