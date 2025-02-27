package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.pathfinder.Model.JobOffer;
import org.example.pathfinder.Service.JobOfferService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class JobOfferUpdateController {

    private JobOfferService jobOfferService;
    private JobOffer currentJobOffer; // Holds the current job offer for editing

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField numberOfSpotsField;

    @FXML
    private ComboBox<String> requiredEducationField;

    @FXML
    private TextField requiredExperienceField;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField skillsField;
    @FXML
    private Label titleErrorLabel, numberOfSpotsErrorLabel,descriptionErrorLabel, requiredExperienceErrorLabel, skillsErrorLabel, jobFieldErrorLabel, countryErrorLabel, cityErrorLabel, requiredEducationErrorLabel, typeErrorLabel;

    @FXML
    private ComboBox<String> jobOffersComboBox,countryComboBox, cityComboBox;

    @FXML
    private Label citylabel;
    @FXML
    private Button submitButton;

    private static final String API_URL = "https://api.adzuna.com/v1/api/jobs/us/categories?app_id=0d0c3bdf&app_key=c3508cb0e5131989785afef1cfcbafc6";
    private static final String GEONAMES_USERNAME = "nourmontasser";  // Replace with your GeoNames username
    private final Map<String, String> countryMap = new HashMap<>(); // Stores CountryName -> ISO Code

    public JobOfferUpdateController() {
        jobOfferService = new JobOfferService(); // Initialize the service
    }

    @FXML
    private void initialize() {
        typeComboBox.getItems().addAll("Full-time", "Part-time", "Contract");
        loadJobFields(); // Populate job fields on initialization
        loadCountries();
        hideErrorLabels();

        // Show city options when a country is selected
        countryComboBox.setOnAction(event -> loadCities());
    }
    private void hideErrorLabels() {
        titleErrorLabel.setVisible(false);
        numberOfSpotsErrorLabel.setVisible(false);
        requiredExperienceErrorLabel.setVisible(false);
        skillsErrorLabel.setVisible(false);
        jobFieldErrorLabel.setVisible(false);
        countryErrorLabel.setVisible(false);
        cityErrorLabel.setVisible(false);
        requiredEducationErrorLabel.setVisible(false);
        typeErrorLabel.setVisible(false);
        descriptionErrorLabel.setVisible(false);
    }
    private void loadJobFields() {
        try {
            List<String> jobFields = fetchJobFieldsFromAPI();
            if (!jobFields.isEmpty()) {
                jobOffersComboBox.getItems().addAll(jobFields);
            } else {
                showAlert(Alert.AlertType.WARNING, "No Data", "No job fields available.");
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to fetch job fields: " + e.getMessage());
        }
    }

    private List<String> fetchJobFieldsFromAPI() throws IOException {
        List<String> jobFields = new ArrayList<>();
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("Failed to fetch job fields, response code: " + responseCode);
            }

            InputStream inputStream = conn.getInputStream();
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            String response = scanner.hasNext() ? scanner.next() : "";
            scanner.close();

            jobFields = parseJobFields(response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Failed to fetch or parse job fields: " + e.getMessage());
        }
        return jobFields;
    }

    private List<String> parseJobFields(String jsonResponse) {
        List<String> jobFields = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray categories = jsonObject.getJSONArray("results");
            for (int i = 0; i < categories.length(); i++) {
                JSONObject category = categories.getJSONObject(i);
                String field = category.getString("label");
                jobFields.add(field);  // Add the job field (label) to the list
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobFields;
    }

    @FXML
    private void handleSubmitButtonClick() {
        try {
            if (!isValidForm()) {
                return; // Stop processing if validation fails
            }

            String title = titleField.getText();
            String description = descriptionField.getText();
            int numberOfSpots = Integer.parseInt(numberOfSpotsField.getText());
            String requiredEducation = requiredEducationField.getValue();
            String requiredExperience = requiredExperienceField.getText();
            String type = typeComboBox.getValue();
            String skills = skillsField.getText(); // Assuming skills are comma-separated
            String field = jobOffersComboBox.getValue();

            // Concatenating country and city into address
            String country = countryComboBox.getValue().trim();
            String city = cityComboBox.getValue().trim();
            String address = country + ", " + city;

            if (!currentJobOffer.getTitle().equals(title) && !jobOfferService.isJobOfferTitleUnique(title)) {
                showError(titleErrorLabel, "A job offer with this title already exists.");
                return;
            }

            currentJobOffer.setTitle(title);
            currentJobOffer.setDescription(description);
            currentJobOffer.setNumberOfSpots(numberOfSpots);
            currentJobOffer.setRequiredEducation(requiredEducation);
            currentJobOffer.setRequiredExperience(requiredExperience);
            currentJobOffer.setType(type);
            currentJobOffer.setSkills(skills);
            currentJobOffer.setField(field);
            currentJobOffer.setAddress(address);

            jobOfferService.update(currentJobOffer);

            showAlert(Alert.AlertType.INFORMATION, "Job Offer Updated", "The job offer was successfully updated.");
            Stage stage = (Stage) titleField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating the job offer: " + e.getMessage());
        }
    }

    private boolean isValidForm() {
        hideErrorLabels();  // Hide all error labels initially

        boolean isValid = true;

        if (titleField.getText().trim().isEmpty()) {
            showError(titleErrorLabel, "Title is required.");
            isValid = false;
        }
        if (descriptionField.getText().trim().isEmpty()) {
            showError(descriptionErrorLabel, "Description is required.");
            isValid = false;
        }

        if (numberOfSpotsField.getText().trim().isEmpty()) {
            showError(numberOfSpotsErrorLabel, "Number of spots is required.");
            isValid = false;
        }

        try {
            Integer.parseInt(numberOfSpotsField.getText().trim());
        } catch (NumberFormatException e) {
            showError(numberOfSpotsErrorLabel, "Please enter a valid number for 'Number of Spots'.");
            isValid = false;
        }

        if (requiredExperienceField.getText().trim().isEmpty()) {
            showError(requiredExperienceErrorLabel, "Required experience is required.");
            isValid = false;
        }

        if (skillsField.getText().trim().isEmpty()) {
            showError(skillsErrorLabel, "Skills are required.");
            isValid = false;
        }

        if (jobOffersComboBox.getValue() == null) {
            showError(jobFieldErrorLabel, "Job field is required.");
            isValid = false;
        }

        if (countryComboBox.getValue() == null || countryComboBox.getValue().trim().isEmpty()) {
            showError(countryErrorLabel, "Country is required.");
            isValid = false;
        }

        if (cityComboBox.getValue() == null || cityComboBox.getValue().isEmpty()) {
            showError(cityErrorLabel, "City is required.");
            isValid = false;
        }

        if (requiredEducationField.getValue() == null) {
            showError(requiredEducationErrorLabel, "Required education is required.");
            isValid = false;
        }

        if (typeComboBox.getValue() == null) {
            showError(typeErrorLabel, "Job type is required.");
            isValid = false;
        }

        return isValid;
    }

    private void showError(Label errorLabel, String errorMessage) {
        errorLabel.setText(errorMessage);
        errorLabel.setVisible(true);
    }

        private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm() {
        titleField.clear();
        descriptionField.clear();
        numberOfSpotsField.clear();
        requiredEducationField.setValue(null);
        requiredExperienceField.clear();
        skillsField.clear();
        typeComboBox.setValue(null);
    }

    @FXML
    private void handleCancelButtonClick() {
        Stage stage = (Stage) descriptionField.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    public void setJobOffer(JobOffer jobOffer) {
        this.currentJobOffer = jobOffer;

        titleField.setText(jobOffer.getTitle());
        descriptionField.setText(jobOffer.getDescription());
        numberOfSpotsField.setText(String.valueOf(jobOffer.getNumberOfSpots()));
        requiredEducationField.setValue(jobOffer.getRequiredEducation());
        requiredExperienceField.setText(jobOffer.getRequiredExperience());
        skillsField.setText(jobOffer.getSkills());
        typeComboBox.setValue(jobOffer.getType());
        jobOffersComboBox.setValue(jobOffer.getField());

        String address = jobOffer.getAddress();
        String[] addressParts = address.split(",", 2);
        if (addressParts.length == 2) {
            String country = addressParts[0].trim(); // Trim any spaces
            String city = addressParts[1].trim();

            // Set the country and city in the combo boxes
            countryComboBox.setValue(country);
            cityComboBox.setValue(city);
        }

    }
    private void loadCountries() {
        try {
            List<String> countries = fetchCountriesFromGeoNames();
            if (!countries.isEmpty()) {
                countryComboBox.getItems().addAll(countries);
            } else {
                showAlert(Alert.AlertType.WARNING, "No Data", "No countries available.");
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to fetch countries.");
            e.printStackTrace();
        }
    }

    private List<String> fetchCountriesFromGeoNames() throws IOException {
        List<String> countries = new ArrayList<>();
        String urlString = "http://api.geonames.org/countryInfoJSON?username=" + GEONAMES_USERNAME;
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setInstanceFollowRedirects(true);
        conn.setRequestProperty("Accept", "application/json");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Failed to fetch countries, response code: " + responseCode);
        }

        InputStream inputStream = conn.getInputStream();
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        String response = scanner.hasNext() ? scanner.next() : "";
        scanner.close();

        countries = parseCountries(response);
        return countries;
    }

    private List<String> parseCountries(String jsonResponse) {
        List<String> countryList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray countryArray = jsonObject.getJSONArray("geonames");
            for (int i = 0; i < countryArray.length(); i++) {
                JSONObject country = countryArray.getJSONObject(i);
                String countryName = country.getString("countryName"); // Full country name
                String countryCode = country.getString("countryCode"); // 2-letter ISO code

                countryMap.put(countryName, countryCode); // Store in map
                countryList.add(countryName); // Add to dropdown
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return countryList;
    }

    @FXML
    private void loadCities() {
        String selectedCountry = countryComboBox.getValue();
        if (selectedCountry != null) {
            try {
                String countryCode = countryMap.get(selectedCountry);
                List<String> cities = fetchCitiesFromGeoNames(countryCode);
                if (!cities.isEmpty()) {
                    cityComboBox.getItems().clear();
                    cityComboBox.getItems().addAll(cities);

                    citylabel.setVisible(true);
                    cityComboBox.setVisible(true);
                } else {
                    showAlert(Alert.AlertType.WARNING, "No Data", "No cities available for the selected country.");

                }
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to fetch cities.");
                e.printStackTrace();
            }
        }
    }

    private List<String> fetchCitiesFromGeoNames(String country) throws IOException {
        List<String> cities = new ArrayList<>();
        String urlString = "http://api.geonames.org/searchJSON?country=" + country + "&username=" + GEONAMES_USERNAME;
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setInstanceFollowRedirects(true);
        conn.setRequestProperty("Accept", "application/json");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Failed to fetch cities, response code: " + responseCode);
        }

        InputStream inputStream = conn.getInputStream();
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        String response = scanner.hasNext() ? scanner.next() : "";
        scanner.close();

        cities = parseCities(response);
        return cities;
    }

    private List<String> parseCities(String jsonResponse) {
        List<String> cities = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray cityArray = jsonObject.getJSONArray("geonames");
            for (int i = 0; i < cityArray.length(); i++) {
                JSONObject city = cityArray.getJSONObject(i);
                String cityName = city.getString("name");
                cities.add(cityName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cities;
    }
}
