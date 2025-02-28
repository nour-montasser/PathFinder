package org.example.pathfinder.Controller;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.json.JSONObject;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import java.io.IOException;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

public class ClientDashboardController {

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

    @FXML
    private WebView hcaptchaWebView;

    private final ApplicationServiceService applicationServiceService = new ApplicationServiceService();

    private static final String API_URL = " https://v6.exchangerate-api.com/v6/01b62ddd85a1a7a3b14cb327/latest/USD";
    private static Map<String, Double> exchangeRates = new HashMap<>();
    private Map<Integer, Double> originalPrices = new HashMap<>(); // Store original prices by service ID

    private static final String HCAPTCHA_SITE_KEY = "51605062-cb86-4442-8f50-44807e842234"; // Replace with your actual hCaptcha site key
    private static final String HCAPTCHA_SECRET_KEY = "ES_3ca1f53f02154f12aac8f49642c189ec"; // Replace with your actual hCaptcha secret key

    @FXML
    public void initialize() {
        loadServices();

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> onSearchTextChanged(newVal));
        }

        setupSortingDropdown();

        // ✅ Initialize Currency Selector
        currencySelector.getItems().addAll("USD", "EUR", "TND", "GBP", "CAD", "AUD", "JPY");
        currencySelector.setValue("USD"); // Default to USD
        currencySelector.setOnAction(event -> updatePrices());

        fetchExchangeRates(); // ✅ Fetch exchange rates at startup
    }

    private void setupCurrencySelector() {
        currencySelector.getItems().addAll("USD", "EUR", "TND", "GBP", "CAD", "AUD", "JPY");
        currencySelector.setValue("USD"); // Default currency
        currencySelector.setOnAction(event -> updatePrices());
    }

    private void loadServices() {
        allServices = serviceOffreService.getAll();
        displayServices(allServices);
    }

    private void displayServices(List<ServiceOffre> services) {
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
        StackPane card = new StackPane();
        card.setStyle("-fx-background-color: white; " +
                "-fx-border-radius: 10px; " +
                "-fx-padding: 15px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 10, 0, 0, 5); " +
                "-fx-min-width: 450px; " +
                "-fx-max-width: 450px; " +
                "-fx-min-height: 250px; " +
                "-fx-max-height: 250px;");

        VBox content = new VBox(10);
        content.getChildren().addAll(
                createLabel(service.getTitle(), 20, "black", true),
                createPriceAndExperience(service),
                createLabel(shortenDescription(service.getDescription()), 14, "#666", false),
                createSkillTags(service.getSkills()),
                createApplyButton(service)
        );

        HBox topBar = new HBox();
        topBar.setStyle("-fx-alignment: top-right;");
        topBar.getChildren().add(createMenuButton(service));

        VBox cardLayout = new VBox();
        cardLayout.getChildren().addAll(topBar, content);

        card.getChildren().add(cardLayout);
        return card;
    }

    private HBox createPriceAndExperience(ServiceOffre service) {
        HBox hbox = new HBox(20);

        Label priceLabel = new Label("$" + service.getPrice());
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

    private HBox createSkillTags(String skills) {
        HBox skillBox = new HBox(5);

        if (skills != null && !skills.isEmpty()) {
            String[] skillList = skills.split(",");
            for (String skill : skillList) {
                Label skillTag = new Label(skill.trim());
                skillTag.setStyle("-fx-background-color: #E0E0E0; -fx-padding: 5px 10px; -fx-border-radius: 15px; -fx-text-fill: #333;");
                skillBox.getChildren().add(skillTag);
            }
        }

        return skillBox;
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

        // Load hCaptcha widget
        WebView hcaptchaWebView = new WebView();
        WebEngine engine = hcaptchaWebView.getEngine();
        engine.setJavaScriptEnabled(true); // Enable JavaScript
// Add error listener for debugging
        engine.setOnError(event -> {
            System.err.println("WebView Error: " + event.getMessage());
        });

        String html = "<html>"
                + "<head><script src='https://js.hcaptcha.com/1/api.js' async defer></script></head>"
                + "<body>"
                + "<form>"
                + "<div class='h-captcha' data-sitekey='" + HCAPTCHA_SITE_KEY + "'></div>"
                + "</form>"
                + "</body></html>";
        engine.loadContent(html);



        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            String userResponseToken = (String) engine.executeScript("hcaptcha.getResponse()");
            if (verifyHcaptcha(userResponseToken)) {
                handleSubmitApplication(service, priceField.getText());
                dialog.close();
            } else {
                showErrorAlert("Error", "hCaptcha verification failed. Please complete the captcha.");
            }
        });
        submitButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        submitButton.setOnAction(event -> {
            handleSubmitApplication(service, priceField.getText());
            dialog.close();
        });

        dialogVBox.getChildren().addAll(infoLabel, priceField, submitButton, hcaptchaWebView);

        Scene dialogScene = new Scene(dialogVBox, 400, 400);
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
                0, offeredPrice, 0, "Pending", service.getId_service(),0 // review (default empty)
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

    private void onSearchTextChanged(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            displayServices(allServices);
            return;
        }

        List<ServiceOffre> filteredServices = allServices.stream()
                .filter(service -> service.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                        service.getDescription().toLowerCase().contains(searchText.toLowerCase()))
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

    private void loadHcaptcha() {
        String html = "<html>"
                + "<head><script src='https://js.hcaptcha.com/1/api.js' async defer></script></head>"
                + "<body>"
                + "<form>"
                + "<div class='h-captcha' data-sitekey='" + HCAPTCHA_SITE_KEY + "'></div>"
                + "</form>"
                + "</body></html>";
        hcaptchaWebView.getEngine().loadContent(html);


        hcaptchaWebView.getEngine().setOnError(event -> {
            System.err.println("WebView Error: " + event.getMessage());
        });
    }

    private boolean verifyHcaptcha(String userResponseToken) {
        try {

            String url = "https://api.hcaptcha.com/siteverify";
            String params = "secret=" + HCAPTCHA_SECRET_KEY + "&response=" + userResponseToken;

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.getOutputStream().write(params.getBytes());

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = reader.readLine();
            reader.close();

            JSONObject json = new JSONObject(response);
            return json.getBoolean("success");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



}
