package org.example.pathfinder.Controller;

import org.json.JSONObject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.pathfinder.Model.ServiceOffre;
import org.example.pathfinder.Service.ServiceOffreService;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;




public class ApplicationServiceController {



    private static final String HCAPTCHA_SITE_KEY = "ES_3ca1f53f02154f12aac8f49642c189ec"; // Replace with your actual hCaptcha site key


    @FXML
    private ImageView logoImage; // ✅ Logo ImageView

    @FXML
    private ImageView searchIcon; // ✅ Search Icon ImageView



    @FXML
    private ComboBox<String> currencySelector; // Dropdown for selecting currency
    @FXML
    private GridPane servicesGrid; // Grid for displaying services

    @FXML
    private Button addServiceButton; // Floating "Add Service" button

    @FXML
    private TextField searchField; // Search bar

    @FXML
    private ComboBox<String> sortDropdown; // Sorting dropdown

    @FXML
    private PieChart revenuePieChart;

    @FXML
    private WebView hcaptchaWebView;

    private static final String API_URL = " https://v6.exchangerate-api.com/v6/01b62ddd85a1a7a3b14cb327/latest/USD";
    private Map<Integer, Double> originalPrices = new HashMap<>(); // Store original prices by service ID
    private Map<String, Double> exchangeRates = new HashMap<>();

    private final ServiceOffreService serviceOffreService = new ServiceOffreService();
    private List<ServiceOffre> allServices; // Store all services initially

    @FXML
    public void initialize() {


        loadServices(); // Load services at startup
        searchField.textProperty().addListener((obs, oldVal, newVal) -> onSearchTextChanged(newVal));

        // ✅ Ensure sorting dropdown is initialized
        setupSortingDropdown();

        // ✅ Initialize Currency Selector
        currencySelector.getItems().addAll("USD", "EUR", "TND", "GBP", "CAD", "AUD", "JPY");
        currencySelector.setValue("USD"); // Default to USD
        currencySelector.setOnAction(event -> updatePrices());

        fetchExchangeRates(); // ✅ Fetch exchange rates at startup


        // ✅ Load Navbar Components
        loadLogo();
        loadSearchIcon();
    }
    private void loadLogo() {
        try {
            Image image = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/org/example/pathfinder/view/Sources/pathfinder_logo.png")
            ));
            logoImage.setImage(image);

            // ✅ Scale Logo Without Expanding Navbar
            logoImage.setFitWidth(140);
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

            // ✅ Set Icon Size Inside Search Bar
            searchIcon.setFitWidth(30);
            searchIcon.setFitHeight(30);
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
        card.setStyle("-fx-background-color: #FFF; " +
                "-fx-border-radius: 10px; " +
                "-fx-padding: 15px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 10, 0, 0, 5); " +
                "-fx-min-width: 500px; " +
                "-fx-max-width: 500px; " +
                "-fx-min-height: 270px; " +
                "-fx-max-height: 270px;");

        VBox content = new VBox(10);
        content.getChildren().addAll(
                createLabel(service.getTitle(), 20, "black", true),
                createPriceExperienceDate(service),
                createLabel(shortenDescription(service.getDescription()), 14, "#666", false),
                createSkillTags(service.getSkills()),
                createDetailsButton(service)
        );

        HBox topBar = new HBox();
        topBar.setStyle("-fx-alignment: top-right;");
        topBar.getChildren().add(createMenuButton(service));

        VBox cardLayout = new VBox();
        cardLayout.getChildren().addAll(topBar, content);

