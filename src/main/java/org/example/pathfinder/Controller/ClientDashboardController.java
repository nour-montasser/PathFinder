package org.example.pathfinder.Controller;
import javafx.collections.FXCollections;
import javafx.scene.layout.HBox;
import javafx.application.Platform;
import javafx.concurrent.Worker;

import netscape.javascript.JSObject;
import org.json.JSONObject;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.pathfinder.Model.ServiceOffre;
import org.example.pathfinder.Service.ServiceOffreService;
import org.example.pathfinder.Model.ApplicationService;
import org.example.pathfinder.Model.ServiceOffre;
import org.example.pathfinder.Service.ApplicationServiceService;
import org.example.pathfinder.Service.ServiceOffreService;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

import java.io.IOException;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.stream.Collectors;

public class ClientDashboardController {

    @FXML
    private ImageView searchIcon;

    @FXML
    private ImageView logoImage;  // Make sure the fx:id matches the FXML file

    @FXML
    private ComboBox<String> skillsDropdown; // Dropdown for filtering services by skill

    @FXML
    private ComboBox<String> currencySelector; // Dropdown for selecting currency

    @FXML
    private GridPane servicesGrid; // Grid layout for services

    @FXML
    private TextField searchField; // Search bar

    @FXML
    private ComboBox<String> sortDropdown; // Sorting dropdown

    private final ServiceOffreService serviceOffreService = new ServiceOffreService();
    private List<ServiceOffre> allServices; // Store all services initially
    private Button submitButton; // ✅ Submit button reference


    private final ApplicationServiceService applicationServiceService = new ApplicationServiceService();

    private static final String API_URL = "https://v6.exchangerate-api.com/v6/01b62ddd85a1a7a3b14cb327/latest/USD";
    private static Map<String, Double> exchangeRates = new HashMap<>();
    private Map<Integer, Double> originalPrices = new HashMap<>(); // Store original prices by service ID

    private static final String RECAPTCHA_SITE_KEY = "6LfKpeYqAAAAAF2Sdz5mXXUp4caTvCgWoICZBw7G";
    private static final String RECAPTCHA_SECRET_KEY = "6LfKpeYqAAAAAHGb5YEpRUwnxz68SndfTR0gJGHL";




