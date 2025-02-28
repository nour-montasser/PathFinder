package org.example.pathfinder.Controller;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.input.*;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.example.pathfinder.Model.CV;
import org.example.pathfinder.Model.Experience;
import org.example.pathfinder.Service.CVService;
import org.example.pathfinder.Service.ExperienceService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.awt.Desktop;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.example.pathfinder.Model.Certificate;
import org.example.pathfinder.Service.CertificateService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.pathfinder.Model.Language;
import org.example.pathfinder.Service.LanguageService;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;







public class CVController {

    // Character limits
    boolean disableGrammarCheck;
    private final int TITLE_MAX = 100;
    private final int INTRO_MAX = 300;
    private final int DESC_MAX = 500;
    private final int CERT_DESC_MAX = 300;
    private final int CERT_NAME_MAX = 150;
    private final int CERT_ASSOC_MAX = 150;
    private final int LOC_MAX = 100;
    private final int POS_MAX = 100;
    // CV Fields
    @FXML private Label titleErrorLabel,experienceErrorLabel,skillsErrorLabel;
    @FXML private Label introductionErrorLabel;
    @FXML private Label descriptionErrorLabel;
    @FXML private Label certificateDescriptionErrorLabel;
    @FXML private Label languageErrorLabel;
    @FXML
    private TextField titleField;
    @FXML
    private TextArea introductionField;
    @FXML
    private Label certificateHiddenPath;
    @FXML
    private CV editingCV = null; // Tracks whether we are editing an existing CV
    @FXML
    private Label titleCounter, introductionCounter, descriptionCounter, certificateDescriptionCounter;
    @FXML
    private Label certificateNameErrorLabel, certificateAssociationErrorLabel,  certificateDateErrorLabel;
    @FXML
    private Label positionErrorLabel, locationErrorLabel,descritpionErrorLabel, startDateErrorLabel, endDateErrorLabel, endDateValidationError,durationErrorLabel;
    @FXML
    private TextArea cvPreview;
    @FXML
    private Label certificateUploadError;
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
    @FXML
    private VBox uploadBox;
    @FXML
    private Label certificateMediaLabel;

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
    private VBox cvPreviewContainer;

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
    @FXML private TextFlow titleTextFlow;
    @FXML private TextFlow introductionTextFlow;

    private final String GRAMMAR_API_URL = "https://api.languagetool.org/v2/check";
    private final Map<String, List<String>> cachedCorrections = new HashMap<>();
    private Map<String, Map<String, String>> correctionSuggestions = new HashMap<>();


