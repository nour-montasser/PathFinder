package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.pathfinder.Model.JobOffer;
import org.example.pathfinder.Service.JobOfferService;

public class JobOfferBackOfficeController {
    @FXML
    private ListView<String> jobOfferListView; // ListView instead of TableView
    @FXML
    private Button deleteButton;

    private final JobOfferService jobOfferService = new JobOfferService();
    private ObservableList<String> jobOfferList;

    @FXML
    public void initialize() {
        loadJobOffers();
    }

    private void loadJobOffers() {
        // Fetching all job offers and formatting them as a list of strings
        jobOfferList = FXCollections.observableArrayList();
        for (JobOffer offer : jobOfferService.getall()) {
            String jobOfferText = String.format("ID: %d | Title: %s | Type: %s | Spots: %d",
                    offer.getIdOffer(), offer.getTitle(), offer.getType(), offer.getNumberOfSpots());
            jobOfferList.add(jobOfferText);
        }
        jobOfferListView.setItems(jobOfferList);
    }

    @FXML
    private void deleteSelectedJobOffer() {
        String selectedJobOfferText = jobOfferListView.getSelectionModel().getSelectedItem();
        if (selectedJobOfferText != null) {
            // Extracting the ID from the selected string to delete the offer
            String[] parts = selectedJobOfferText.split("\\|");
            long jobOfferId = Long.parseLong(parts[0].split(":")[1].trim());
            jobOfferService.delete(jobOfferId);
            jobOfferList.remove(selectedJobOfferText);
        } else {
            showAlert("No selection", "Please select a job offer to delete.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
