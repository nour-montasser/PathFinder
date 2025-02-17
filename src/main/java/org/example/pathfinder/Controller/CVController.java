package org.example.pathfinder.Controller;
import org.example.pathfinder.Model.CV;
import org.example.pathfinder.Model.Experience;
import org.example.pathfinder.Service.CVService;
import org.example.pathfinder.Service.ExperienceService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.awt.Desktop;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.example.pathfinder.Model.Certificate;
import org.example.pathfinder.Service.CertificateService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.pathfinder.Model.Language;
import org.example.pathfinder.Service.LanguageService;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;







public class CVController {
    // CV Fields
    @FXML
    private TextField titleField;
    @FXML
    private TextArea introductionField;
    @FXML
    private CV editingCV = null; // Tracks whether we are editing an existing CV


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
    private TextArea descriptionField; // ADDED DESCRIPTION FIELD
    @FXML
    private ComboBox<String> typeDropdown;
    @FXML
    private ComboBox<String> skillsDropdown;
    @FXML
    private TextField positionField;
    @FXML
    private TextField locationField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;

    private  List<Experience> experiences = new ArrayList<>();
    private Experience editingExperience = null; // To track editing mode
    private Certificate editingCertificate = null; // To track editing mode

    // Services
    private final CVService cvService = new CVService();
    private final ExperienceService experienceService = new ExperienceService();
    private static final String CERTIFICATE_DIRECTORY = "view/Certificates/";
    @FXML
    private VBox certificateContainer;

    @FXML
    private StackPane certificateModalOverlay;

    @FXML
    private VBox certificateModal;

    @FXML
    private TextField certificateNameField;

    @FXML
    private TextField certificateAssociationField;

    @FXML
    private DatePicker certificateDatePicker;

    @FXML
    private Label certificateMediaLabel;
    @FXML
    private TextArea certificateDescriptionField; // Added description field for certificates


    private  List<Certificate> certificates = new ArrayList<>();

    private final CertificateService certificateService = new CertificateService();
    @FXML
    private VBox languageContainer;

    @FXML
    private ComboBox<String> languagesDropdown;

    @FXML
    private HBox languageLevelContainer;

    @FXML
    private HBox levelDots;

    private int selectedLevel = 0; // Stores selected level

    private final ObservableList<String> allLanguages = FXCollections.observableArrayList();

    private  List<Language> languages = new ArrayList<>();

    private final LanguageService languageService = new LanguageService();
    private String selectedLanguageLevel = ""; // Stores the selected level as text



    private void ensureCertificateDirectoryExists() {
        File directory = new File(CERTIFICATE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs(); // Create directory if it does not exist
        }
    }

