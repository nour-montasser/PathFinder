package org.example.pathfinder.Controller;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfPage;
//import com.itextpdf.layout.properties.TextAlignment;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.example.pathfinder.Model.*;
import org.example.pathfinder.Service.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import java.awt.Desktop;
import java.io.*;
import java.net.MalformedURLException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;



public class CVController {

    private long loggedInUserId = LoggedUser.getInstance().getUserId();
    // ProfileService profileService;
    //Profile profile=profileService.getOne(loggedInUserId);
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
    private VBox summaryContainer;
    @FXML
    private VBox headerContainer;
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
    @FXML private ComboBox<String> skillComboBox; // Editable search field for skills
    @FXML private FlowPane selectedSkillsFlow;      // Container for the added skill tags

    private final ObservableList<String> availableSkills = FXCollections.observableArrayList();
    private final ObservableList<String> selectedSkills = FXCollections.observableArrayList();

    private Timeline skillSearchDelayTimeline;

    private final String GRAMMAR_API_URL = "https://api.languagetool.org/v2/check";
    private final Map<String, List<String>> cachedCorrections = new HashMap<>();
    private Map<String, Map<String, String>> correctionSuggestions = new HashMap<>();

    private static final String API_KEY = "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";
    private static final String API_URL = "https://api-inference.huggingface.co/models/algiraldohe/lm-ner-linkedin-skills-recognition"; // Model endpoint
    @FXML private VBox experiencePreviewContainer;
    @FXML private VBox educationPreviewContainer;
    @FXML private VBox skillsPreviewContainer;
    @FXML private VBox languagesPreviewContainer;
    @FXML private VBox certificatesPreviewContainer;
    @FXML private StackPane downloadModalOverlay;
    @FXML private VBox downloadModal;
    @FXML private ComboBox<String> fileTypeDropdown;
    @FXML private ComboBox<String> pageRangeDropdown;
    @FXML private CheckBox addCertficatesCheckbox;
    UserService userService;

    @FXML
    private void showDownloadModal() {
        downloadModalOverlay.setVisible(true);
    }
    @FXML
    private void downloadCV() {
        String selectedFileType = fileTypeDropdown.getValue();
        boolean flatten = addCertficatesCheckbox.isSelected();
        System.out.println("Downloading as: " + selectedFileType);
        System.out.println("Flatten: " + flatten);

        if (selectedFileType.contains("A4")) {
            exportToPDF();
        } else if (selectedFileType.contains("Image")) {
            exportToImage();
        }
        else if (selectedFileType.contains("PDF "))
        {
            exportToPDF2();
        }

        hideDownloadModalCancel(); // Close modal after download
    }


    private void exportToImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                WritableImage snapshot = cvPreviewContainer.snapshot(null, null);
                BufferedImage fullImage = SwingFXUtils.fromFXImage(snapshot, null);
                ImageIO.write(fullImage, "png", file);