    private void setupGrammarCheck(TextInputControl textField) {
        Timeline typingDelay = new Timeline(new KeyFrame(Duration.millis(500), event -> {
            checkGrammarForField(textField);
        }));

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) { // Only check if value actually changed
                typingDelay.stop();
                typingDelay.playFromStart();
            }
        });

        textField.setOnContextMenuRequested(event -> showCorrectionMenu(event, textField));
    }

    private void checkGrammarForField(TextInputControl textField) {
        if (disableGrammarCheck) return; // Skip grammar check when auto-filling

        String text = textField.getText().trim();
        if (text.isBlank()) return;

        // Check cache first
        if (cachedCorrections.containsKey(text)) {
            highlightErrors(textField, text, cachedCorrections.get(text));
            return;
        }

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(GRAMMAR_API_URL).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            String requestBody = "text=" + URLEncoder.encode(text, "UTF-8") + "&language=en-US";
            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray matches = jsonResponse.getJSONArray("matches");

            Map<String, String> wordCorrections = new HashMap<>();
            for (int i = 0; i < matches.length(); i++) {
                JSONObject match = matches.getJSONObject(i);
                int offset = match.getInt("offset");
                int length = match.getInt("length");
                String incorrectWord = text.substring(offset, offset + length);

                JSONArray replacements = match.getJSONArray("replacements");
                if (replacements.length() > 0) {
                    String bestSuggestion = replacements.getJSONObject(0).getString("value");
                    wordCorrections.put(incorrectWord, bestSuggestion);
                }
            }

            cachedCorrections.put(text, new ArrayList<>(wordCorrections.keySet())); // Cache incorrect words
            highlightErrors(textField, text, new ArrayList<>(wordCorrections.keySet()));

            // Cache suggested corrections separately
            correctionSuggestions.put(text, wordCorrections);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void highlightErrors(TextInputControl textField, String fullText, List<String> incorrectWords) {
        if (incorrectWords.isEmpty()) {
            textField.setStyle("-fx-control-inner-background: white;"); // Reset to default background
            return;
        }

        // Apply red background for errors
        textField.setStyle("-fx-control-inner-background: #FFD2D2;");
    }


    private void showCorrectionMenu(ContextMenuEvent event, TextInputControl textField) {
        String text = textField.getText().trim();
        if (!cachedCorrections.containsKey(text)) return;

        List<String> incorrectWords = cachedCorrections.get(text);
        ContextMenu contextMenu = new ContextMenu();

        for (String incorrectWord : incorrectWords) {
            List<String> suggestions = fetchSuggestionsForWord(incorrectWord);

            for (String suggestion : suggestions) {
                MenuItem item = new MenuItem("Replace '" + incorrectWord + "' with '" + suggestion + "'");
                item.setOnAction(e -> textField.setText(text.replace(incorrectWord, suggestion)));
                contextMenu.getItems().add(item);
            }
        }

        if (!contextMenu.getItems().isEmpty()) {
            contextMenu.show(textField, event.getScreenX(), event.getScreenY());
        }
    }


    private List<String> fetchSuggestionsForWord(String word) {
        for (Map<String, String> suggestionMap : correctionSuggestions.values()) {
            if (suggestionMap.containsKey(word)) {
                return List.of(suggestionMap.get(word)); // Return actual suggested correction
            }
        }
        return List.of(); // No corrections found
    }
    private void ensureCertificateDirectoryExists() {
        File directory = new File(CERTIFICATE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs(); // Create directory if it does not exist
        }
    }
    private String extractDescription(String labelText) {
        Pattern pattern = Pattern.compile("(?i)Description: (.*)"); // Case-insensitive match for "Description: "
        Matcher matcher = pattern.matcher(labelText);
        if (matcher.find()) {
            return matcher.group(1).trim(); // Extract only the description part
        }
        return ""; // If no match is found, return an empty string
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
        // Apply Auto Caps to all text fields
        applyAutoCaps(titleField);
        applyAutoCaps(introductionField);
        applyAutoCaps(positionField);
        applyAutoCaps(locationField);
        applyAutoCaps(descriptionField);
        applyAutoCaps(certificateNameField);
        applyAutoCaps(certificateAssociationField);
        applyAutoCaps(certificateDescriptionField);
        setupCharacterLimit(titleField, titleCounter, TITLE_MAX);
        setupCharacterLimit(introductionField, introductionCounter, INTRO_MAX);
        setupCharacterLimit(descriptionField, descriptionCounter, DESC_MAX);
        setupCharacterLimit(certificateDescriptionField, certificateDescriptionCounter, CERT_DESC_MAX);
        setupCharacterLimit(positionField, null, POS_MAX);
        setupCharacterLimit(locationField, null, LOC_MAX);
        setupCharacterLimit(certificateNameField, null, CERT_NAME_MAX);
        setupCharacterLimit(certificateAssociationField, null, CERT_ASSOC_MAX);
        setupGrammarCheck(titleField);
        setupGrammarCheck(introductionField);
        setupGrammarCheck(descriptionField);
        setupGrammarCheck(certificateDescriptionField);



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
        certificateDatePicker.setDayCellFactory(picker -> new DateCell() {
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

    private void setupCharacterLimit(TextInputControl field, Label counter, int maxLength) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            int length = newValue.length();

            if (length > maxLength) {
                field.setText(oldValue);  // Prevent more characters
            }

            if (counter != null) {
                counter.setText(length + " / " + maxLength);
                updateCounterColor(counter, length, maxLength);
            }
        });
    }

    /**
     * Updates the counter color based on the character limit.
     */
    private void updateCounterColor(Label counter, int length, int maxLength) {
        if (length >= maxLength) {
            counter.setStyle("-fx-text-fill: red;");
        } else if (length > maxLength * 0.9) { // Turn orange at 90%
            counter.setStyle("-fx-text-fill: orange;");
        } else {
            counter.setStyle("-fx-text-fill: #666666;");
        }
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
        String position = positionField.getText().trim();
        String location = locationField.getText().trim();
        String description = descriptionField.getText().trim();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        // Reset error messages
        positionErrorLabel.setVisible(false);
        locationErrorLabel.setVisible(false);
        descritpionErrorLabel.setVisible(false);
        startDateErrorLabel.setVisible(false);
        endDateErrorLabel.setVisible(false);
        durationErrorLabel.setVisible(false); // 🔥 New label for duration validation

        boolean isValid = true;

        if (position.isEmpty()) {
            positionErrorLabel.setVisible(true);
            isValid = false;
        }
        if (location.isEmpty()) {
            locationErrorLabel.setVisible(true);
            isValid = false;
        }
        if (description.isEmpty()) {
            descritpionErrorLabel.setVisible(true);
            isValid = false;
        }
        if (startDate == null) {
            startDateErrorLabel.setVisible(true);
            isValid = false;
        }
        if (endDate == null) {
            endDateErrorLabel.setVisible(true);
            isValid = false;
        }

        // 🔥 Check if the duration is at least 7 days
        if (startDate != null && endDate != null) {
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
            if (daysBetween < 7) {
                durationErrorLabel.setText("The duration must be at least 7 days.");
                durationErrorLabel.setVisible(true);
                isValid = false;
            }
        }

        if (!isValid) return; // Stop execution if there’s an error

        if (editingExperience == null) {
            Experience experience = new Experience();
            experience.setType(type);
            experience.setPosition(position);
            experience.setLocationName(location);
            experience.setStartDate(startDate.toString());
            experience.setEndDate(endDate.toString());
            experience.setDescription(description);

            experiences.add(experience);
            addExperienceBox(experience);
        } else {
            editingExperience.setType(type);
            editingExperience.setPosition(position);
            editingExperience.setLocationName(location);
            editingExperience.setStartDate(startDate.toString());
            editingExperience.setEndDate(endDate.toString());
            editingExperience.setDescription(description);

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
    private void applyAutoCaps(TextInputControl textInput) {
        textInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) return; // Don't process empty input

            StringBuilder formattedText = new StringBuilder();
            boolean capitalizeNext = true;

            for (int i = 0; i < newValue.length(); i++) {
                char c = newValue.charAt(i);

                // Capitalize first letter or the letter after a period
                if (capitalizeNext && Character.isLetter(c)) {
                    formattedText.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    formattedText.append(c);
                }

                // Enable capitalization after a period + space
                if (c == '.') {
                    capitalizeNext = true;
                }
            }

            // Avoid infinite loop by checking if text has changed
            if (!formattedText.toString().equals(newValue)) {
                textInput.setText(formattedText.toString());
                textInput.positionCaret(formattedText.length()); // Keep caret at end
            }
        });
    }

    @FXML
    private void submitCV() {
        try {
            // Reset error labels
            titleErrorLabel.setVisible(false);
            introductionErrorLabel.setVisible(false);
            skillsErrorLabel.setVisible(false);
            experienceErrorLabel.setVisible(false);

            boolean isValid = true;

            // Validate required fields
            if (titleField.getText().trim().isEmpty()) {
                titleErrorLabel.setText("Title is required.");
                titleErrorLabel.setVisible(true);
                isValid = false;
            }
            if (introductionField.getText().trim().isEmpty()) {
                introductionErrorLabel.setText("Introduction is required.");
                introductionErrorLabel.setVisible(true);
                isValid = false;
            }
            if (skillsDropdown.getValue() == null) {
                skillsErrorLabel.setText("Please select a skill.");
                skillsErrorLabel.setVisible(true);
                isValid = false;
            }
            if (experiences.isEmpty()) {
                experienceErrorLabel.setText("At least one experience is required.");
                experienceErrorLabel.setVisible(true);
                isValid = false;
            }

            if (!isValid) return; // Stop execution if validation fails

            // Collect CV details
            String title = titleField.getText().trim();
            String introduction = introductionField.getText().trim();
            String skills = skillsDropdown.getValue().trim();

            // ✅ Check if updating an existing CV
            if (editingCV != null) {
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

            } else {
                // ✅ CREATE NEW CV
                CV cv = new CV(1, title, introduction, skills);
                cvService.add(cv);

                // Retrieve latest CV ID
                int latestCvId = cvService.getLatestCVId();
                if (latestCvId == -1) {
                    return; // Stop execution if CV ID retrieval fails
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
            }

            // ✅ Clear form after submission
            clearCVForm();
            editingCV = null; // Reset editing state

        } catch (Exception e) {
            e.printStackTrace();
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

    }
    private String wrapText(String text, int lineLength) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        StringBuilder wrappedText = new StringBuilder();
        String[] words = text.split("\\s+"); // Split words by spaces
        int currentLineLength = 0;

        for (String word : words) {
            if (currentLineLength + word.length() > lineLength) {
                wrappedText.append("\n"); // Move to next line
                currentLineLength = 0;
            }
            wrappedText.append(word).append(" ");
            currentLineLength += word.length() + 1;
        }

        return wrappedText.toString().trim();
    }

    // 🔹 Creates a header section (NAME + ROLE + CONTACT INFO)
    private VBox createHeaderSection() {
        VBox header = new VBox();
        header.setAlignment(Pos.CENTER);  // ✅ Center everything
        header.setSpacing(5);
        header.setMaxWidth(Double.MAX_VALUE);

        Label nameLabel = createStyledLabel("DANETTE EASTWOOD", "header");
        Label roleLabel = createStyledLabel("Full Stack Developer", "sub-header");
        Label contactLabel = createStyledLabel("+1-555-555-5555  •  danette.eastwood@gmail.com  •  github.io/danette.east  •  San Francisco, CA", "contact");
        nameLabel.setAlignment(Pos.CENTER);
        roleLabel.setAlignment(Pos.CENTER);
        contactLabel.setAlignment(Pos.CENTER);
        header.getChildren().addAll(nameLabel, roleLabel, contactLabel);
        return header;
    }

    @FXML
    private void updateCVPreview() {
        cvPreviewContainer.getChildren().clear(); // Clear previous preview

        // 🔹 HEADER (CENTERED)
        cvPreviewContainer.getChildren().add(createHeaderSection());

        // 🔹 SUMMARY (CENTERED WITH BROWN LINE)
        cvPreviewContainer.getChildren().add(createSectionLabel("Summary"));
        cvPreviewContainer.getChildren().add(createParagraphLabel(introductionField.getText()));

        // 🔹 EXPERIENCE (Internships ONLY)
        cvPreviewContainer.getChildren().add(createSectionLabel("Experience"));
        boolean hasInternship = false;
        for (Experience exp : experiences) {
            if (exp.getType().equalsIgnoreCase("Internship")) {
                hasInternship = true;
                VBox experienceBox = new VBox();
                experienceBox.setSpacing(3);

                // 🔹 Create an HBox to align Company Name (Left) and Date (Right)
                HBox companyDateBox = new HBox();
                companyDateBox.setSpacing(10);
                companyDateBox.setMaxWidth(Double.MAX_VALUE);

                Label company = createStyledLabel(exp.getLocationName(), "bold");
                Label dates = createStyledLabel(exp.getStartDate() + " - " + exp.getEndDate(), "small");

                HBox.setHgrow(company, Priority.ALWAYS);  // Pushes date to the right
                companyDateBox.getChildren().addAll(company, dates);

                // 🔹 Job Title & Description
                Label title = createStyledLabel(exp.getPosition(), "position_experience");
                Label desc = createParagraphLabel(exp.getDescription());

                experienceBox.getChildren().addAll(companyDateBox, title, desc);
                cvPreviewContainer.getChildren().add(experienceBox);
            }
        }
        if (!hasInternship) {
            cvPreviewContainer.getChildren().add(createParagraphLabel("No Internship Experience Listed"));
        }

        // 🔹 EDUCATION (Academic ONLY)
        cvPreviewContainer.getChildren().add(createSectionLabel("Education"));
        boolean hasAcademic = false;
        for (Experience exp : experiences) {
            if (exp.getType().equalsIgnoreCase("Academic")) {
                hasAcademic = true;
                VBox educationBox = new VBox();
                educationBox.setSpacing(3);

                // 🔹 Create an HBox to align School Name (Left) and Date (Right)
                HBox schoolDateBox = new HBox();
                schoolDateBox.setSpacing(10);
                schoolDateBox.setMaxWidth(Double.MAX_VALUE);

                Label school = createStyledLabel(exp.getLocationName(), "italic");
                Label dates = createStyledLabel(exp.getStartDate() + " - " + exp.getEndDate(), "small");

                HBox.setHgrow(school, Priority.ALWAYS);  // Pushes date to the right
                schoolDateBox.getChildren().addAll(school, dates);

                // 🔹 Degree Title & Description
                Label degree = createStyledLabel(exp.getPosition(), "position_experience");
                Label desc = createParagraphLabel(exp.getDescription());

                educationBox.getChildren().addAll(schoolDateBox, degree, desc);
                cvPreviewContainer.getChildren().add(educationBox);
            }
        }
        if (!hasAcademic) {
            cvPreviewContainer.getChildren().add(createParagraphLabel("No Education Listed"));
        }

        // 🔹 SKILLS (CENTERED WITH BROWN LINE)
        cvPreviewContainer.getChildren().add(createSectionLabel("Skills"));
        if (skillsDropdown.getValue() != null) {
            cvPreviewContainer.getChildren().add(createParagraphLabel(skillsDropdown.getValue()));
        } else {
            cvPreviewContainer.getChildren().add(createParagraphLabel("No Skills Listed"));
        }

        // 🔹 LANGUAGES (CENTERED WITH BROWN LINE)
        cvPreviewContainer.getChildren().add(createSectionLabel("Languages"));
        if (languages.isEmpty()) {
            cvPreviewContainer.getChildren().add(createParagraphLabel("No Languages Listed"));
        } else {
            for (Language lang : languages) {
                cvPreviewContainer.getChildren().add(createStyledLabel("• " + lang.getName() + " - " + lang.getLevel(), "small"));
            }
        }
    }



    // 🔹 Creates a section title (CENTERED)
    // 🔹 Creates a section title (CENTERED with Brown Line Underneath)
    private VBox createSectionLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #3B261D;");
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);

        // 🔹 Brown Line Under the Title
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #3B261D; ");
        separator.setPrefWidth(400); // Width of the line

        VBox sectionBox = new VBox(label, separator);
        sectionBox.setAlignment(Pos.CENTER); // ✅ Center both elements
        sectionBox.setSpacing(2); // Small spacing between title & line

        return sectionBox;
    }



    // 🔹 Creates a standard paragraph-style label
    private Label createParagraphLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(600);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
        return label;
    }

    // 🔹 Creates a custom styled label based on type
    private Label createStyledLabel(String text, String type) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(600);

        switch (type) {
            case "header":
                label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #3B261D;");
                break;
            case "sub-header":
                label.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #555555;");
                break;
            case "contact":
                label.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
                break;
            case "bold":
                label.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;-fx-text-fill: #3B261D;");
                break;
            case "italic":
                label.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
                break;
            case "position_experience":
                label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #555555;");
                break;
            case "small":
                label.setStyle("-fx-font-size: 12px; -fx-text-fill: #777777;");
                break;
        }
        return label;
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
    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    @FXML
    private void handleDragDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        boolean success = false;

        if (dragboard.hasFiles()) {
            File file = dragboard.getFiles().get(0); // Get the first file dropped

            if (validateFile(file)) {
                saveFile(file);
                certificateMediaLabel.setText(file.getName());
                certificateUploadError.setVisible(false);
                success = true;
            } else {
                certificateUploadError.setText("Invalid file type or size exceeds limit!");
                certificateUploadError.setVisible(true);
            }
        }

        event.setDropCompleted(success);
        event.consume();
    }
    private boolean validateFile(File file) {
        String fileName = file.getName().toLowerCase();
        long fileSize = file.length(); // Size in bytes (1MB = 1,048,576 bytes)

        // Allowed file types
        List<String> allowedExtensions = List.of(".jpg", ".jpeg", ".png", ".pdf", ".doc", ".docx");

        for (String ext : allowedExtensions) {
            if (fileName.endsWith(ext) && fileSize <= 5 * 1024 * 1024) { // 5MB max
                return true;
            }
        }
        return false;
    }
    private void saveFile(File file) {
        try {
            ensureCertificateDirectoryExists(); // Ensure directory exists
            String fileExtension = file.getName().substring(file.getName().lastIndexOf("."));
            String uniqueFileName = System.currentTimeMillis() + fileExtension;
            Path destinationPath = Path.of("view/Certificates/", uniqueFileName);

            Files.copy(file.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            certificateMediaLabel.setText(uniqueFileName); // Display filename
        } catch (IOException e) {
            e.printStackTrace();
            certificateUploadError.setText("Error saving file!");
            certificateUploadError.setVisible(true);
        }
    }

    @FXML
    private void uploadCertificateMedia() {
        ensureCertificateDirectoryExists(); // Ensure directory exists

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Certificate File");

        // ✅ Restrict allowed file types
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("Word Documents", "*.doc", "*.docx")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                long fileSize = Files.size(selectedFile.toPath()); // Get file size in bytes
                long maxSize = 5 * 1024 * 1024; // 5MB limit

                if (fileSize > maxSize) {
                    certificateUploadError.setText("File size exceeds 5MB limit.");
                    certificateUploadError.setVisible(true);
                    return; // Stop if the file is too large
                }

                // ✅ Generate unique filename
                String fileExtension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
                String uniqueFileName = System.currentTimeMillis() + fileExtension;

                // ✅ Save the file to destination folder
                File destinationFile = new File(CERTIFICATE_DIRECTORY + uniqueFileName);
                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // ✅ Update UI: Show file name & hide error message
                certificateHiddenPath.setText(destinationFile.getAbsolutePath()); // Store full path
                certificateMediaLabel.setText(uniqueFileName);
                certificateUploadError.setVisible(false); // Hide error

            } catch (IOException e) {
                e.printStackTrace();
                certificateUploadError.setText("Error saving file.");
                certificateUploadError.setVisible(true);
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
        String description = certificateDescriptionField.getText().trim();
        LocalDate date = certificateDatePicker.getValue();
        String mediaPath = certificateMediaLabel.getText();

        // Reset error messages
        certificateNameErrorLabel.setVisible(false);
        certificateAssociationErrorLabel.setVisible(false);
        certificateDescriptionErrorLabel.setVisible(false);
        certificateDateErrorLabel.setVisible(false);
        certificateUploadError.setVisible(false);

        boolean isValid = true;

        if (name.isEmpty()) {
            certificateNameErrorLabel.setVisible(true);
            isValid = false;
        }
        if (association.isEmpty()) {
            certificateAssociationErrorLabel.setVisible(true);
            isValid = false;
        }
        if (description.isEmpty()) {
            certificateDescriptionErrorLabel.setVisible(true);
            isValid = false;
        }
        if (date == null) {
            certificateDateErrorLabel.setVisible(true);
            isValid = false;
        }
        if (mediaPath.equals("No file selected")) {
            certificateUploadError.setVisible(true);
            isValid = false;
        }

        if (!isValid) return; // Stop execution if there's an error

        if (editingCertificate == null) {
            Certificate certificate = new Certificate(0, name, description, mediaPath, association, Date.valueOf(date));
            certificates.add(certificate);
            addCertificateBox(certificate);
        } else {
            editingCertificate.setTitle(name);
            editingCertificate.setAssociation(association);
            editingCertificate.setDescription(description);
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
        // Ensure file path is correct
        File file = new File("view/Certificates/" + fileName).getAbsoluteFile();

        System.out.println("Opening file: " + file.getAbsolutePath());

        if (!file.exists()) {
            System.err.println("❌ File not found!");
            return;
        }

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            } else {
                new ProcessBuilder("xdg-open", file.getAbsolutePath()).start(); // Linux fallback
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Error opening file.");
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
            new Timeline(new KeyFrame(Duration.millis(1000), event -> {
                disableGrammarCheck = false;
                checkGrammarForField(titleField);
                checkGrammarForField(introductionField);
            })).play();

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