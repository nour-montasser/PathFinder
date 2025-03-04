package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.pathfinder.Model.JobOffer;
import org.example.pathfinder.Model.LoggedUser;
import org.example.pathfinder.Service.JobOfferService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class JobOfferFormController {

    private JobOfferService jobOfferService;
    private final long loggedInUserId = LoggedUser.getInstance().getUserId();

    @FXML
    private TextField titleField, numberOfSpotsField, requiredExperienceField, skillsField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private ComboBox<String> typeComboBox, jobOffersComboBox, countryComboBox, cityComboBox, requiredEducationField;
    @FXML
    private Button saveButton, cancelButton;
    @FXML
    private Label citylabel;

    @FXML
    private Label titleErrorLabel,descriptionErrorLabel, numberOfSpotsErrorLabel, requiredExperienceErrorLabel, skillsErrorLabel, jobFieldErrorLabel, countryErrorLabel, cityErrorLabel, requiredEducationErrorLabel, typeErrorLabel;

    private static final String API_URL = "https://api.adzuna.com/v1/api/jobs/gb/categories?app_id=0d0c3bdf&app_key=c3508cb0e5131989785afef1cfcbafc6";
    private static final String GEONAMES_USERNAME = "nourmontasser";  // Replace with your GeoNames username
    private final Map<String, String> countryMap = new HashMap<>(); // Stores CountryName -> ISO Code


    public JobOfferFormController() {
        this.jobOfferService = new JobOfferService();
    }

    public void setService(JobOfferService jobOfferService) {
        this.jobOfferService = jobOfferService;
    }

    @FXML
    private void initialize() {
        typeComboBox.getItems().addAll("Full-time", "Part-time", "Fixed-term contract", "Long-term contract");
        loadJobFields();
        loadCountries();
        citylabel.setVisible(false);
        cityComboBox.setVisible(false);

        // Hide error labels by default
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

    private void showError(Label errorLabel, String errorMessage) {
        errorLabel.setText(errorMessage);
        errorLabel.setVisible(true);
    }

    // This method fetches job fields from the API
    private void loadJobFields() {
        try {
            List<String> jobFields = fetchJobFieldsFromAPI();  // Fetch the job fields
            if (!jobFields.isEmpty()) {
                jobOffersComboBox.getItems().addAll(jobFields);  // Add to ComboBox
            } else {
                showAlert(Alert.AlertType.WARNING, "No Data", "No job fields available.");
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to fetch job fields.");
            e.printStackTrace();
        }
    }

    // This method makes the API call and returns job fields
    private List<String> fetchJobFieldsFromAPI() throws IOException {
        List<String> jobFields = new ArrayList<>();
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode != 200) {
                throw new IOException("Failed to fetch job fields, response code: " + responseCode);
            }

            // Reading the response with InputStream and Scanner
            InputStream inputStream = conn.getInputStream();
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            String response = scanner.hasNext() ? scanner.next() : "";
            scanner.close();

            // Parse JSON and extract job fields
            jobFields = parseJobFields(response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Failed to fetch or parse job fields.");
        }
        return jobFields;
    }

    // This method parses the job fields from the JSON response
    private List<String> parseJobFields(String jsonResponse) {
        List<String> jobFields = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray categories = jsonObject.getJSONArray("results");  // array
            if (categories.length() == 0) {
                throw new IOException("No categories available in the API response.");
            }
            for (int i = 0; i < categories.length(); i++) {
                JSONObject category = categories.getJSONObject(i);
                String field = category.getString("label");
                jobFields.add(field);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobFields;
    }



    @FXML
    private void handleSubmitButtonClick() {
        if (!isValidForm()) return;

        try {
            String title = titleField.getText().trim();
            String description = descriptionField.getText().trim();
            int numberOfSpots = Integer.parseInt(numberOfSpotsField.getText().trim());
            String requiredEducation = requiredEducationField.getValue(); // Use getValue() for ComboBox
            String requiredExperience = requiredExperienceField.getText().trim();
            String type = typeComboBox.getValue();
            String skills = skillsField.getText().trim();
            String jobField = jobOffersComboBox.getValue();

            // Concatenating country and city into address
            String country = countryComboBox.getValue().trim();
            String city = cityComboBox.getValue().trim();
            String address = country + ", " + city;

            if (jobOfferService.isJobOfferTitleUnique(title)) {
                JobOffer jobOffer = new JobOffer(loggedInUserId, title, description, type, numberOfSpots,
                        requiredEducation, requiredExperience, skills, jobField, address);

                jobOfferService.add(jobOffer);

                // Show alert asking if the user wants to create a test
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Create Test?");
                alert.setHeaderText(null);
                alert.setContentText("Do you want to create a test for this job offer?");

                Optional<ButtonType> result = alert.showAndWait();
                    long id = jobOfferService.getIdByTitle(titleField.getText().trim());
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // Close the current form
                    closeForm();

                    // Open the question.fxml and pass the jobOfferId to the controller
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Frontoffice/Question.fxml"));
                    Parent root = loader.load();
                    QuestionController controller = loader.getController();
                    controller.setJobOfferId(id); // Pass the jobOffer ID to the controller

                    Stage stage = (Stage) cityErrorLabel.getScene().getWindow();
                    stage.centerOnScreen();
                    stage.setResizable(false);
                    stage.setScene(new Scene(root));
                    stage.show();
                } else {
                    // If user cancels, just close the form
                    closeForm();
                }
            } else {
                showError(titleErrorLabel, "A job offer with this title already exists.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
private boolean isValidForm() {
        hideErrorLabels();  // Hide all error labels initially

        boolean isValid = true;

        if (titleField.getText().trim().isEmpty()) {
            showError(titleErrorLabel, "Title is required.");
            isValid = false;
        }

        if (numberOfSpotsField.getText().trim().isEmpty()) {
            showError(numberOfSpotsErrorLabel, "Number of spots is required.");
            isValid = false;
        }  if (descriptionField.getText().trim().isEmpty()) {
            showError(descriptionErrorLabel, "Description is required.");
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
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeForm() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    @FXML
    private void handleCancelButtonClick() {
        closeForm();
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
                    citylabel.setVisible(false);
                    cityComboBox.setVisible(false);
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