                System.out.println("✅ Image Exported: " + file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    @FXML
    private void exportToPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                // Take high-resolution snapshot of cvPreviewContainer

                SnapshotParameters params = new SnapshotParameters();
                WritableImage snapshot = new WritableImage(
                        (int) cvPreviewContainer.getWidth(),
                        (int) cvPreviewContainer.getHeight()
                );


                cvPreviewContainer.snapshot(params, snapshot);

                BufferedImage fullImage = SwingFXUtils.fromFXImage(snapshot, null);

                // Setup PDF
                PdfWriter writer = new PdfWriter(new FileOutputStream(file));
                PdfDocument pdfDoc = new PdfDocument(writer);
                Document document = new Document(pdfDoc, PageSize.A4);

                float a4Width = PageSize.A4.getWidth(); // 595 points (A4 width)
                float a4Height = PageSize.A4.getHeight(); // 842 points (A4 height)

                int imgWidth = fullImage.getWidth();
                int imgHeight = fullImage.getHeight();

                // Scale width to A4 and adjust height proportionally
                float scaleFactor = a4Width / imgWidth;
                int scaledImgHeight = (int) (imgHeight * scaleFactor);

                int y = 0;
                int pageNum = 1;

                while (y < scaledImgHeight) {
                    // Calculate crop height for the current page
                    int cropHeight = Math.min((int) a4Height, scaledImgHeight - y);

                    // Ensure we don't cut text awkwardly
                    cropHeight = findOptimalCut(fullImage, (int) (y / scaleFactor), cropHeight);

                    // Extract the sub-image
                    BufferedImage subImage = fullImage.getSubimage(0, (int) (y / scaleFactor), imgWidth, (int) (cropHeight / scaleFactor));
                    y += cropHeight;

                    // Save the sub-image temporarily
                    File tempImageFile = new File("temp_cv_page_" + pageNum + ".png");
                    ImageIO.write(subImage, "png", tempImageFile);

                    // Add a new A4 page
                    pdfDoc.addNewPage(PageSize.A4);

                    // Load the image
                    com.itextpdf.layout.element.Image img = new com.itextpdf.layout.element.Image(
                            com.itextpdf.io.image.ImageDataFactory.create(tempImageFile.getAbsolutePath())
                    );

                    // Scale the image to fit A4 width, ensuring last page is correctly positioned

                    img.scaleToFit(a4Width, cropHeight);

                    // 🔹 Correct Last Page Position 🔹
                    float yPos;
                    if (cropHeight < a4Height) {
                        yPos = a4Height - cropHeight; // Align to top without extra space
                    } else {
                        yPos = 0; // Normal positioning for full pages
                    }

                    img.setFixedPosition(pageNum, 0, yPos);
                    document.add(img);

                    pageNum++; // Move to the next page
                }
                appendCertificateImagesToPdf(pdfDoc, document);
                document.close();
                System.out.println("✅ PDF exported successfully: " + file.getAbsolutePath());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private int findOptimalCut(BufferedImage image, int startY, int maxHeight) {
        int scanHeight = Math.min(maxHeight, image.getHeight() - startY);

        // Look for a natural text break (avoid cutting text mid-line)
        for (int y = startY + scanHeight - 80; y < startY + scanHeight; y++) {
            if (y >= image.getHeight()) break; // Prevent overflow

            for (int x = 0; x < image.getWidth(); x++) {
                if (isNonEmptyPixel(image, x, y)) {
                    return scanHeight; // Text found, use full A4 height
                }
            }
        }

        // If no text is found in the bottom 80 pixels, cut slightly higher
        return scanHeight - 80;
    }
    private boolean isNonEmptyPixel(BufferedImage image, int x, int y) {
        if (y >= image.getHeight() || x >= image.getWidth()) return false;

        int pixel = image.getRGB(x, y);
        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = pixel & 0xff;

        // Adjust white threshold if needed (more robust detection)
        return alpha > 200 && (red < 235 || green < 235 || blue < 235);
    }

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
    public void exportHybridCVPreview() {
        try {
            // Step 1: Take snapshot of VBox
            WritableImage snapshot = cvPreviewContainer.snapshot(null, null);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);
            File imageFile = new File("cv_preview.png");
            ImageIO.write(bufferedImage, "png", imageFile);

            // Step 2: Create a new PDF
            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Step 3: Add Image to PDF
            PDImageXObject pdImage = PDImageXObject.createFromFile("cv_preview.png", document);
            contentStream.drawImage(pdImage, 50, 300, 500, 400); // Adjust position & size

            // Step 4: Add Selectable Text Overlay
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Curriculum Vitae - " + titleField.getText());
            contentStream.endText();

            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 720);
            contentStream.showText("Name: " + "DANETTE EASTWOOD");
            contentStream.endText();


            contentStream.beginText();
            contentStream.newLineAtOffset(50, 700);
            contentStream.showText("Summary: " + introductionField.getText());
            contentStream.endText();

            // Step 5: Add Experiences as Text
            int y = 670;
            for (Experience exp : experiences) {
                contentStream.beginText();
                contentStream.newLineAtOffset(50, y);
                contentStream.showText(exp.getType() + " - " + exp.getPosition() + " at " + exp.getLocationName());
                contentStream.endText();
                y -= 20; // Move down
            }

            contentStream.close();
            document.save("cv_hybrid.pdf");
            document.close();
            System.out.println("✅ CV saved as Hybrid PDF!");

        } catch (IOException e) {
            e.printStackTrace();
        }
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



