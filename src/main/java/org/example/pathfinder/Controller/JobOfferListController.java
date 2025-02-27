package org.example.pathfinder.Controller;

import com.mysql.cj.xdevapi.JsonArray;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.pathfinder.Model.JobOffer;
import org.example.pathfinder.Model.LoggedUser;
import org.example.pathfinder.Service.ApplicationService;
import org.example.pathfinder.Service.JobOfferService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class JobOfferListController {

    @FXML
    private GridPane jobOfferGridPane;  // Use GridPane instead of TilePane

    @FXML
    private Button addJobOfferButton;

    @FXML
    private ImageView searchIcon;

    @FXML
    private Button btnViewApplications;

    @FXML
    private ComboBox filterComboBox;

    @FXML
    private TextField searchField;
   // @FXML
   // private TextField citySearchField;
    @FXML
    private ComboBox<String> searchFilter;

    ApplicationService applicationService = new ApplicationService();

    private JobOfferService jobOfferService;
    private ObservableList<JobOffer> jobOffers;
    private String loggedUserRole = LoggedUser.getInstance().getRole();
    private long loggedInUserId = LoggedUser.getInstance().getUserId();
    private static final String GEONAMES_API_URL = "http://api.geonames.org/searchJSON?q=%s&maxRows=10&username=nourmontasser";


   /* @FXML
    private ComboBox<String> cityComboBox;*/
    @FXML
    public void initialize() {
        String imagePath = String.valueOf(getClass().getResource("/org/example/pathfinder/view/Sources/pathfinder_logo_compass.png"));
        searchIcon.setImage(new Image(imagePath));
        jobOfferService = new JobOfferService();
        jobOffers = FXCollections.observableArrayList();
        loadJobOffers();
        refreshJobOfferList();
        if ("COMPANY".equals(loggedUserRole)) {
            // For companies, show only "Show My Job Offers" and "Show All Job Offers"
            filterComboBox.setItems(FXCollections.observableArrayList(
                    "Show My Job Offers",
                    "Show All Job Offers"
            ));
        } else if ("SEEKER".equals(loggedUserRole)) {
            // For seekers, show all options
            filterComboBox.setItems(FXCollections.observableArrayList(
                    "Part-time",
                    "Full-time",
                    "Fixed-term contract",
                    "Long-term contract",
                    "Show All Job Offers"
            ));
        }
        if (loggedUserRole.equals("COMPANY")) {
            btnViewApplications.setVisible(false);  // Hide the applications button for companies
            filterComboBox.setOnAction(event -> {
                String selectedFilter = filterComboBox.getSelectionModel().getSelectedItem().toString();
                // Call method to filter job offers based on the selected option
                filterJobOffersForCompany(selectedFilter);
            });
        } else if (loggedUserRole.equals("SEEKER")) {
            filterComboBox.setVisible(true);  // Make it visible for seekers
            filterComboBox.setOnAction(event -> {
                String selectedFilter = filterComboBox.getSelectionModel().getSelectedItem().toString();
                // Call method to filter job offers based on the selected option for seekers
                filterJobOffersForSeeker(selectedFilter);
            });
            addJobOfferButton.setVisible(false);  // Hide add button for seekers
        }


        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (searchFilter.getValue().equals("By City")) {
                if (newValue.isEmpty()) {
                    jobOffers.setAll(jobOfferService.getall()); // Reset list when field is empty
                    refreshJobOfferList();
                    cityContextMenu.hide();
                } else if (newValue.length() > 2) {
                    fetchCities(newValue); // Fetch cities only if more than 2 characters
                }
            } else {
                filterJobOffersBySearch(newValue);
            }
        });


        // Handle selection of a city from the ComboBox
       /* cityComboBox.setOnAction(event -> {
            String selectedCity = cityComboBox.getValue();
            if (selectedCity != null) {
                searchField.setText(selectedCity);  // Set the selected city into the search field
                cityComboBox.setVisible(false); // Hide the ComboBox after selection
                // Optionally, fetch the city's coordinates or use the selected city in your logic
            }
        });*/



        // Listen to width changes for responsive design
        jobOfferGridPane.widthProperty().addListener((obs, oldWidth, newWidth) -> refreshJobOfferList());
    }

    private void filterJobOffersBySearch(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            jobOffers.setAll(jobOfferService.getall()); // Recharger toutes les offres si le champ est vide
        } else {
            String lowerCaseSearch = searchText.toLowerCase();

            ObservableList<JobOffer> filteredList = FXCollections.observableArrayList(
                    jobOfferService.getall().stream()
                            .filter(jobOffer -> jobOffer.getTitle() != null && jobOffer.getTitle().toLowerCase().contains(lowerCaseSearch))
                            .collect(Collectors.toList())
            );


            jobOffers.setAll(filteredList);
        }
        //System.out.println("Search text: " + searchText);
       // System.out.println("Filtered list size: " + jobOffers.size());
        refreshJobOfferList();
    }

    private void filterJobOffersForCompany(String filter) {
        if ("Show My Job Offers".equals(filter)) {
            // Load only job offers belonging to the logged-in user (company)
            jobOffers.setAll(jobOfferService.getByUserId(loggedInUserId));
        } else if ("Show All Job Offers".equals(filter)) {
            // Load all job offers
            jobOffers.setAll(jobOfferService.getall());
        }
        refreshJobOfferList(); // Refresh UI with new list
    }


    private void filterJobOffersForSeeker(String filter) {
        if ("Full-time".equals(filter)) {
            // Filter for full-time positions
            jobOffers.setAll(jobOfferService.getByJobType("Full-time"));
        } else if ("Part-time".equals(filter)) {
            // Filter for part-time positions
            jobOffers.setAll(jobOfferService.getByJobType("Part-time"));
        }
        else if ("Fixed-term contract".equals(filter)) {
            jobOffers.setAll(jobOfferService.getByJobType("Fixed-term contract"));
        } else if ("Long-term contract".equals(filter)) {
            jobOffers.setAll(jobOfferService.getByJobType("Long-term contract"));
        } else if ("Show All Job Offers".equals(filter)) {
            // Load all job offers
            jobOffers.setAll(jobOfferService.getall());
        }

        refreshJobOfferList(); // Refresh UI with new list
    }


    @FXML
    public void handleAddJobOffer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Frontoffice/JobOfferForm.fxml"));
            VBox form = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add Job Offer");
            stage.initModality(Modality.APPLICATION_MODAL);  // Block interaction with other windows
            stage.setScene(new Scene(form));

            // Make window non-resizable and remove default decorations (close button, etc.)
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);

            // Create an overlay for the transparent effect
            StackPane overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");  // Semi-transparent background

            overlay.getChildren().add(form);

            Scene overlayScene = new Scene(overlay);

            // Prevent window from being moved
            stage.setResizable(false);
            stage.setScene(overlayScene);
            stage.showAndWait();

            // Refresh the job offer list after closing the form
            loadJobOffers();
            refreshJobOfferList();
        } catch (IOException e) {
            System.err.println("Error opening Add Job Offer form: " + e.getMessage());
            e.printStackTrace();
        }
    }




    public void refreshJobOfferList() {
        jobOfferGridPane.getChildren().clear();  // Clear existing cards

        int columns = 3;  // Number of columns
        int row = 0;
        int col = 0;

        double gridWidth = jobOfferGridPane.getWidth();  // Get the current width of the GridPane
        double cardWidth = (gridWidth - (columns - 1) * 10) / columns;  // Calculate width for each card (including gaps)
        double cardHeight = 150;  // Fixed height for the cards

        for (JobOffer jobOffer : jobOffers) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Frontoffice/JobOfferListCard.fxml"));
                VBox card = loader.load();

               /*String css = getClass().getResource("/org/example/pathfinder/view/Frontoffice/styles.css").toExternalForm();
                card.getStylesheets().add(css);*/

                JobOfferListCardController controller = loader.getController();
                controller.setJobOffer(jobOffer);
                controller.setParentController(this);

                // Set calculated size for the card
                card.setPrefSize(cardWidth, cardHeight);

                // Add the card to the GridPane
                jobOfferGridPane.add(card, col, row);
                GridPane.setMargin(card, new javafx.geometry.Insets(10));  // Add spacing

                col++;
                if (col == columns) {
                    col = 0;
                    row++;
                }//System.out.println("GridPane width: " + jobOfferGridPane.getWidth());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void loadJobOffers() {
        jobOffers.setAll(jobOfferService.getall());
    }

    public ObservableList<JobOffer> getJobOffers() {
        return jobOffers;
    }




    @FXML
    private void handleViewApplications() {
        try {
            // Charger la vue principale (navbar + contentArea)
            FXMLLoader frontOfficeLoader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Frontoffice/main-frontoffice.fxml"));
            Parent frontOfficeView = frontOfficeLoader.load();
            FrontOfficeController frontOfficeController = frontOfficeLoader.getController();

            // Charger la liste des applications et l'injecter dans contentArea
            Parent applicationListView = FXMLLoader.load(getClass().getResource("/org/example/pathfinder/view/Frontoffice/ApplicationList.fxml"));
            frontOfficeController.loadView(applicationListView); // Fonction à ajouter dans FrontOfficeController

            // Obtenir la fenêtre actuelle (Stage)
            Stage stage = (Stage) searchIcon.getScene().getWindow();

            // Recréer la scène avec le nouveau contenu
            Scene newScene = new Scene(frontOfficeView);
            newScene.getStylesheets().add(getClass().getResource("/org/example/pathfinder/view/Frontoffice/styles.css").toExternalForm());

            // Appliquer la nouvelle scène et forcer le redimensionnement
            stage.setScene(newScene);
            stage.setMaximized(false);
            stage.setMaximized(true);

            stage.show();

        } catch (IOException e) {
            showError("Error opening application list view: " + e.getMessage());
        }
    }


    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    //************************************************************
    private final String GEONAMES_USERNAME = "nourmontasser";

    private void fetchCities(String query) {
        String apiUrl = String.format(GEONAMES_API_URL, query);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();

        HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        handleCityResponse(response.body());
                    }
                });

    }

    private ContextMenu cityContextMenu = new ContextMenu(); // Keep a single context menu

    private void handleCityResponse(String responseBody) {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONArray cities = jsonResponse.getJSONArray("geonames");
            List<String> cityNames = new ArrayList<>();

            for (int i = 0; i < cities.length(); i++) {
                JSONObject city = cities.getJSONObject(i);
                String cityName = city.getString("name");
                cityNames.add(cityName);
            }

            Platform.runLater(() -> {
                cityContextMenu.getItems().clear(); // Clear previous suggestions

                if (!cityNames.isEmpty()) {
                    for (String city : cityNames) {
                        MenuItem menuItem = new MenuItem(city);
                        menuItem.setOnAction(event -> {
                            searchField.setText(city); // Set selected city
                            cityContextMenu.hide();

                            // Fetch coordinates and filter job offers
                            fetchCityCoordinates(city, (selectedLat, selectedLng) -> {
                                System.out.println("Selected city coordinates: " + selectedLat + ", " + selectedLng);
                                filterJobOffersByDistance(selectedLat, selectedLng);
                            });
                        });
                        cityContextMenu.getItems().add(menuItem);
                    }

                    if (!cityContextMenu.isShowing()) {
                        cityContextMenu.show(searchField, javafx.geometry.Side.BOTTOM, 0, 0);
                    }
                } else {
                    cityContextMenu.hide();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchCityCoordinates(String cityName, BiConsumer<Double, Double> callback) {
        String apiUrl = String.format(GEONAMES_API_URL, cityName);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();

        HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response.body());
                            JSONArray cities = jsonResponse.getJSONArray("geonames");

                            if (cities.length() > 0) {
                                JSONObject city = cities.getJSONObject(0);
                                double lat = city.getDouble("lat");
                                double lng = city.getDouble("lng");

                                // Pass the coordinates to the callback
                                callback.accept(lat, lng);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void filterJobOffersByDistance(double selectedLat, double selectedLng) {
        List<JobOffer> filteredJobs = new ArrayList<>();
        Map<String, double[]> cityCoordinatesCache = new HashMap<>(); // Cache to avoid multiple API calls

        // Iterate over job offers
        for (JobOffer job : jobOffers) {
            String address = job.getAddress();
           // System.out.println(job);
            if (address == null || !address.contains(",")) {
                continue; // Skip if no address or invalid format
            }

            String jobCity = address.split(",")[1].trim(); // Extract city name
                // Fetch city coordinates asynchronously if not cached
                fetchCityCoordinates(jobCity, (lat, lng) -> {
                    cityCoordinatesCache.put(jobCity, new double[]{lat, lng});
                    double distance = calculateDistance(selectedLat, selectedLng, lat, lng);
                    System.out.println("Distance1: " + distance);
                    if (distance <= 200) {
                        filteredJobs.add(job);
                    }
                    updateJobOffers(filteredJobs); // Update after all cities processed
                });

        }
    }

    private void updateJobOffers(List<JobOffer> filteredJobs) {
        Platform.runLater(() -> {
            jobOffers.setAll(filteredJobs);
            refreshJobOfferList();
        });
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in km
    }


    // Method to handle hover effects for Buttons
// Method to handle hover effects for Buttons
    public void handleButtonHoverEnter(Event event) {
        Button button = (Button) event.getSource();
        button.setStyle("-fx-background-color: #5b3f29; " // change background color to green
                + "-fx-text-fill: white; "          // set text color to white
                + "-fx-font-size: 16px; "          // set font size
                + "-fx-font-weight: bold; "        // set font weight to bold
                + "-fx-padding: 10px 20px; "       // padding around the text
                + "-fx-border-radius: 25px; "     // rounded corners
                + "-fx-cursor: hand; "            // hand cursor to indicate clickable
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 2, 2);"); // shadow effect
    }

    // Method to handle hover exit effects for Buttons
    public void handleButtonHoverExit(Event event) {
        Button button = (Button) event.getSource();
        button.setStyle("-fx-background-color: #3b261d; "   // reset background color
                + "-fx-text-fill: white; "          // keep text color white
                + "-fx-font-size: 16px; "          // keep font size
                + "-fx-font-weight: bold; "        // keep font weight bold
                + "-fx-padding: 10px 20px; "       // keep padding around text
                + "-fx-border-radius: 25px; "     // keep rounded corners
                + "-fx-cursor: hand; "            // keep hand cursor
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 2, 2);"); // keep shadow effect
    }

    public void handleTextFieldHoverEnter(Event event) {
        TextField textField = (TextField) event.getSource();
        textField.setStyle("-fx-background-color: #f0f0f0; "  // light gray background on hover
                + "-fx-border-color: #3b261d; "   // border color to match button
                + "-fx-border-width: 2px; "       // make border a bit thicker
                + "-fx-cursor: text;");           // change cursor to text input
    }

    public void handleTextFieldHoverExit(Event event) {
        TextField textField = (TextField) event.getSource();
        textField.setStyle("-fx-background-color: transparent; "  // reset to transparent background
                + "-fx-border-color: #ccc; "   // default border color
                + "-fx-border-width: 0px; "    // remove border width
                + "-fx-cursor: text;");        // cursor remains as text input
    }

    // Method to handle hover effects for ComboBox
    public void handleComboBoxHoverEnter(Event event) {
        ComboBox comboBox = (ComboBox) event.getSource();
        comboBox.setStyle("-fx-background-color: #f0f0f0; "  // light gray background on hover
                + "-fx-border-color: #3b261d; "   // border color to match button
                + "-fx-border-width: 2px; "       // make border a bit thicker
                + "-fx-cursor: hand;");           // change cursor to indicate clickable
    }

    // Method to handle hover exit effects for ComboBox
    public void handleComboBoxHoverExit(Event event) {
        ComboBox comboBox = (ComboBox) event.getSource();
        comboBox.setStyle("-fx-background-color: transparent; "  // reset to transparent background
                + "-fx-border-color: #ccc; "   // default border color
                + "-fx-border-width: 0px; "    // remove border width
                + "-fx-cursor: hand;");        // cursor remains as hand cursor
    }

    // Method to handle hover effects for ImageView (e.g., icons)
    public void handleImageViewHoverEnter(Event event) {
        ImageView imageView = (ImageView) event.getSource();
        imageView.setStyle("-fx-opacity: 0.8; ");  // reduce opacity to indicate hover effect
    }

    // Method to handle hover exit effects for ImageView (e.g., icons)
    public void handleImageViewHoverExit(Event event) {
        ImageView imageView = (ImageView) event.getSource();
        imageView.setStyle("-fx-opacity: 1; ");  // reset opacity back to normal
    }

}
