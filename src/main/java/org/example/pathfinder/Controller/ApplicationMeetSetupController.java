package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ApplicationMeetSetupController {

    @FXML
    private RadioButton startNowRadio;

    @FXML
    private RadioButton scheduleLaterRadio;

    @FXML
    private HBox scheduleLaterBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField timeField;

    private boolean submitClicked = false;
    private LocalDate selectedDate;
    private LocalTime selectedTime;

    @FXML
    public void initialize() {
        // Show/hide the date and time picker based on the selected radio button
        startNowRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            scheduleLaterBox.setVisible(!newValue);
        });

        scheduleLaterRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            scheduleLaterBox.setVisible(newValue);
        });
    }

    @FXML
    private void handleSubmit() {
        if (scheduleLaterRadio.isSelected()) {
            // Validate date and time
            selectedDate = datePicker.getValue();
            String timeText = timeField.getText();

            if (selectedDate == null || timeText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please select a date and time.");
                return;
            }

            try {
                selectedTime = LocalTime.parse(timeText, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Time", "Please enter time in HH:mm format.");
                return;
            }
        }

        submitClicked = true;

        // If "Start Meeting Now" is selected, open the Google Meet link in a new window

        closeDialog();
    }

    @FXML
    private void handleCancel() {
        submitClicked = false;
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) scheduleLaterBox.getScene().getWindow();
        stage.close();
    }

    public boolean isSubmitClicked() {
        return submitClicked;
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public LocalTime getSelectedTime() {
        return selectedTime;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    void openMeetInDefaultBrowser(String url) {
        try {
            // Check if the Desktop API is supported
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                // Open the URL in the default browser
                Desktop.getDesktop().browse(new URI(url));
            } else {
                // Fallback: Show an error message if the Desktop API is not supported
                showAlert(Alert.AlertType.ERROR, "Error", "Unable to open the default browser.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to open the Google Meet link.");
        }
    }}