    @FXML
    private void fetchSkills() {
        String apiUrl = "https://ec.europa.eu/esco/api/search?type=skill&text=&language=en&limit=100&";
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            // Log raw API response
            System.out.println("API Response: " + response);

            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            if (!jsonResponse.has("_embedded")) {
                System.out.println("The key '_embedded' is missing in the API response!");
                return;
            }

            JSONArray skillsArray = jsonResponse.getJSONObject("_embedded").getJSONArray("results");

            // Clear previous items and add new skills
            skillsDropdown.getItems().clear();
            for (int i = 0; i < skillsArray.length(); i++) {
                JSONObject skill = skillsArray.getJSONObject(i);
                String skillName = skill.getString("title");

                // Only include short, single-word skills (or up to 2 words)
                if (skillName.split(" ").length <= 2) {
                    skillsDropdown.getItems().add(skillName);
                }
            }

            // Notify if no skills were found
            if (skillsDropdown.getItems().isEmpty()) {
                System.out.println("No concise skills found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void populateLanguagesFromAPI() {
        try {
            URL url = new URL("https://restcountries.com/v3.1/all");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse JSON response
            ObservableList<String> languageOptions = FXCollections.observableArrayList();
            JSONArray countries = new JSONArray(response.toString());

            for (int i = 0; i < countries.length(); i++) {
                JSONObject country = countries.getJSONObject(i);
                if (country.has("languages")) {
                    JSONObject languagesJson = country.getJSONObject("languages");
                    for (String key : languagesJson.keySet()) {
                        String language = languagesJson.getString(key);
                        if (!languageOptions.contains(language)) {
                            languageOptions.add(language);
                        }
                    }
                }
            }

            // Sort & set dropdown items
            FXCollections.sort(languageOptions);
            allLanguages.setAll(languageOptions);
            languagesDropdown.setItems(allLanguages);

            // Enable searching but prevent new entries
            languagesDropdown.setEditable(true);
            languagesDropdown.getEditor().textProperty().addListener((observable, oldValue, newValue) -> filterLanguages(newValue));

            // Ensure dropdown resets to a valid selection
            languagesDropdown.getEditor().setOnAction(event -> {
                if (!allLanguages.contains(languagesDropdown.getEditor().getText())) {
                    languagesDropdown.getEditor().clear(); // Prevent non-existing input
                    languagesDropdown.getSelectionModel().clearSelection();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void filterLanguages(String query) {
        if (query == null || query.trim().isEmpty()) {
            if (!languagesDropdown.getItems().equals(allLanguages)) {
                languagesDropdown.setItems(allLanguages); // Restore full list safely
            }
            return;
        }

        ObservableList<String> filteredList = FXCollections.observableArrayList();
        for (String language : allLanguages) {
            if (language.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(language);
            }
        }

        // Only update if the filtered list is different
        if (!filteredList.equals(languagesDropdown.getItems())) {
            languagesDropdown.setItems(filteredList);
        }
    }



    @FXML
    private void showLanguageLevelSelection() {
        String selectedLanguage = languagesDropdown.getValue();
        languageLevelContainer.setVisible(selectedLanguage != null);

        if (selectedLanguage != null) {
            resetLevelSelection(); // Reset level selection when a new language is chosen
        }
    }
    private String getStoredLanguageLevel(String languageName) {
        for (Language lang : languages) {
            if (lang.getName().equals(languageName)) {
                return lang.getLevel(); // Return stored level if found
            }
        }
        return ""; // Default empty if not found
    }

    // ============================== 🔹 LEVEL SELECTION (DOT SYSTEM) 🔹 ============================== //

    @FXML
    private void setLanguageLevel1() { selectLevel(1); }

    @FXML
    private void setLanguageLevel2() { selectLevel(2); }

    @FXML
    private void setLanguageLevel3() { selectLevel(3); }

    @FXML
    private void setLanguageLevel4() { selectLevel(4); }

    @FXML
    private void setLanguageLevel5() { selectLevel(5); }


    @FXML
    private void selectLevel(int level) {
        selectedLevel = level;
        updateLanguageDots(levelDots.getChildren(), level); // Now it works!
    }

    @FXML
    private void resetLevelSelection() {
        selectedLevel = 0;
        for (int i = 0; i < levelDots.getChildren().size(); i++) {
            Label dot = (Label) levelDots.getChildren().get(i);
            dot.setText("⚪");
        }
    }
    @FXML
    private int convertLevelToDots(String level) {
        return switch (level.toLowerCase()) {
            case "beginner" -> 1;
            case "novice" -> 2;
            case "intermediate" -> 3;
            case "advanced" -> 4;
            case "expert" -> 5;
            default -> 1; // Default to beginner
        };
    }
    // Converts number of dots clicked to level name
    private String convertDotsToLevel(int dots) {
        return switch (dots) {
            case 1 -> "Beginner";
            case 2 -> "Novice";
            case 3 -> "Intermediate";
            case 4 -> "Advanced";
            case 5 -> "Expert";
            default -> "Beginner";
        };
    }

    // Updates the UI to reflect selected dots
    @FXML
    private void updateLanguageDots(ObservableList<Node> dotNodes, int selectedLevel) {
        for (int i = 0; i < dotNodes.size(); i++) {
            Label dot = (Label) dotNodes.get(i); // Cast Node to Label
            dot.setText(i < selectedLevel ? "⚫" : "⚪");
        }
    }

    @FXML
    private void addLanguage() {
        String selectedLanguage = languagesDropdown.getValue();

        if (selectedLanguage == null || selectedLanguage.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please select a valid language.");
            return;
        }

        if (selectedLevel == 0) {
            showAlert(Alert.AlertType.WARNING, "Please select a proficiency level.");
            return;
        }

        String levelText = convertDotsToLevel(selectedLevel);

        // Check if the language already exists
        for (Language lang : languages) {
            if (lang.getName().equalsIgnoreCase(selectedLanguage)) {
                // Update existing language's level
                lang.setLevel(levelText);
                refreshLanguageContainer(); // Refresh UI to reflect changes
                return; // Exit early to prevent duplicate entry
            }
        }

        // If language does not exist, add it as a new entry
        Language newLanguage = new Language(0, selectedLanguage, levelText);
        languages.add(newLanguage);
        addLanguageBox(newLanguage);

        // Remove selected language from dropdown to prevent duplicate addition
        allLanguages.remove(selectedLanguage);
        languagesDropdown.setItems(allLanguages);

        // Reset selections
        languagesDropdown.getSelectionModel().clearSelection();
        selectedLevel = 0;
        languageLevelContainer.setVisible(false);
    }
    @FXML
    private void refreshLanguageContainer() {
        languageContainer.getChildren().clear();
        for (Language language : languages) {
            addLanguageBox(language);
        }
    }



    @FXML

    private void addLanguageBox(Language language) {
        HBox languageBox = new HBox(10);
        languageBox.setStyle("-fx-background-color: #F5EDE1; -fx-border-color: #3B261D; -fx-border-width: 2px; " +
                "-fx-padding: 10px; -fx-border-radius: 8px; -fx-alignment: center-left;");


        Label languageLabel = new Label(language.getName() + " - " + language.getLevel());
        languageLabel.setStyle("-fx-text-fill: #3B261D; -fx-font-size: 14px; -fx-font-weight: bold;");


        HBox dotContainer = new HBox(5);
        for (int i = 1; i <= 5; i++) {
            Label dot = new Label(i <= convertLevelToDots(language.getLevel()) ? "⚫" : "⚪");
            dot.setStyle("-fx-font-size: 14px; -fx-text-fill: #3B261D;");
            dotContainer.getChildren().add(dot);
        }

        Button editButton = createStyledButton("Edit", "#4CAF50", "#FFFFFF");
        editButton.setOnAction(e -> editLanguage(language, languageBox));

        Button deleteButton = createStyledButton("Delete", "#FF5555", "#FFFFFF");
        deleteButton.setOnAction(e -> deleteLanguage(language, languageBox));

        languageBox.getChildren().addAll(languageLabel, dotContainer, editButton, deleteButton);
        languageContainer.getChildren().add(languageBox);
    }
    @FXML
    private void editLanguage(Language language, HBox languageBox) {
        // Restore the language to dropdown temporarily for selection
        if (!allLanguages.contains(language.getName())) {
            allLanguages.add(language.getName());
            FXCollections.sort(allLanguages);
            languagesDropdown.setItems(allLanguages);
        }

        // Set fields for editing
        languagesDropdown.setValue(language.getName());
        selectedLevel = convertLevelToDots(language.getLevel());
        updateLanguageDots(levelDots.getChildren(), selectedLevel);

        // Remove the old entry from the list and UI
        languages.remove(language);
        languageContainer.getChildren().remove(languageBox);
    }


    @FXML
    private void deleteLanguage(Language language, HBox languageBox) {
        languages.remove(language);
        languageContainer.getChildren().remove(languageBox);
        updateCVPreview();

        // Restore the deleted language back to the dropdown
        allLanguages.add(language.getName());
        FXCollections.sort(allLanguages);
        languagesDropdown.setItems(allLanguages);
    }





    @FXML
    public void initialize() {
        // ✅ Ensure languages are fetched from API
        populateLanguagesFromAPI(); // RESTORED API CALL

        // ✅ Ensure skills dropdown is populated
        fetchSkills(); // Ensure we load skills

        // ✅ Populate Experience Types
        typeDropdown.setItems(FXCollections.observableArrayList("Academic", "Internship"));
        typeDropdown.setValue("Academic");

        // ✅ Set Default Start Date
        startDatePicker.setValue(LocalDate.now());

        // ✅ Restrict Start Date (Min 1940-01-01)
        startDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isBefore(LocalDate.of(1940, 1, 1)) || date.isAfter(LocalDate.now()));
            }
        });

        // ✅ Restrict End Date (Cannot be before Start Date)
        endDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (startDatePicker.getValue() != null) {
                    setDisable(date.isBefore(startDatePicker.getValue()) || date.isAfter(LocalDate.now()));
                } else {
                    setDisable(date.isAfter(LocalDate.now())); // Prevent future dates if no start date
                }
            }
        });

        // ✅ Ensure End Date updates when Start Date changes
        startDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                endDatePicker.setValue(newDate.plusDays(1)); // Ensure End Date is after Start
                endDatePicker.setDayCellFactory(picker -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        setDisable(date.isBefore(newDate) || date.isAfter(LocalDate.now()));
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
    private void clearExperienceForm() {
        typeDropdown.setValue("Academic");
        positionField.clear();
        locationField.clear();
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now());
        descriptionField.clear();

        editingExperience = null;
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
    private void addExperienceBox(Experience experience) {
        HBox experienceBox = new HBox(10);

        experienceBox.setStyle("-fx-background-color: #F5EDE1; -fx-border-color: #3B261D; -fx-border-width: 2px; " +
                "-fx-padding: 10px; -fx-border-radius: 8px; -fx-alignment: center-left;");

        Label details = new Label(
                experience.getType() + ": " + experience.getPosition() + " at " + experience.getLocationName() +
                        " (" + experience.getStartDate() + " to " + experience.getEndDate() + ")\n" +
                        "Description: " + experience.getDescription() // ADDED DESCRIPTION
        );
        details.setStyle("-fx-text-fill: #3B261D; -fx-font-size: 14px; -fx-font-weight: bold;");

        Button editButton = createStyledButton("Edit", "#4CAF50", "#FFFFFF");
        editButton.setOnAction(e -> {
            editingExperience = experience;
            typeDropdown.setValue(experience.getType());
            positionField.setText(experience.getPosition());
            locationField.setText(experience.getLocationName());
            descriptionField.setText(experience.getDescription()); // Set Description
            startDatePicker.setValue(LocalDate.parse(experience.getStartDate().split(" ")[0]));
            endDatePicker.setValue(LocalDate.parse(experience.getEndDate().split(" ")[0]));

            showExperienceModal();
        });

        Button deleteButton = createStyledButton("Delete", "#FF5555", "#FFFFFF");
        deleteButton.setOnAction(e -> {
            experiences.remove(experience);
            experienceContainer.getChildren().remove(experienceBox);
            updateCVPreview();
        });

        experienceBox.getChildren().addAll(details, editButton, deleteButton);
        experienceContainer.getChildren().add(experienceBox);
    }
    @FXML
    private void addExperience() {
        String type = typeDropdown.getValue();
        String position = positionField.getText();
        String location = locationField.getText();
        String description = descriptionField.getText(); // Get Description
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (position.isEmpty() || location.isEmpty() || startDate == null || endDate == null || description.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "All fields must be filled out!");
            return;
        }

        if (endDate.isBefore(startDate)) {
            showAlert(Alert.AlertType.WARNING, "End Date must be after Start Date.");
            return;
        }

        if (editingExperience == null) {
            Experience experience = new Experience();
            experience.setType(type);
            experience.setPosition(position);
            experience.setLocationName(location);
            experience.setStartDate(startDate.toString());
            experience.setEndDate(endDate.toString());
            experience.setDescription(description); // Set Description

            experiences.add(experience);
            addExperienceBox(experience);
        } else {
            editingExperience.setType(type);
            editingExperience.setPosition(position);
            editingExperience.setLocationName(location);
            editingExperience.setStartDate(startDate.toString());
            editingExperience.setEndDate(endDate.toString());
            editingExperience.setDescription(description); // Update Description

            refreshExperienceContainer();
        }

        updateCVPreview();
        hideExperienceModal(null);
    }

    @FXML
    private void refreshExperienceContainer() {
        experienceContainer.getChildren().clear();
        for (Experience experience : experiences) {
            addExperienceBox(experience);
        }
    }

    @FXML
    private void submitCV() {
        try {
            // Validate required fields
            if (titleField.getText().trim().isEmpty() || introductionField.getText().trim().isEmpty() ||
                    skillsDropdown.getValue() == null || experiences.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Please fill in all required fields before submitting.");
                return;
            }

            // Collect CV details
            String title = titleField.getText().trim();
            String introduction = introductionField.getText().trim();
            String skills = skillsDropdown.getValue().trim();

            // Check if updating an existing CV
            if (editingCV != null) { // 🔥 Check if we have an existing CV loaded
                // ✅ UPDATE EXISTING CV
                editingCV.setTitle(title);
                editingCV.setIntroduction(introduction);
                editingCV.setSkills(skills);
                cvService.update(editingCV);

                // ✅ Handle Experiences
                List<Experience> originalExperiences = experienceService.getByCvId(editingCV.getIdCV());
                for (Experience originalExp : originalExperiences) {
                    boolean stillExists = false;
                    for (Experience currentExp : experiences) {
                        if (originalExp.getIdExperience() == currentExp.getIdExperience()) {
                            stillExists = true;
                            break;
                        }
                    }
                    if (!stillExists) {
                        experienceService.delete(originalExp.getIdExperience()); // 🔥 DELETE from DB
                    }
                }
                for (Experience experience : experiences) {
                    if (experience.getIdExperience() == 0) {
                        experience.setIdCv(editingCV.getIdCV());
                        experienceService.add(experience);
                    } else {
                        experienceService.update(experience);
                    }
                }

                // ✅ Handle Certificates
                List<Certificate> originalCertificates = certificateService.getByCvId(editingCV.getIdCV());
                for (Certificate originalCert : originalCertificates) {
                    boolean stillExists = false;
                    for (Certificate currentCert : certificates) {
                        if (originalCert.getIdCertificate() == currentCert.getIdCertificate()) {
                            stillExists = true;
                            break;
                        }
                    }
                    if (!stillExists) {
                        certificateService.delete(originalCert.getIdCertificate()); // 🔥 DELETE from DB
                    }
                }
                for (Certificate certificate : certificates) {
                    if (certificate.getIdCertificate() == 0) {
                        certificate.setIdCv(editingCV.getIdCV());
                        certificateService.add(certificate);
                    } else {
                        certificateService.update(certificate);
                    }
                }

                // ✅ Handle Languages
                List<Language> originalLanguages = languageService.getByCvId(editingCV.getIdCV());
                System.out.println(originalLanguages);
                for (Language originalLang : originalLanguages) {
                    boolean stillExists = false;

                    for (Language currentLang : languages) {
                        if (originalLang.getIdLanguage() == currentLang.getIdLanguage()) {
                            stillExists = true;
                            break;
                        }
                    }
                    if (!stillExists) {
                        languageService.delete(originalLang.getIdLanguage()); // 🔥 DELETE from DB
                    }
                }
                for (Language language : languages) {
                    if (language.getIdLanguage() == 0) {
                        language.setCvId(editingCV.getIdCV());
                        languageService.add(language);
                    } else {
                        languageService.update(language);
                    }
                }

                showAlert(Alert.AlertType.INFORMATION, "CV Updated Successfully!"); // 🎉

            } else {
                // ✅ CREATE NEW CV (Same as before)
                CV cv = new CV(1, title, introduction, skills);
                cvService.add(cv);

                // Retrieve latest CV ID
                int latestCvId = cvService.getLatestCVId();
                if (latestCvId == -1) {
                    showAlert(Alert.AlertType.ERROR, "Failed to retrieve the latest CV ID.");
                    return;
                }

                // Store experiences linked to the CV
                for (Experience experience : experiences) {
                    experience.setIdCv(latestCvId);
                    experienceService.add(experience);
                }

                // Store certificates linked to the CV
                for (Certificate certificate : certificates) {
                    certificate.setIdCv(latestCvId);
                    certificateService.add(certificate);
                }

                // Store languages linked to the CV
                for (Language language : languages) {
                    language.setCvId(latestCvId);
                    languageService.add(language);
                }

                showAlert(Alert.AlertType.INFORMATION, "CV Submitted Successfully!");
            }

            // ✅ Clear form after submission
            clearCVForm();
            editingCV = null; // Reset editing state

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "An error occurred while saving the CV.");
        }
    }



