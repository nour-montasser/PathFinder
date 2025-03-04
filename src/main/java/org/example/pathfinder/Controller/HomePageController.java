package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import org.example.pathfinder.Model.JobOffer;
import org.example.pathfinder.Service.JobOfferService;

import java.io.IOException;
import java.util.List;

public class HomePageController {

    @FXML private Label welcomeLabel;
    @FXML private Label introLabel;
    @FXML private ImageView logoImage;
    @FXML private ImageView compassImage;
    @FXML private HBox jobOffersContainer;

    private JobOfferService jobOfferService = new JobOfferService();

    @FXML
    public void initialize() {
        // Set text dynamically if needed
        welcomeLabel.setText("Welcome to Pathfinder!");
        introLabel.setText("Pathfinder helps you find job opportunities, connect with companies, \n" +
                "and enhance your skills through interactive skill tests. Start your journey today!");

        // Set the logo image dynamically
        Image logo = new Image(getClass().getResourceAsStream("/org/example/pathfinder/view/Sources/pathfinder_logo.png"));
        logoImage.setImage(logo);

        // Set the compass logo dynamically
        Image compassLogo = new Image(getClass().getResourceAsStream("/org/example/pathfinder/view/Sources/pathfinder_logo_compass.png"));
        compassImage.setImage(compassLogo);

        // Load job offers
        loadJobOffers();
    }

    private void loadJobOffers() {
        try {
            // Get first 5 job offers
            List<JobOffer> jobOffers = jobOfferService.getall().stream().limit(5).toList();

            for (JobOffer offer : jobOffers) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Frontoffice/JobOfferListCard.fxml"));
                Node jobCard = loader.load();

                // Cast to Region to set width
                if (jobCard instanceof Region) {
                    Region region = (Region) jobCard;
                    region.setMinWidth(300);
                    region.setMaxWidth(300);
                    region.setPrefWidth(300);
                    region.setPrefHeight(150);


                }

                JobOfferListCardController controller = loader.getController();
                controller.setJobOffer(offer);

                jobOffersContainer.getChildren().add(jobCard);
            }
        } catch (IOException e) {
            System.err.println("Error loading job offer cards: " + e.getMessage());
        }
    }}