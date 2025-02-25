package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.pathfinder.Model.JobOffer;
import org.example.pathfinder.Model.ApplicationJob;
import org.example.pathfinder.Service.JobOfferService;
import org.example.pathfinder.Service.ApplicationService;

import java.sql.SQLException;

public class JobOfferBackOfficeController {
    @FXML
    private ListView<String> jobOfferListView; // Liste des offres
    @FXML
    private ListView<String> applicationListView; // Liste des applications
    @FXML
    private Button deleteButton;

    private final JobOfferService jobOfferService = new JobOfferService();
    private final ApplicationService jobApplicationService = new ApplicationService();

    private ObservableList<String> jobOfferList;
    private ObservableList<String> applicationList;

    @FXML
    public void initialize() {
        loadJobOffers();
        jobOfferListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    try {
                        handleJobOfferSelection(newValue);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private void loadJobOffers() {
        jobOfferList = FXCollections.observableArrayList();
        for (JobOffer offer : jobOfferService.getall()) {
            String jobOfferText = String.format("ID: %d | Title: %s | Type: %s | Spots: %d",
                    offer.getIdOffer(), offer.getTitle(), offer.getType(), offer.getNumberOfSpots());
            jobOfferList.add(jobOfferText);
        }
        jobOfferListView.setItems(jobOfferList);
    }

    private void handleJobOfferSelection(String selectedJobOfferText) throws SQLException {
        if (selectedJobOfferText != null) {
            // Extraire l'ID de l'offre sélectionnée
            String[] parts = selectedJobOfferText.split("\\|");
            long jobOfferId = Long.parseLong(parts[0].split(":")[1].trim());

            // Charger les candidatures associées
            loadApplications(jobOfferId);
        }
    }

    private void loadApplications(long jobOfferId) throws SQLException {
        applicationList = FXCollections.observableArrayList();
        for (ApplicationJob application : jobApplicationService.getApplicationsForJobOffer(jobOfferId)) {
            String applicationText = String.format("App ID: %d | User ID: %d | Status: %s",
                    application.getApplicationId(), application.getIdUser(), application.getStatus());
            applicationList.add(applicationText);
        }
        applicationListView.setItems(applicationList);
    }

    @FXML
    private void deleteSelectedJobOffer() {
        String selectedJobOfferText = jobOfferListView.getSelectionModel().getSelectedItem();
        if (selectedJobOfferText != null) {
            String[] parts = selectedJobOfferText.split("\\|");
            long jobOfferId = Long.parseLong(parts[0].split(":")[1].trim());
            jobOfferService.delete(jobOfferId);
            jobOfferList.remove(selectedJobOfferText);
            applicationList.clear(); // Vider la liste des applications après suppression
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
