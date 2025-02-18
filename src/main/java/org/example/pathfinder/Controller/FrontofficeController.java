package org.example.pathfinder.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class FrontofficeController {



    @FXML
    private StackPane contentArea; // The main content area where views will be loaded dynamically

    @FXML
    private Button homeButton, freelanceButton, findJobButton, profileButton, messagesButton, helpButton;

    private Button activeButton; // Stores the currently active button

    public void initialize() {
        System.out.println("✅ FrontOfficeController initialized!");


        Platform.runLater(() -> {
            if (contentArea.getScene() != null) {
                contentArea.getScene().setUserData(this);
                System.out.println("✅ FrontOfficeController set as Scene User Data!");
            } else {
                System.err.println("❌ Scene is NULL when trying to set user data!");
            }
        });
        // Set Home as default active page
        setActiveButton(homeButton);

    }

    // 📌 Function to Load New Pages in contentArea
    private void loadPage(String fxmlFile) {
        try {
            Parent newView = FXMLLoader.load(getClass().getResource("/org/example/pathfinder/view/Frontoffice/" + fxmlFile));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(newView);
        } catch (IOException e) {
            System.err.println("❌ Error loading " + fxmlFile + ": " + e.getMessage());
        }
    }

    // ✅ Function to Set Active Button (Turns White & Adds Underline)
    private void setActiveButton(Button button) {
        if (activeButton != null) {
            activeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #F5E4C3; -fx-font-size: 18px; -fx-font-weight: bold; -fx-border-width: 0;");
        }

        button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-border-width: 0; -fx-border-bottom: 2px solid white;");
        activeButton = button;
    }

    // 🔹 Navigation Handlers (Each Button Loads a Different Page)
    @FXML
    private void loadHomePage() {
        setActiveButton(homeButton);
        loadPage("Home.fxml");
    }

    @FXML
    private void loadFreelancePage() {
        setActiveButton(freelanceButton);
        loadPage("ClientDashboard.fxml");
    }

    @FXML
    private void loadFindJobPage() {
        setActiveButton(findJobButton);
        loadPage("FindJob.fxml");
    }

    @FXML
    private void loadProfilePage() {
        setActiveButton(profileButton);
        loadPage("Profile.fxml");
    }

    @FXML
    private void loadMessagesPage() {
        setActiveButton(messagesButton);
        loadPage("ApplicationForum.fxml");
    }

    @FXML
    private void loadHelpPage() {
        setActiveButton(helpButton);
        loadPage("load.fxml");
    }

    // 🔥 Load CV Forum (from Add CV Button)
    // 📌 Function to Load Views in contentArea
    public void loadView(Parent newView) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(newView);
    }

    @FXML
    private void loadServiceForum() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/ClientDashboard.fxml"));
            Parent cvForumView = loader.load();
            loadView(cvForumView);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Error loading CV-Forum.fxml");
        }

    }


    public void setContent(Parent newContent) {
        if (contentArea.getScene() == null) {
            System.err.println("⚠️ Scene is NULL, delaying content update...");
            contentArea.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    System.out.println("✅ Scene initialized, updating contentArea.");
                    updateContent(newContent);
                }
            });
        } else {
            System.out.println("✅ Scene exists, updating contentArea immediately.");
            updateContent(newContent);
        }
    }
    // Helper method to update content safely
    private void updateContent(Parent newContent) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(newContent);
    }


}