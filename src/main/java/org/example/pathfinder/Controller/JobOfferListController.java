package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.pathfinder.Model.JobOffer;
import org.example.pathfinder.Service.JobOfferService;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JobOfferListController {

    @FXML
    private ListView<JobOffer> jobOfferList;

    @FXML
    private Button addJobOfferButton;

    private JobOfferService jobOfferService;

    @FXML
    public void initialize() {
        jobOfferService = new JobOfferService();  // Initialize service

        List<JobOffer> jobOffers = jobOfferService.getall();  // Fetch job offers
        jobOfferList.getItems().setAll(jobOffers);  // Populate ListView

        // Custom cell factory to display job offer cards
        jobOfferList.setCellFactory(listView -> new ListCell<JobOffer>() {
            @Override
            protected void updateItem(JobOffer jobOffer, boolean empty) {
                super.updateItem(jobOffer, empty);
                if (jobOffer != null && !empty) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/View/JobOfferListCard.fxml"));
                        VBox card = loader.load();

                        JobOfferListCardController controller = loader.getController();
                        controller.setJobOffer(jobOffer);  // Pass the job offer to the card controller

                        setGraphic(card);
                    } catch (IOException e) {
                        e.printStackTrace();
                        setGraphic(null);
                    }
                } else {
                    setGraphic(null);
                }
            }
        });

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
        } catch (IOException e) {
            System.err.println("Error opening Add Job Offer form: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
