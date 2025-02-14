package org.example.pathfinder.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
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

    private JobOfferService jobOfferService;
    private ObservableList<JobOffer> jobOffers;

    @FXML
    public void initialize() {
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
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(form));
            stage.showAndWait();

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
                }
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
}
