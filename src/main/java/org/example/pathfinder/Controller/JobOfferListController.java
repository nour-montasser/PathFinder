package org.example.pathfinder.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
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

import java.io.IOException;
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
    ApplicationService applicationService = new ApplicationService();

    private JobOfferService jobOfferService;
    private ObservableList<JobOffer> jobOffers;
    private String loggedUserRole = LoggedUser.getInstance().getRole();
    private long loggedInUserId = LoggedUser.getInstance().getUserId();

    @FXML
    public void initialize() {

        String imagePath = String.valueOf(getClass().getResource("/org/example/pathfinder/view/Sources/pathfinder_logo_compass.png"));
        searchIcon.setImage(new Image(imagePath));
        jobOfferService = new JobOfferService();
        jobOffers = FXCollections.observableArrayList();
        loadJobOffers();
        refreshJobOfferList();


        if (loggedUserRole.equals("COMPANY")) {
            btnViewApplications.setVisible(false);  // Hide the applications button for companies
            filterComboBox.setOnAction(event -> {
                String selectedFilter = filterComboBox.getSelectionModel().getSelectedItem ().toString();
                // Call method to filter job offers based on the selected option
                filterJobOffers(selectedFilter);
            });
        }else if(loggedUserRole.equals("SEEKER")) {
            filterComboBox.setVisible(false);
            addJobOfferButton.setVisible(false);
        }

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterJobOffersBySearch(newValue);

        });
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
        System.out.println("Search text: " + searchText);
        System.out.println("Filtered list size: " + jobOffers.size());
        refreshJobOfferList();
    }

    @FXML
    private void filterJobOffers(String filter) {
        if ("Show My Job Offers".equals(filter)) {
            // Load only job offers belonging to the logged-in user
            jobOffers.setAll(jobOfferService.getByUserId(loggedInUserId));
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
}
