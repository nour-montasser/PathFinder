package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class BackOfficeController {

    @FXML
    private StackPane contentArea; // The main content area

    @FXML
    private Button usersButton, cvsButton, jobOffersButton, skillTestsButton, channelsButton, reportsButton, freelanceGigsButton;

    private Button activeButton;
    @FXML
    private ImageView logoImage;// Stores the currently active button

    public void initialize() {
        System.out.println("✅ BackOfficeController initialized!");
        // Set Users as default active page
        setActiveButton(usersButton);
         String logoPath = getClass().getResource("/org/example/pathfinder/view/Sources/pathfinder_logo_white.png").toExternalForm();
         logoImage.setImage(new Image(logoPath));

    }


    // 📌 Function to Load New Pages in contentArea
    private void loadPage(String fxmlFile) {
        try {
            Parent newView = FXMLLoader.load(getClass().getResource("/org/example/pathfinder/view/Backoffice/" + fxmlFile));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(newView);
        } catch (IOException e) {
            System.err.println("❌ Error loading " + fxmlFile + ": " + e.getMessage());
        }
    }

    // ✅ Function to Set Active Button (Turns White & Adds Underline)
    private void setActiveButton(Button button) {
        if (activeButton != null) {
            activeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #F5E4C3; -fx-font-size: 16px; -fx-font-weight: bold; -fx-border-width: 0;");
        }

        button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-border-width: 0; -fx-border-bottom: 2px solid white;");
        activeButton = button;
    }

    // 🔹 Navigation Handlers (Each Button Loads a Different Page)
    @FXML
    private void loadUsersPage() {
        setActiveButton(usersButton);
        loadPage("Users.fxml");
    }

    @FXML
    private void loadCVsPage() {
        setActiveButton(cvsButton);
        loadPage("CVs.fxml");
    }

    @FXML
    private void loadJobOffersPage() {
        setActiveButton(jobOffersButton);
        loadPage("JobOfferList.fxml");
    }

    @FXML
    private void loadSkillTestsPage() {
        setActiveButton(skillTestsButton);
        loadPage("SkillTests.fxml");
    }

    @FXML
    private void loadChannelsPage() {
        setActiveButton(channelsButton);
        loadPage("channel-backoffice.fxml");
    }

    @FXML
    private void loadReportsPage() {
        setActiveButton(reportsButton);
        loadPage("Reports.fxml");
    }

    @FXML
    private void loadFreelanceGigsPage() {
        setActiveButton(freelanceGigsButton);
        loadPage("FreelanceGigs.fxml");
    }
}
