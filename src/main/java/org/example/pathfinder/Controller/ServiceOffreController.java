package org.example.pathfinder.Controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.pathfinder.Model.ServiceOffre;
import org.example.pathfinder.Service.ServiceOffreService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ServiceOffreController {

    @FXML
    private TextField titleField, priceField, requiredExperienceField, requiredEducationField, skillsField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private ComboBox<String> fieldField;
    @FXML
    private ComboBox<String> experienceLevelField;
    @FXML
    private Button submitNewButton, cancelButton;
    @FXML
    private DatePicker dateField;

    @FXML
    private DatePicker startDatePicker, endDatePicker;
    @FXML
    private Label durationLabel;

    private LocalDateTime date_posted;
    private final int loggedInUserId = 10; // Simulated logged-in user ID
    private final ServiceOffreService serviceOffreService = new ServiceOffreService();
    private ServiceOffre selectedService = null; // Track the selected service for editing

    private static final String API_KEY = "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"; // Replace with your actual API key
    private static final String API_URL = "https://api-inference.huggingface.co/models/deepseek-ai/DeepSeek-R1-Distill-Qwen-32B"; // Model endpoint

    @FXML
    public void initialize() {
        // Initialize field dropdown values
        fieldField.setItems(FXCollections.observableArrayList(
                "Art", "Computer Science", "Engineering", "Accounting", "Business", "Design", "Health"
        ));
        fieldField.setValue(null);

        // Initialize experience level dropdown values
        experienceLevelField.setItems(FXCollections.observableArrayList(
                "Beginner", "Intermediate", "Expert"
        ));
        experienceLevelField.setValue(null);

        // ✅ Add listener to titleField to trigger AI price suggestion
        titleField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // When focus is lost from titleField
                suggestAIPrice();
            }
        });

        // Calculate duration whenever a date is selected
        startDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> calculateDuration());
        endDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> calculateDuration());
    }

    @FXML
    private void handleSubmit() {
        if (!validateInputs()) return;

        try {
            if (selectedService != null) {
                selectedService.setTitle(titleField.getText());
                selectedService.setDescription(descriptionField.getText());
                selectedService.setField(fieldField.getValue());
                selectedService.setPrice(Double.parseDouble(priceField.getText()));

                selectedService.setRequired_education(requiredEducationField.getText());
                selectedService.setSkills(skillsField.getText());
                selectedService.setExperience_level(experienceLevelField.getValue());
                selectedService.setDuration(durationLabel.getText()); // ✅ Set Duration

                serviceOffreService.update(selectedService);
                showAlert("Success", "Service updated successfully!", Alert.AlertType.INFORMATION);
            } else {
                ServiceOffre newService = new ServiceOffre(
                        loggedInUserId, 0, titleField.getText(), descriptionField.getText(),
                        fieldField.getValue(), Double.parseDouble(priceField.getText()),
                         requiredEducationField.getText(),
                        skillsField.getText(), experienceLevelField.getValue(),
                        durationLabel.getText() , selectedService.getStatus()// ✅ Pass Duration
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
        System.out.println("📌 Date Posted: " + service.getDate_posted().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        titleField.setText(service.getTitle());
        descriptionField.setText(service.getDescription());
        fieldField.setValue(service.getField());
        priceField.setText(String.valueOf(service.getPrice()));

        requiredEducationField.setText(service.getRequired_education());
        skillsField.setText(service.getSkills());
        experienceLevelField.setValue(service.getExperience_level());
        durationLabel.setText(service.getDuration()); // ✅ Load Duration

        selectedService = service;
    }

    private boolean validateInputs() {
        String errorMessage = "";

        if (experienceLevelField.getValue() == null) {
            errorMessage += "❌ Please select an Experience Level!\n";
        }

        if (durationLabel.getText().equals("Duration: --")) {
            errorMessage += "❌ Please select a start and end date to calculate duration!\n";
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
        requiredExperienceField.clear();
        requiredEducationField.clear();
        skillsField.clear();
        experienceLevelField.setValue(null);
        durationLabel.setText("Duration: --"); // ✅ Clear Duration field
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ✅ Method to suggest AI Price
    private void suggestAIPrice() {
        String serviceTitle = titleField.getText();
        String serviceField = fieldField.getValue();
        String experienceLevel = experienceLevelField.getValue();
        String duration = durationLabel.getText().replace("Duration: ", ""); // Extract duration from label

        if (serviceTitle.isEmpty() || serviceField == null || experienceLevel == null || duration.equals("--")) {
            showAlert("Error", "Please enter a service title, select a field, experience level, and duration.", Alert.AlertType.ERROR);
            return;
        }

        double aiEstimatedPrice = generateAIPrice(serviceTitle, serviceField, experienceLevel, duration);
        if (aiEstimatedPrice > 0) {
            priceField.setText(String.valueOf(aiEstimatedPrice)); // ✅ Update priceField with AI-suggested price
        } else {
            showAlert("Error", "Unable to generate a valid price estimation.", Alert.AlertType.ERROR);
        }
    }

    // ✅ Method to generate AI Price using Hugging Face API
    private double generateAIPrice(String serviceTitle, String serviceField, String experienceLevel, String duration) {
        try {
            // ✅ Updated prompt with experience level and duration
            String prompt = "Estimate the average price for a freelance + service titled '" + serviceTitle +
                    "' with an " + experienceLevel + " experience level Return only a number between 50 and 10000.";

            JSONObject requestData = new JSONObject();
            requestData.put("inputs", prompt);

            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestData.toString().getBytes());
                os.flush();
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            JSONArray responseArray = new JSONArray(response.toString());
            String result = responseArray.getJSONObject(0).getString("generated_text").trim();

            // ✅ Extract the last number in the response
            String[] parts = result.split("\\D+"); // Split by non-digit characters
            String lastNumber = "";

            // Iterate through the parts to find the last number
            for (String part : parts) {
                if (!part.isEmpty()) {
                    lastNumber = part;
                }
            }

            // ✅ Validate the extracted number
            double price = Double.parseDouble(lastNumber);
            if (price >= 50 && price <= 10000) { // Ensure the price is within a reasonable range
                return price;
            } else {
                return 0.0; // Invalid price
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to generate AI price: " + e.getMessage(), Alert.AlertType.ERROR);
            return 0.0;
        }
    }

    private void calculateDuration() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate != null && endDate != null) {
            long monthsBetween = ChronoUnit.MONTHS.between(startDate, endDate);

            if (monthsBetween < 1) {
                durationLabel.setText("Duration: Less than 1 month");
            } else if (monthsBetween <= 3) {
                durationLabel.setText("Duration: 1-3 months");
            } else if (monthsBetween <= 6) {
                durationLabel.setText("Duration: 3-6 months");
            } else if (monthsBetween <= 12) {
                durationLabel.setText("Duration: 6-12 months");
            } else {
                durationLabel.setText("Duration: More than a year");
            }
        } else {
            durationLabel.setText("Duration: --"); // Reset if dates are not selected
        }
    }
}