        card.getChildren().add(cardLayout);
        return card;
    }

    private HBox createPriceExperienceDate(ServiceOffre service) {
        HBox hbox = new HBox(20);

        VBox priceBox = new VBox();
        Label priceLabel = new Label("" + service.getPrice());
        priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #008000;");
        Label priceTypeLabel = new Label("Starting-price");
        priceTypeLabel.setStyle("-fx-text-fill: #A9A9A9; -fx-font-size: 12px;");
        priceBox.getChildren().addAll(priceLabel, priceTypeLabel);

        VBox experienceBox = new VBox();
        Label experienceLabel = new Label(service.getExperience_level());
        experienceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");
        Label experienceTypeLabel = new Label("Experience level");
        experienceTypeLabel.setStyle("-fx-text-fill: #A9A9A9; -fx-font-size: 12px;");
        experienceBox.getChildren().addAll(experienceLabel, experienceTypeLabel);

        VBox dateBox = new VBox();
        Label dateLabel = new Label(service.getFormattedDate());
        dateLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
        Label postedLabel = new Label("Posted on");
        postedLabel.setStyle("-fx-text-fill: #A9A9A9; -fx-font-size: 12px;");
        dateBox.getChildren().addAll(dateLabel, postedLabel);

        hbox.getChildren().addAll(priceBox, experienceBox, dateBox);
        return hbox;
    }

    private HBox createPriceAndExperience(ServiceOffre service) {
        HBox hbox = new HBox(20);

        Label priceLabel = new Label("DT" + service.getPrice());
        priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #008000;");

        Label experienceLabel = new Label(service.getExperience_level());
        experienceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        hbox.getChildren().addAll(priceLabel, experienceLabel);
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
        if (description.length() > 100) { // ✅ If the description is longer than 100 characters
            return description.substring(0, 97) + "..."; // ✅ Keep only the first 97 characters and add "..."
        }
        return description; // ✅ If it's already short, return it as is
    }


    private Label createLabel(String text, int fontSize, String color, boolean bold) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + color + "; " +
                "-fx-font-size: " + fontSize + "px; " +
                (bold ? "-fx-font-weight: bold;" : ""));
        return label;
    }

    private Button createDetailsButton(ServiceOffre service) {
        Button detailsButton = new Button("View Requests");
        detailsButton.setStyle("-fx-background-color: #512E1B; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-border-radius: 8px; " +
                "-fx-padding: 8px 16px;");
        detailsButton.setOnAction(event -> openApplicationDetails(service.getId_service()));
        return detailsButton;
    }

    private MenuButton createMenuButton(ServiceOffre service) {



        MenuButton menuButton = new MenuButton("⋮"); // Correct way to create a MenuButton
        menuButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        MenuItem editItem = new MenuItem("Edit");
        MenuItem deleteItem = new MenuItem("Delete");
        MenuItem viewStatsItem = new MenuItem("View Statistics");

        menuButton.getItems().addAll(editItem, deleteItem, viewStatsItem);
        editItem.setOnAction(event -> handleEditService(service));
        deleteItem.setOnAction(event -> handleDeleteService(service));
        viewStatsItem.setOnAction(event -> openFreelancerStats());

        return menuButton;
    }

    private void setupSortingDropdown() {
        if (sortDropdown == null) {
            System.out.println("⚠ sortDropdown is NULL! Check your FXML file.");
            return;
        }

        // ✅ Populate sorting options
        sortDropdown.getItems().addAll(
                "Price: Low to High",
                "Price: High to Low",
                "Newest First",
                "Oldest First"
        );

        // ✅ Set default value
        sortDropdown.setValue("Price: Low to High");

        // ✅ Attach sorting logic
        sortDropdown.setOnAction(event -> sortServices());
    }

    private void sortServices() {
        if (sortDropdown == null || sortDropdown.getValue() == null) return;

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
            default:
                return;
        }

        displayServices(allServices);
    }

    @FXML
    private void openServiceForumModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/ServiceForum.fxml"));
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.setScene(new Scene(root));
            modalStage.setTitle("Add a New Service");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            if (addServiceButton != null && addServiceButton.getScene() != null) {
                modalStage.initOwner(addServiceButton.getScene().getWindow());
            }
            modalStage.setResizable(false);
            modalStage.showAndWait();

            loadServices();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditService(ServiceOffre service) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/ServiceForum.fxml"));
            Parent root = loader.load();

            ServiceOffreController controller = loader.getController();
            // ✅ Ensure controller is properly initialized
            if (controller == null) {
                System.err.println("❌ ERROR: ServiceOffreController is NULL!");
                return;
            }
            controller.loadServiceData(service);

            Stage modalStage = new Stage();
            modalStage.setScene(new Scene(root));
            modalStage.setTitle("Edit Service");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.showAndWait();

            loadServices();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteService(ServiceOffre service) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Service: " + service.getTitle());
        alert.setContentText("Are you sure you want to delete this service?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                serviceOffreService.delete(service.getId_service());
                loadServices();
            }
        });
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

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openApplicationDetails(int serviceId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/showApplicationDetails.fxml"));
            Parent root = loader.load();

            ShowApplicationDetailsController controller = loader.getController();
            controller.setServiceId(serviceId);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Application Details");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openFreelancerStats() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/stats.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Revenue Statistics");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    }

    private static final String HCAPTCHA_SECRET_KEY = "ES_3ca1f53f02154f12aac8f49642c189ec"; // Replace with your actual hCaptcha secret key

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

    private void handleApplyForService(ServiceOffre service) {
        // Get the hCaptcha response token from the WebView
        WebEngine engine = hcaptchaWebView.getEngine();
        engine.executeScript("hcaptcha.getResponse()").toString();

        // Verify the hCaptcha token
        String userResponseToken = engine.executeScript("hcaptcha.getResponse()").toString();
        if (verifyHcaptcha(userResponseToken)) {
            // Proceed with the application
            openApplicationDetails(service.getId_service());
        } else {
            showErrorAlert("Error", "hCaptcha verification failed. Please complete the captcha.");
        }
    }



}