    private void fetchSkills(String inputText) {
        // API endpoint and token
        String apiUrl = "https://api-inference.huggingface.co/models/algiraldohe/lm-ner-linkedin-skills-recognition";
        String apiToken = "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"; // Replace with your actual token

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiToken);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            System.out.println(inputText);
            // Use the inputText variable instead of a constant string
            String jsonInputString = "{\"inputs\": \"" + inputText + "\"}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            JSONArray jsonResponse = new JSONArray(response.toString());
            System.out.print(jsonResponse);

            // Update available skills on the JavaFX thread
            Platform.runLater(() -> {
                availableSkills.clear();
                for (int i = 0; i < jsonResponse.length(); i++) {
                    JSONObject entity = jsonResponse.getJSONObject(i);
                    String entityType = entity.getString("entity_group");
                    String skillName = entity.getString("word");
                    if ( skillName.split(" ").length <= 2) {
                        availableSkills.add(skillName);
                    }
                }
                // Update the ComboBox items with new suggestions
                skillComboBox.setItems(availableSkills);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addSkillTag(String skill) {
        Label skillLabel = new Label(skill);
        skillLabel.setStyle("-fx-text-fill: #3B261D; -fx-font-size: 14px;");

        Button removeButton = new Button("x");
        removeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-font-size: 12px;");
        removeButton.setOnAction(e -> {
            selectedSkillsFlow.getChildren().remove(removeButton.getParent());
            selectedSkills.remove(skill);
        });

        HBox tag = new HBox(skillLabel, removeButton);
        tag.setAlignment(Pos.CENTER);
        tag.setSpacing(5);
        tag.setStyle("-fx-background-color: #E0E0E0; -fx-padding: 5; -fx-border-radius: 5; -fx-background-radius: 5;");

        selectedSkillsFlow.getChildren().add(tag);
    }
    private String getSelectedSkillsString() {
        StringBuilder sb = new StringBuilder();
        for (String skill : selectedSkills) {
            sb.append(skill).append("-");
        }
        return sb.toString();
    }




    @FXML
    private void populateLanguagesFromAPI() {
        try {

            URL url = new URL("https://restcountries.com/v3.1/all");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
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
        updateLanguagesSection();
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
        // Create the main container for this language entry
        HBox languageBox = new HBox(10);
        languageBox.setAlignment(Pos.CENTER_LEFT);
        languageBox.setStyle(
                "-fx-background-color: #F5EDE1; " +
                        "-fx-border-color: #3B261D; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10;"
        );

        // Create the label for the language name and level
        Label languageLabel = new Label(language.getName() + " - " + language.getLevel());
        languageLabel.setStyle("-fx-text-fill: #3B261D; -fx-font-size: 14px; -fx-font-weight: bold;");

        // Create a container for the proficiency dots with fixed minimum width for each dot
        HBox dotContainer = new HBox(5);
        dotContainer.setAlignment(Pos.CENTER_LEFT);
        for (int i = 1; i <= 5; i++) {
            Label dot = new Label(i <= convertLevelToDots(language.getLevel()) ? "⚫" : "⚪");
            dot.setStyle("-fx-font-size: 16px; -fx-text-fill: gray; -fx-min-width: 20px;");
            dotContainer.getChildren().add(dot);
        }

        // Create a spacer region to push the action buttons to the far right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Create the Edit and Delete buttons, all using the brown theme (#3B261D)
        Button editButton = createStyledButton("Edit", "#3B261D", "white");
        editButton.setOnAction(e -> editLanguage(language, languageBox));

        Button deleteButton = createStyledButton("Delete", "#3B261D", "white");
        deleteButton.setOnAction(e -> deleteLanguage(language, languageBox));

        // Add all components to the main languageBox
        languageBox.getChildren().addAll(languageLabel, dotContainer, spacer, editButton, deleteButton);

        // Add this languageBox to the container that holds all language entries
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
        updateLanguagesSection();

        // Restore the deleted language back to the dropdown
        allLanguages.add(language.getName());
        FXCollections.sort(allLanguages);
        languagesDropdown.setItems(allLanguages);
    }




    private void initializeSkillSearch() {
        // Set the ComboBox to be editable
        skillComboBox.setEditable(true);

        // Listen to changes in the editor's text property and debounce for 500ms
        skillComboBox.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (skillSearchDelayTimeline != null) {
                skillSearchDelayTimeline.stop();
            }
            // Wait 500ms after typing stops
            skillSearchDelayTimeline = new Timeline(new KeyFrame(Duration.millis(1500), event -> {
                // Call fetchSkills with the current input
                fetchSkills(newText);
            }));
            skillSearchDelayTimeline.play();
        });

        // Optionally, you can add an action listener for when a skill is selected from suggestions:
        skillComboBox.setOnAction(event -> {
            String skill = skillComboBox.getValue();
            if (skill != null && !skill.isBlank() && !selectedSkills.contains(skill)) {
                addSkillTag(skill);
                selectedSkills.add(skill);
                // Clear the editor for a new search
                skillComboBox.getEditor().clear();
            }
        });
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
        introductionField.textProperty().addListener((obs, oldText, newText) -> updateSummarySection());
        selectedSkills.addListener((ListChangeListener<String>) change -> updateSkillsSection());


        // ✅ Ensure languages are fetched from API
        populateLanguagesFromAPI(); // RESTORED API CALL

        // ✅ Ensure skills dropdown is populated
        initializeSkillSearch();

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
        // 🔹 HEADER (Always Present)
        updateHeaderSection();
        titleField.textProperty().addListener((obs, oldText, newText) -> updateHeaderSection());

        fileTypeDropdown.valueProperty().addListener((obs, oldVal, newVal) -> {
            // If the selected file type contains "Image", hide the certificate checkbox.
            if (newVal != null && newVal.contains("Image")) {
                addCertficatesCheckbox.setVisible(false);
            } else {
                addCertficatesCheckbox.setVisible(true);
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
        populateFileTypeDropdown();
    }
    private void populateFileTypeDropdown() {
        ObservableList<String> fileTypes = FXCollections.observableArrayList(
                "📄 Flattened PDF ",
                "🖼️ Image (PNG)",
                "📄 PDF A4 (BETA)"
        );
        fileTypeDropdown.setItems(fileTypes);
        fileTypeDropdown.setValue("📄 Flattened PDF"); // Default
    }


    @FXML
    private void hideDownloadModal(MouseEvent event) {
        if (!downloadModal.isVisible()) return;
        downloadModalOverlay.setVisible(false);
    }

    @FXML
    private void hideDownloadModalCancel() {
        downloadModalOverlay.setVisible(false);
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
    private void addExperienceBox(Experience experience) {
        // Main container for an experience entry
        HBox experienceBox = new HBox(10);
        experienceBox.setAlignment(Pos.CENTER_LEFT);
        experienceBox.setStyle(
                "-fx-background-color: #F5EDE1; " +
                        "-fx-border-color: #3B261D; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10;"
        );

        // Left side: Experience details (type, position, location, dates, and description)
        VBox detailsBox = new VBox(5);
        detailsBox.setAlignment(Pos.CENTER_LEFT);

        // Experience header with type, position and location
        Label headerLabel = new Label(experience.getType() + ": " + experience.getPosition() + " at " + experience.getLocationName());
        headerLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #3B261D;");

        // Dates and description in smaller font
        Label datesLabel = new Label("(" + experience.getStartDate() + " to " + experience.getEndDate() + ")");
        datesLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #3B261D;");
        Label descLabel = new Label("Description: " + experience.getDescription());
        descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #3B261D;");
        descLabel.setWrapText(true);

        detailsBox.getChildren().addAll(headerLabel, datesLabel, descLabel);

        // Spacer to push buttons to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Create Edit and Delete buttons with the brown theme
        Button editButton = createStyledButton("Edit", "#3B261D", "white");
        editButton.setOnAction(e -> {
            editingExperience = experience;
            typeDropdown.setValue(experience.getType());
            positionField.setText(experience.getPosition());
            locationField.setText(experience.getLocationName());
            descriptionField.setText(experience.getDescription());
            startDatePicker.setValue(LocalDate.parse(experience.getStartDate().split(" ")[0]));
            endDatePicker.setValue(LocalDate.parse(experience.getEndDate().split(" ")[0]));
            showExperienceModal();
        });

        Button deleteButton = createStyledButton("Delete", "#3B261D", "white");
        deleteButton.setOnAction(e -> {
            experiences.remove(experience);
            experienceContainer.getChildren().remove(experienceBox);
            updateExperienceSection();
            updateEducationSection();
        });

        // Assemble the experience box
        experienceBox.getChildren().addAll(detailsBox, spacer, editButton, deleteButton);
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
        updateEducationSection();
        updateExperienceSection();
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
            // New validation: ensure at least one skill tag is selected
            if (selectedSkills.isEmpty()) {
                skillsErrorLabel.setText("Please add at least one skill.");
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
            // Concatenate selected skills into a single string, e.g., "Java-Python-C++-"
            String skills = getSelectedSkillsString();

            // ✅ Check if updating an existing CV
            if (editingCV != null) {
                // UPDATE EXISTING CV
                editingCV.setUserTitle(title);
                editingCV.setIntroduction(introduction);
                editingCV.setSkills(skills);
                cvService.update(editingCV);

                // Handle Experiences
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
                        experienceService.delete(originalExp.getIdExperience());
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

                // Handle Certificates
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
                        certificateService.delete(originalCert.getIdCertificate());
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

                // Handle Languages
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
                        languageService.delete(originalLang.getIdLanguage());
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
                // CREATE NEW CV
                CV cv = new CV(1, title, introduction, skills);
                cvService.add(cv);

                // Retrieve latest CV ID
                int latestCvId = cvService.getLatestCVId();
                if (latestCvId == -1) {
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
            }

            // Clear form after submission
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



    @FXML
    private void updateCVPreview() {
        // Clear previous content before updating
        System.out.println("Updating CV Preview...");
        System.out.println("Header: CV Title = '" + titleField.getText().trim() + "'");
        System.out.println("Summary: '" + introductionField.getText().trim() + "'");
        System.out.println("Experiences: " + experiences.size() + " items");
        updateHeaderSection();
        updateSummarySection();
        updateExperienceSection();
        updateEducationSection();
        updateSkillsSection();
        updateLanguagesSection();
        updateCertificatesSection();
    }
    private void appendCertificateImagesToPdf(PdfDocument pdfDoc, Document document) throws IOException {
        if (addCertficatesCheckbox.isSelected()) {
            for (Certificate cert : certificates) {
                String media = cert.getMedia().toLowerCase();
                if (media.endsWith(".png") || media.endsWith(".jpg") || media.endsWith(".jpeg")) {
                    File certFile = new File("view/Certificates/" + cert.getMedia());
                    System.out.println("Adding certificate image: " + cert.getMedia());
                    if (certFile.exists()) {
                        // Read the image to obtain its dimensions.
                        BufferedImage bufferedImage = ImageIO.read(certFile);
                        float imgWidth = bufferedImage.getWidth();   // in pixels (assuming 1 pixel = 1 point)
                        float imgHeight = bufferedImage.getHeight(); // in pixels (assuming 1 pixel = 1 point)

                        // Create a custom page size matching the certificate image.
                        PageSize certPageSize = new PageSize(imgWidth, imgHeight);
                        pdfDoc.addNewPage(certPageSize);

                        // Create the iText image from the certificate file.
                        com.itextpdf.layout.element.Image certImg = new com.itextpdf.layout.element.Image(
                                com.itextpdf.io.image.ImageDataFactory.create(certFile.getAbsolutePath())
                        );

                        // Scale the image to exactly fill the page.
                        certImg.scaleToFit(certPageSize.getWidth(), certPageSize.getHeight());
                        certImg.setFixedPosition(pdfDoc.getNumberOfPages(), 0, 0);
                        document.add(certImg);
                    }
                }
            }
        }
    }



    @FXML
    private void exportToPDF2() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                double scaleFactor = 3.0;
                SnapshotParameters params = new SnapshotParameters();
                params.setTransform(javafx.scene.transform.Transform.scale(scaleFactor, scaleFactor));

                WritableImage snapshot = new WritableImage(
                        (int) (cvPreviewContainer.getWidth() * scaleFactor),
                        (int) (cvPreviewContainer.getHeight() * scaleFactor)
                );
                cvPreviewContainer.snapshot(params, snapshot);

                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

                File imageFile = new File("cv_preview_high_res.png");
                ImageIO.write(bufferedImage, "png", imageFile);

                PdfWriter writer = new PdfWriter(new FileOutputStream(file));
                PdfDocument pdfDoc = new PdfDocument(writer);
                Document document = new Document(pdfDoc);

                // Use the image's dimensions to create a custom page size for the main CV page
                float imageWidth = bufferedImage.getWidth();
                float imageHeight = bufferedImage.getHeight();
                PageSize customPageSize = new PageSize(imageWidth, imageHeight);
                pdfDoc.setDefaultPageSize(customPageSize);

                com.itextpdf.layout.element.Image img = new com.itextpdf.layout.element.Image(
                        com.itextpdf.io.image.ImageDataFactory.create(imageFile.getAbsolutePath()));
                img.scaleToFit(customPageSize.getWidth(), customPageSize.getHeight());
                img.setFixedPosition(0, 0);
                document.add(img);

                // Append certificate images on A4 pages by passing A4 dimensions.
                appendCertificateImagesToPdf(pdfDoc, document);

                document.close();

                System.out.println("✅ PDF saved at: " + file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateHeaderSection() {
        // Clear the header container first
        headerContainer.getChildren().clear();

        // Create a new header VBox
        VBox header = new VBox();
        header.setAlignment(Pos.CENTER);
        header.setSpacing(5);
        header.setMaxWidth(Double.MAX_VALUE);

        // Name is always displayed
        Label nameLabel = createStyledLabel(LoggedUser.getInstance().getName(), "header");
        nameLabel.setAlignment(Pos.CENTER);
        header.getChildren().add(nameLabel);

        // Only display the role label if the CV Title field is not empty
        String cvTitle = titleField.getText().trim();
        if (!cvTitle.isEmpty()) {
            Label roleLabel = createStyledLabel(cvTitle, "sub-header");
            roleLabel.setAlignment(Pos.CENTER);
            header.getChildren().add(roleLabel);
        }

        // Contact information is always displayed
       /* Label contactLabel = createStyledLabel( LoggedUser.getInstance().getEmail() +"•" +  profile.getPhone() +  "•" + profile.getAddress() + "•" + profile.getCurrent_occupation(), "contact");
        contactLabel.setAlignment(Pos.CENTER);
        header.getChildren().add(contactLabel);*/

        // Add the header to the dedicated header container in the preview
        headerContainer.getChildren().add(header);
    }

    private void updateSummarySection() {
        summaryContainer.getChildren().clear();
        String introText = introductionField.getText().trim();
        if (!introText.isEmpty()) {
            summaryContainer.getChildren().add(createSectionLabel("Summary"));
            summaryContainer.getChildren().add(createParagraphLabel(introText));
        }
    }

    private void updateExperienceSection() {
        // Clear the dedicated experience preview container first
        experiencePreviewContainer.getChildren().clear();

        boolean hasInternship = false;
        VBox experienceSection = new VBox();
        experienceSection.setSpacing(10);

        for (Experience exp : experiences) {
            if (exp.getType().equalsIgnoreCase("Internship")) {
                hasInternship = true;
                VBox experienceBox = new VBox();
                experienceBox.setSpacing(5);

                // Create an HBox to align Company Name (left) and Date (right)
                HBox companyDateBox = new HBox();
                companyDateBox.setSpacing(10);
                companyDateBox.setMaxWidth(Double.MAX_VALUE);

                Label company = createStyledLabel(exp.getLocationName(), "bold");
                Label dates = createStyledLabel(exp.getStartDate() + " - " + exp.getEndDate(), "small");
                HBox.setHgrow(company, Priority.ALWAYS);  // Pushes date to the right

                companyDateBox.getChildren().addAll(company, dates);

                // Job Title & Description
                Label title = createStyledLabel(exp.getPosition(), "position_experience");
                Label desc = createParagraphLabel(exp.getDescription());

                experienceBox.getChildren().addAll(companyDateBox, title, desc);
                experienceSection.getChildren().add(experienceBox);
            }
        }
        if (hasInternship) {
            // Add the section header and the built section to the dedicated container
            experiencePreviewContainer.getChildren().add(createSectionLabel("Experience"));
            experiencePreviewContainer.getChildren().add(experienceSection);
        }
    }


    private void updateEducationSection() {
        // Clear the dedicated education preview container first
        educationPreviewContainer.getChildren().clear();

        boolean hasAcademic = false;
        VBox educationSection = new VBox();
        educationSection.setSpacing(10);

        for (Experience exp : experiences) {
            if (exp.getType().equalsIgnoreCase("Academic")) {
                hasAcademic = true;
                VBox educationBox = new VBox();
                educationBox.setSpacing(5);

                // Create an HBox to align School Name (left) and Date (right)
                HBox schoolDateBox = new HBox();
                schoolDateBox.setSpacing(10);
                schoolDateBox.setMaxWidth(Double.MAX_VALUE);

                Label school = createStyledLabel(exp.getLocationName(), "bold");
                Label dates = createStyledLabel(exp.getStartDate() + " - " + exp.getEndDate(), "small");
                HBox.setHgrow(school, Priority.ALWAYS);  // Pushes date to the right

                schoolDateBox.getChildren().addAll(school, dates);

                // Degree Title & Description
                Label degree = createStyledLabel(exp.getPosition(), "position_experience");
                Label desc = createParagraphLabel(exp.getDescription());

                educationBox.getChildren().addAll(schoolDateBox, degree, desc);
                educationSection.getChildren().add(educationBox);
            }
        }
        if (hasAcademic) {
            // Add the header and the built section to the dedicated container
            educationPreviewContainer.getChildren().add(createSectionLabel("Education"));
            educationPreviewContainer.getChildren().add(educationSection);
        }
    }


    private void updateSkillsSection() {
        // Clear the dedicated skills container first
        skillsPreviewContainer.getChildren().clear();

        if (!selectedSkills.isEmpty()) {
            skillsPreviewContainer.getChildren().add(createSectionLabel("Skills"));
            StringBuilder sb = new StringBuilder();
            for (String skill : selectedSkills) {
                sb.append(skill).append(", ");
            }
            // Remove the trailing comma and space
            if (sb.length() >= 2) {
                sb.setLength(sb.length() - 2);
            }
            skillsPreviewContainer.getChildren().add(createParagraphLabel(sb.toString()));
        }
    }


    private void updateLanguagesSection() {
        // Clear the dedicated languages container first
        languagesPreviewContainer.getChildren().clear();

        if (!languages.isEmpty()) {
            languagesPreviewContainer.getChildren().add(createSectionLabel("Languages"));
            for (Language lang : languages) {
                languagesPreviewContainer.getChildren().add(
                        createStyledLabel("• " + lang.getName() + " - " + lang.getLevel(), "small")
                );
            }
        }
    }

    private void updateCertificatesSection() {
        // Clear the dedicated certificates container first
        certificatesPreviewContainer.getChildren().clear();

        boolean hasCertificates = false;
        VBox certificateSection = new VBox();
        certificateSection.setSpacing(10);

        for (Certificate cert : certificates) {
            hasCertificates = true;
            VBox certificateBox = new VBox();
            certificateBox.setSpacing(5);

            // Create an HBox to align Issuing Organization (left) and Date (right)
            HBox certDateBox = new HBox();
            certDateBox.setSpacing(10);
            certDateBox.setMaxWidth(Double.MAX_VALUE);

            Label organization = createStyledLabel(cert.getAssociation(), "bold");
            Label issueDate = createStyledLabel(cert.getDate().toString(), "small");
            HBox.setHgrow(organization, Priority.ALWAYS);  // Pushes date to the right

            certDateBox.getChildren().addAll(organization, issueDate);

            // Certificate Name & Description
            Label certName = createStyledLabel(cert.getTitle(), "position_experience");
            Label certDesc = createParagraphLabel(cert.getDescription());

            certificateBox.getChildren().addAll(certDateBox, certName, certDesc);
            certificateSection.getChildren().add(certificateBox);
        }

        if (hasCertificates) {
            certificatesPreviewContainer.getChildren().add(createSectionLabel("Certificates"));
            certificatesPreviewContainer.getChildren().add(certificateSection);
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



    private Label createParagraphLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true); // ✅ Ensure wrapping
        label.setMaxHeight(Double.MAX_VALUE); // ✅ Allow expansion in VBox
        label.setMinHeight(Region.USE_PREF_SIZE); // ✅ Prevent unnecessary shrinking
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");

        VBox.setVgrow(label, Priority.ALWAYS); // ✅ Ensure label grows inside VBox
        return label;
    }

    private Label createStyledLabel(String text, String type) {
        Label label = new Label(text);
        label.setWrapText(true); // ✅ Enable text wrapping
        label.setMaxHeight(Double.MAX_VALUE); // ✅ Allow expansion in VBox
        label.setMinHeight(Region.USE_PREF_SIZE); // ✅ Prevent unwanted shrinkage
        VBox.setVgrow(label, Priority.ALWAYS); // ✅ Ensure proper layout

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



    private void addCertificateBox(Certificate certificate) {
        // Main container for a certificate entry
        HBox certificateBox = new HBox(10);
        certificateBox.setAlignment(Pos.CENTER_LEFT);
        certificateBox.setStyle(
                "-fx-background-color: #F5EDE1; " +
                        "-fx-border-color: #3B261D; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10;"
        );

        // Left side: Certificate details
        VBox detailsBox = new VBox(5);
        detailsBox.setAlignment(Pos.CENTER_LEFT);

        // Certificate name in bold
        Label nameLabel = new Label(certificate.getTitle());
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #3B261D;");

        // Issuing organization and date
        Label orgLabel = new Label("Issued by: " + certificate.getAssociation());
        orgLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #3B261D;");
        Label dateLabel = new Label("Date: " + certificate.getDate().toString());
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #3B261D;");

        // Description
        Label descLabel = new Label("Description: " + certificate.getDescription());
        descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #3B261D;");
        descLabel.setWrapText(true);

        detailsBox.getChildren().addAll(nameLabel, orgLabel, dateLabel, descLabel);

        // Spacer to push buttons to the far right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Edit and Delete buttons using the brown theme
        Button editButton = createStyledButton("Edit", "#3B261D", "white");
        editButton.setOnAction(e -> {
            editingCertificate = certificate;
            certificateNameField.setText(certificate.getTitle());
            certificateAssociationField.setText(certificate.getAssociation());
            certificateDescriptionField.setText(certificate.getDescription());
            certificateDatePicker.setValue(certificate.getDate().toLocalDate());
            certificateMediaLabel.setText(certificate.getMedia());
            showCertificateModal();
        });

        Button deleteButton = createStyledButton("Delete", "#3B261D", "white");
        deleteButton.setOnAction(e -> {
            certificates.remove(certificate);
            certificateContainer.getChildren().remove(certificateBox);
            updateCertificatesSection();
        });

        // Assemble certificate box
        certificateBox.getChildren().addAll(detailsBox, spacer, editButton, deleteButton);
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
        updateCertificatesSection();
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
        editingCV = cvService.getById(cvId); // Fetch CV from database
        if (editingCV != null) {
            // Fill in basic details
            titleField.setText(editingCV.getUserTitle());
            introductionField.setText(editingCV.getIntroduction());

            // Instead of setting the skillsDropdown value, parse the stored skills string
            String skillsString = editingCV.getSkills(); // Expected format: "skill1-skill2-skill3-"
            if (skillsString != null && !skillsString.trim().isEmpty()) {
                // Clear previous skills
                selectedSkills.clear();
                selectedSkillsFlow.getChildren().clear();
                // Split by hyphen and add non-empty tokens to selectedSkills
                String[] skillsArr = skillsString.split("-");
                for (String skill : skillsArr) {
                    if (!skill.trim().isEmpty()) {
                        selectedSkills.add(skill.trim());
                        addSkillTag(skill.trim());
                    }
                }
            }

            // Run grammar check after a delay
            new Timeline(new KeyFrame(Duration.millis(1000), event -> {
                disableGrammarCheck = false;
                checkGrammarForField(titleField);
                checkGrammarForField(introductionField);
            })).play();

            // Clear old data before inserting new ones
            experienceContainer.getChildren().clear();
            certificateContainer.getChildren().clear();
            languageContainer.getChildren().clear();

            // Load Experiences
            experiences = editingCV.getExperiences();
            if (experiences != null) {
                for (Experience experience : experiences) {
                    addExperienceBox(experience);
                }
            }

            // Load Certificates
            certificates = editingCV.getCertificates();
            if (certificates != null) {
                for (Certificate certificate : certificates) {
                    addCertificateBox(certificate);
                }
            }

            // Load Languages
            languages = editingCV.getLanguageList();
            if (languages != null) {
                for (Language language : languages) {
                    addLanguageBox(language);
                }
            }

            Platform.runLater(() -> updateCVPreview());
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