    @FXML
    private void clearCVForm() {
        titleField.clear();
        introductionField.clear();
        skillsDropdown.getSelectionModel().clearSelection();

        // Clear experiences
        experiences.clear();
        experienceContainer.getChildren().clear();

        // Clear certificates
        certificates.clear();
        certificateContainer.getChildren().clear();

        // Clear languages
        languages.clear();
        languageContainer.getChildren().clear();

        // Reset CV preview
        cvPreview.clear();
    }


    @FXML
    private void updateCVPreview() {
        StringBuilder preview = new StringBuilder();
        preview.append("Title: ").append(titleField.getText()).append("\n");
        preview.append("Introduction: ").append(introductionField.getText()).append("\n");

        preview.append("Skills: ").append(skillsDropdown.getValue()).append("\n\n");

        preview.append("Experiences:\n");
        for (Experience exp : experiences) {
            preview.append("- ").append(exp.getType()).append(": ").append(exp.getPosition())
                    .append(" at ").append(exp.getLocationName())
                    .append(" (").append(exp.getStartDate()).append(" to ").append(exp.getEndDate()).append(")\n");
        }

        preview.append("\nCertificates:\n");
        for (Certificate cert : certificates) {
            preview.append("- ").append(cert.getTitle()).append(" (").append(cert.getAssociation())
                    .append(", ").append(cert.getDate()).append(")\n")
                    .append("  Description: ").append(cert.getDescription()).append("\n"); // Added description
        }

        cvPreview.setText(preview.toString());
    }


    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.showAndWait();
    }
    //Certificate
    @FXML
    private void showCertificateModal() {
        certificateModalOverlay.setVisible(true);
    }
    @FXML
    private void hideCertificateModal(MouseEvent event) {
        if (!certificateModal.isVisible()) return;
        certificateModalOverlay.setVisible(false);
        clearCertificateForm();
    }
    @FXML
    private void clearCertificateForm() {
        certificateNameField.clear();
        certificateAssociationField.clear();
        certificateDatePicker.setValue(null);
        certificateMediaLabel.setText("No file selected");
    }
    @FXML
    private void uploadCertificateMedia() {
        ensureCertificateDirectoryExists(); // Ensure directory exists

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Certificate File");

        // Set allowed file extensions
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("Word Documents", "*.doc", "*.docx")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                // Generate unique filename
                String fileExtension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
                String uniqueFileName = System.currentTimeMillis() + fileExtension;

                // Destination path
                File destinationFile = new File(CERTIFICATE_DIRECTORY + uniqueFileName);

                // Copy file to destination
                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Set file name in the label
                certificateMediaLabel.setText(uniqueFileName);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error saving certificate file.");
            }
        }
    }

    @FXML
    private void addCertificateBox(Certificate certificate) {
        HBox certificateBox = new HBox(10);
        certificateBox.setStyle("-fx-background-color: #F5EDE1; -fx-border-color: #3B261D; -fx-border-width: 2px; " +
                "-fx-padding: 10px; -fx-border-radius: 8px; -fx-alignment: center-left;");

        Label details = new Label(
                certificate.getTitle() + " (" + certificate.getAssociation() + ") - " + certificate.getDate() +
                        "\nDescription: " + certificate.getDescription() // Display description
        );
        details.setStyle("-fx-text-fill: #3B261D; -fx-font-size: 14px; -fx-font-weight: bold;");


        Button openFileButton = createStyledButton("Open", "#3B261D", "#FFFFFF");
        openFileButton.setOnAction(e -> openCertificateFile(certificate.getMedia()));

        Button editButton = createStyledButton("Edit", "#4CAF50", "#FFFFFF");
        editButton.setOnAction(e -> {
            editingCertificate = certificate;
            certificateNameField.setText(certificate.getTitle());
            certificateAssociationField.setText(certificate.getAssociation());
            certificateDescriptionField.setText(certificate.getDescription());
            certificateDatePicker.setValue(certificate.getDate().toLocalDate());
            certificateMediaLabel.setText(certificate.getMedia());
            showCertificateModal();
        });

        Button deleteButton = createStyledButton("Delete", "#FF5555", "#FFFFFF");
        deleteButton.setOnAction(e -> {
            certificates.remove(certificate);
            certificateContainer.getChildren().remove(certificateBox);
            updateCVPreview();
        });

        certificateBox.getChildren().addAll(details, openFileButton, editButton, deleteButton);
        certificateContainer.getChildren().add(certificateBox);
    }



    @FXML
    private void addCertificate() {
        String name = certificateNameField.getText().trim();
        String association = certificateAssociationField.getText().trim();
        String description = certificateDescriptionField.getText().trim(); // Get description
        LocalDate date = certificateDatePicker.getValue();
        String mediaPath = certificateMediaLabel.getText();

        if (name.isEmpty() || association.isEmpty() || description.isEmpty() || date == null || mediaPath.equals("No file selected")) {
            showAlert(Alert.AlertType.WARNING, "Please fill in all certificate fields.");
            return;
        }

        if (editingCertificate == null) {
            Certificate certificate = new Certificate(0, name,description,mediaPath, association, Date.valueOf(date));
            certificate.setDescription(description); // Set description
            certificates.add(certificate);
            addCertificateBox(certificate);
        } else {
            editingCertificate.setTitle(name);
            editingCertificate.setAssociation(association);
            editingCertificate.setDescription(description); // Update description
            editingCertificate.setDate(Date.valueOf(date));
            editingCertificate.setMedia(mediaPath);
            refreshCertificateContainer();
            editingCertificate = null;
        }

        clearCertificateForm();
        hideCertificateModal(null);
    }



    @FXML
    private void refreshCertificateContainer() {
        certificateContainer.getChildren().clear();
        for (Certificate certificate : certificates) {
            addCertificateBox(certificate);
        }
    }
    @FXML
    private void openCertificateFile(String fileName) {
        File file = new File(CERTIFICATE_DIRECTORY + fileName);
        if (!file.exists()) {
            showAlert(Alert.AlertType.ERROR, "File not found!");
            return;
        }

        try {
            Desktop.getDesktop().open(file); // Open with default app
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error opening file.");
        }
    }
    @FXML
    private void hideCertificateModalCancel() {
        certificateModalOverlay.setVisible(false);
        clearCertificateForm(); // Optional: Clear fields when closing
    }

    public void loadCVData(int cvId) {
         editingCV= cvService.getById(cvId); // 🔥 Fetch CV from database
        if (editingCV!= null) {
            // 🔹 Fill in basic details
            titleField.setText(editingCV.getTitle());
            introductionField.setText(editingCV.getIntroduction());
            skillsDropdown.setValue(editingCV.getSkills()); // 🔥 Adjust if Skills is a list

            // 🔹 Clear old data before inserting new ones
            experienceContainer.getChildren().clear();
            certificateContainer.getChildren().clear();
            languageContainer.getChildren().clear();

            // 🔥 Load Experiences
            experiences = editingCV.getExperiences();
            if (experiences != null) {
                for (Experience experience : experiences) {
                    addExperienceBox(experience);
                }
            }

            // 🔥 Load Certificates
          certificates = editingCV.getCertificates();
            if (certificates != null) {
                for (Certificate certificate : certificates) {
                    addCertificateBox(certificate);
                }
            }

            // 🔥 Load Languages
         languages = editingCV.getLanguageList();

            if (languages != null) {
                for (Language language : languages) {
                    addLanguageBox(language);
                }
            }

        } else {
            System.err.println("❌ CV not found for ID: " + cvId);
        }
    }
    // 🔥 Styled Button Factory
    private Button createStyledButton(String text, String bgColor, String textColor) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: " + textColor + "; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 8px 12px; -fx-border-radius: 6px;");
        return button;
    }



}