    @FXML
    public void initialize() {
        loadServices();

        // ✅ Populate skills dropdown from all services
        List<String> uniqueSkills = allServices.stream()
                .flatMap(service -> List.of(service.getSkills().split(", ")).stream()) // Split and flatten
                .distinct()
                .sorted()
                .toList();

        skillsDropdown.setItems(FXCollections.observableArrayList(uniqueSkills));

        // ✅ Add an event listener to trigger search when a skill is selected
        skillsDropdown.setOnAction(event -> {
            String selectedSkill = skillsDropdown.getValue();
            if (selectedSkill != null && !selectedSkill.isEmpty()) {
                filterServicesBySkill(selectedSkill);
            }
        });


        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> onSearchTextChanged(newVal));
        }

        setupSortingDropdown();

        // ✅ Initialize Currency Selector
        currencySelector.getItems().addAll("USD", "EUR", "TND", "GBP", "CAD", "AUD", "JPY");
        currencySelector.setValue("USD"); // Default to USD
        currencySelector.setOnAction(event -> updatePrices());

        fetchExchangeRates(); // ✅ Fetch exchange rates at startup

        populateSkillsDropdown(); // ✅ Fetch skills and populate dropdown
        loadLogo();
        loadSearchIcon();


    }

    private void loadLogo() {
        try {
            // Load image dynamically from resources
            Image image = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/org/example/pathfinder/view/Sources/pathfinder_logo.png")
            ));

            logoImage.setImage(image);

            // Set larger size while maintaining proportions
            logoImage.setFitWidth(140);  // Adjust width
            logoImage.setPreserveRatio(true);
            logoImage.setSmooth(true);

        } catch (NullPointerException e) {
            System.err.println("❌ Error: Logo image not found! Check file path.");
        }
    }
    private void loadSearchIcon() {
        try {
            Image searchImage = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/org/example/pathfinder/view/Sources/compass.png")
            ));
            searchIcon.setImage(searchImage);

            // Set icon size
            searchIcon.setFitWidth(20);
            searchIcon.setFitHeight(20);
            searchIcon.setPreserveRatio(true);
            searchIcon.setSmooth(true);
        } catch (NullPointerException e) {
            System.err.println("❌ Error: Search icon not found! Check file path.");
        }
    }


    private void setupCurrencySelector() {
        currencySelector.getItems().addAll("USD", "EUR", "TND", "GBP", "CAD", "AUD", "JPY");
        currencySelector.setValue("USD"); // Default currency
        currencySelector.setOnAction(event -> updatePrices());
    }

    private void loadServices() {
        allServices = serviceOffreService.getAll();

        // ✅ Store the original price for each service
        for (ServiceOffre service : allServices) {
            originalPrices.put(service.getId_service(), service.getPrice());
        }
        displayServices(allServices);
    }

    private void displayServices(List<ServiceOffre> services) {
        if (services == null || services.isEmpty()) {
            System.out.println("❌ No services to display!");
            return;
        }
        System.out.println("✅ Displaying " + services.size() + " services...");
        servicesGrid.getChildren().clear();
        int column = 0, row = 0;

        for (ServiceOffre service : services) {
            StackPane serviceCard = createServiceCard(service);
            servicesGrid.add(serviceCard, column++, row);

            if (column == 2) { // Show only 2 services per row
                column = 0;
                row++;
            }
        }
    }

    private StackPane createServiceCard(ServiceOffre service) {

        if (service == null) {
            System.out.println("❌ Service is NULL in createServiceCard!");
            return new StackPane();
        }
        System.out.println("✅ Creating service card for: " + service.getTitle());
        StackPane card = new StackPane();
        card.setStyle("-fx-background-color: white; -fx-border-radius: 10px; -fx-padding: 15px;");

        VBox content = new VBox(10);

        HBox priceAndExperience = createPriceAndExperience(service);

        // ✅ Format Duration (Ensure it's not null)
        String durationText = (service.getDuration() != null && !service.getDuration().isEmpty()) ? service.getDuration() : "No duration";

        Label durationLabel = new Label(" " + durationText);
        durationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #444;");

        // ✅ Make "Posted On" smaller and less visible
        Label postedOnLabel = new Label("Posted on: " + service.getFormattedDate());
        postedOnLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #A9A9A9; -fx-padding: 5px 0;");

        // ✅ Arrange Posted On & Duration in the same row
        HBox dateInfo = new HBox(10); // Horizontal box with spacing
        dateInfo.getChildren().addAll(postedOnLabel, durationLabel);

        content.getChildren().addAll(
                createLabel(service.getTitle(), 20, "black", true),
                priceAndExperience,
                dateInfo, // ✅ Display both "Posted On" & Duration
                createLabel(shortenDescription(service.getDescription()), 14, "#666", false),
                createApplyButton(service)
        );

        card.getChildren().add(content);
        return card;
    }

    private HBox createPriceAndExperience(ServiceOffre service) {
        HBox hbox = new HBox(20);

        Label priceLabel = new Label("" + service.getPrice());
        priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #008000;");

        Label priceTypeLabel = new Label("Starting-price");
        priceTypeLabel.setStyle("-fx-text-fill: #A9A9A9; -fx-font-size: 12px;");

        Label experienceLabel = new Label(service.getExperience_level());
        experienceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");

        Label experienceTypeLabel = new Label("Experience level");
        experienceTypeLabel.setStyle("-fx-text-fill: #A9A9A9; -fx-font-size: 12px;");

        VBox priceBox = new VBox(priceLabel, priceTypeLabel);
        VBox experienceBox = new VBox(experienceLabel, experienceTypeLabel);
        hbox.getChildren().addAll(priceBox, experienceBox);
        return hbox;
    }


    private String shortenDescription(String description) {
        if (description.length() > 100) {
            return description.substring(0, 97) + "...";
        }
        return description;
    }

    private Label createLabel(String text, int fontSize, String color, boolean bold) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + color + "; " +
                "-fx-font-size: " + fontSize + "px; " +
                (bold ? "-fx-font-weight: bold;" : ""));
        return label;
    }

    private Button createApplyButton(ServiceOffre service) {
        Button applyButton = new Button("Apply");
        applyButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px;");

        applyButton.setOnAction(event -> openApplicationModal(service));
        return applyButton;

    }

    private void openApplicationModal(ServiceOffre service) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Apply for Service");

        VBox dialogVBox = new VBox(10);
        dialogVBox.setStyle("-fx-padding: 20px;");

        Label infoLabel = new Label("Enter your offered price:");
        TextField priceField = new TextField();
        priceField.setPromptText("Enter price...");

        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        submitButton.setOnAction(event -> handleSubmitApplication(service, priceField.getText()));

        dialogVBox.getChildren().addAll(infoLabel, priceField, submitButton);
        Scene dialogScene = new Scene(dialogVBox, 400, 200);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    private void handleSubmitApplication(ServiceOffre service, String priceText) {
        if (priceText.isEmpty()) {
            showErrorAlert("Error", "Please enter a valid price!");
            return;
        }

        double offeredPrice;
        try {
            offeredPrice = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            showErrorAlert("Error", "Invalid price format! Enter a numeric value.");
            return;
        }

        if (offeredPrice < service.getPrice()) {
            showErrorAlert("Error", "Your offered price must be at least $" + service.getPrice());
            return;
        }
        if (serviceOffreService.getById(service.getId_service()) == null) {
            showErrorAlert("Error", "This service no longer exists!");
            return;
        }
        ApplicationService application = new ApplicationService(
                0, offeredPrice, 0, "Pending", service.getId_service(), 0 // review (default empty)
        );

        try {
            applicationServiceService.add(application);
            showSuccessAlert("Success", "Your application has been submitted!");
        } catch (Exception e) {
            showErrorAlert("Database Error", "Failed to submit application: " + e.getMessage());
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private MenuButton createMenuButton(ServiceOffre service) {
        MenuButton menuButton = new MenuButton("⋮");
        menuButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        MenuItem editItem = new MenuItem("Edit");
        MenuItem deleteItem = new MenuItem("Delete");

        menuButton.getItems().addAll(editItem, deleteItem);
        return menuButton;
    }

    private void setupSortingDropdown() {
        if (sortDropdown == null) return;

        sortDropdown.getItems().addAll("Price: Low to High", "Price: High to Low", "Newest First", "Oldest First");
        sortDropdown.setValue("Price: Low to High");
        sortDropdown.setOnAction(event -> sortServices());
    }

    private void sortServices() {
        String selectedSort = sortDropdown.getValue();
        switch (selectedSort) {
            case "Price: Low to High":
                allServices = serviceOffreService.getAllSortedByPrice();
                break;
            case "Price: High to Low":
                allServices = serviceOffreService.getAllSortedByPriceDesc();
                break;
            case "Newest First":
                allServices = serviceOffreService.getAllSortedByDateDesc();
                break;
            case "Oldest First":
                allServices = serviceOffreService.getAllSortedByDate();
                break;
        }
        displayServices(allServices);
    }

    @FXML
    private void onSearchTextChanged(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            displayServices(allServices);
            return;
        }

        List<ServiceOffre> filteredServices = allServices.stream()
                .filter(service -> service.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                        service.getDescription().toLowerCase().contains(searchText.toLowerCase()) ||
                        (service.getSkills() != null && service.getSkills().toLowerCase().contains(searchText.toLowerCase())) // ✅ Ensure skills are checked properly
                )
                .collect(Collectors.toList());

        displayServices(filteredServices);
    }


    private void fetchExchangeRates() {
        try {
            URL url = new URL(API_URL);
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
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject rates = jsonResponse.getJSONObject("conversion_rates");

            for (String currency : rates.keySet()) {
                exchangeRates.put(currency, rates.getDouble(currency));
            }

            System.out.println("✅ Exchange rates updated: " + exchangeRates);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Error fetching exchange rates!");
        }

    }

    private double convertPrice(double price, String targetCurrency) {
        if (!exchangeRates.containsKey(targetCurrency)) {
            System.out.println("⚠ Currency not available: " + targetCurrency);
            return price; // Return original price if currency is missing
        }
        return exchangeRates.get(targetCurrency) * price;
    }

    private void updatePrices() {
        String selectedCurrency = currencySelector.getValue();

        for (ServiceOffre service : allServices) {
            double originalPrice = originalPrices.get(service.getId_service()); // ✅ Always use the original price
            double convertedPrice = convertPrice(originalPrice, selectedCurrency);
            service.setPrice(convertedPrice);
        }

        displayServices(allServices); // Refresh the UI
    }

    private void populateSkillsDropdown() {
        List<ServiceOffre> allServices = serviceOffreService.getAll();
        Set<String> uniqueSkills = new HashSet<>();

        for (ServiceOffre service : allServices) {
            if (service.getSkills() != null && !service.getSkills().isEmpty()) {
                String[] skillsArray = service.getSkills().split(", ");
                uniqueSkills.addAll(Arrays.asList(skillsArray));
            }
        }

        skillsDropdown.getItems().clear();
        skillsDropdown.getItems().addAll(uniqueSkills);
    }

    private void filterServicesBySkill(String skill) {
        if (skill == null || skill.trim().isEmpty()) {
            displayServices(allServices);
            return;
        }

        List<ServiceOffre> filteredServices = allServices.stream()
                .filter(service -> service.getSkills() != null && service.getSkills().toLowerCase().contains(skill.toLowerCase()))
                .collect(Collectors.toList());

        displayServices(filteredServices);
    }



}