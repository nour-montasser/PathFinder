package org.example.pathfinder.Controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.pathfinder.Model.JobOffer;
import org.example.pathfinder.Service.JobOfferService;

import java.io.IOException;

public class JobOfferListController {

    @FXML
    private GridPane jobOfferGridPane;  // Use GridPane instead of TilePane

    @FXML
    private Button addJobOfferButton;

    @FXML
    private ImageView searchIcon;

    private JobOfferService jobOfferService;
    private ObservableList<JobOffer> jobOffers;

    @FXML
    public void initialize() {

        String imagePath = String.valueOf(getClass().getResource("/org/example/pathfinder/Sources/pathfinder_logo_compass.png"));
        searchIcon.setImage(new Image(imagePath));
        jobOfferService = new JobOfferService();
        jobOffers = FXCollections.observableArrayList();
        loadJobOffers();
        refreshJobOfferList();
        // Listen to width changes for responsive design
        jobOfferGridPane.widthProperty().addListener((obs, oldWidth, newWidth) -> refreshJobOfferList());
    }

    @FXML
    public void handleAddJobOffer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/View/JobOfferForm.fxml"));
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/View/JobOfferListCard.fxml"));
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
                }System.out.println("GridPane width: " + jobOfferGridPane.getWidth());
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
    public void handleViewApplications() {
        try {
            // Load the FXML for the application list view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/View/ApplicationList.fxml"));
            Parent applicationListView = loader.load();

            // Get the current stage (assuming this is called from the main stage)
            Stage stage = (Stage) searchIcon.getScene().getWindow();


            // Set the new scene
            Scene applicationListScene = new Scene(applicationListView);
            stage.setScene(applicationListScene);
            stage.setMaximized(false); //   Temporarily disable maximization
            stage.setMaximized(true);
            applicationListScene.getStylesheets().add(getClass().getResource("/org/example/pathfinder/view/styles.css").toExternalForm());

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
