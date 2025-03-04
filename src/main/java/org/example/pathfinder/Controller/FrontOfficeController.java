    package org.example.pathfinder.Controller;

    import javafx.application.Platform;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Parent;
    import javafx.scene.control.Button;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
    import javafx.scene.layout.StackPane;
    import javafx.scene.text.Font;

    import java.io.IOException;
    import java.net.URL;

    public class FrontOfficeController {

        @FXML
        private ImageView logoImage; // The logo in the navbar

        @FXML
        private StackPane contentArea; // The main content area where views will be loaded dynamically

        @FXML
        private Button homeButton, freelanceButton, findJobButton, profileButton, messagesButton, helpButton, serviceButton;

        private Button activeButton; // Stores the currently active button

        public void initialize() {
            System.out.println("✅ FrontOfficeController initialized!");

            // Load Logo Image
            try {
                String logoPath = getClass().getResource("/org/example/pathfinder/view/Sources/pathfinder_logo_navbar.png").toExternalForm();
                logoImage.setImage(new Image(logoPath));
            } catch (Exception e) {
                System.err.println("⚠️ Logo Image not found: " + e.getMessage());
            }

            // Set Home as default active page and load it
            setActiveButton(homeButton);

            // Load HomePage and get its controller to initialize the cards
            try {
                String fxmlPath = "/org/example/pathfinder/view/Frontoffice/HomePage.fxml";
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent homeView = loader.load();
                HomePageController homeController = loader.getController();
homeController.initialize();
                // Add the view to contentArea
                contentArea.getChildren().clear();
                contentArea.getChildren().add(homeView);
            } catch (IOException e) {
                System.err.println("❌ Error loading HomePage.fxml");
                e.printStackTrace();
            }

            // Set up scene user data
            Platform.runLater(() -> {
                if (contentArea.getScene() != null) {
                    contentArea.getScene().setUserData(this);
                    System.out.println("✅ FrontOfficeController set as Scene User Data!");
                } else {
                    System.err.println("❌ Scene is NULL when trying to set user data!");
                }
            });
        }
        // 📌 Function to Load New Pages in contentArea
        private void loadPage(String fxmlFile) {
            try {
                String fxmlPath = "/org/example/pathfinder/view/Frontoffice/" + fxmlFile;
                URL fxmlUrl = FrontOfficeController.class.getResource(fxmlPath);

                if (fxmlUrl == null) {
                    System.err.println("❌ Cannot find FXML: " + fxmlPath);
                    return;
                }

                FXMLLoader loader = new FXMLLoader(fxmlUrl);
                Parent newView = loader.load();

                // If it's the Messages page, add the CSS
                if (fxmlFile.equals("Message.fxml")) {
                    String cssPath = "/styles/style.css";
                    URL cssUrl = FrontOfficeController.class.getResource(cssPath);

                    if (cssUrl != null) {
                        newView.getStylesheets().add(cssUrl.toExternalForm());
                    } else {
                        System.err.println("❌ Cannot find CSS: " + cssPath);
                    }
                }

                contentArea.getChildren().clear();
                contentArea.getChildren().add(newView);

            } catch (IOException e) {
                System.err.println("❌ Error loading " + fxmlFile);
                e.printStackTrace();
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
            loadPage("HomePage.fxml");
        }

        @FXML
        private void loadFreelancePage() {
            setActiveButton(freelanceButton);
            loadPage("ApplicationForum.fxml");
        }

        @FXML
        private void loadServicesPage() {
            setActiveButton(serviceButton);
            loadPage("ClientDashboard.fxml");
        }

        @FXML
        private void loadFindJobPage() {
            setActiveButton(findJobButton);
            loadPage("JobOfferList.fxml");
        }

        @FXML
        private void loadProfilePage() {
            setActiveButton(profileButton);
            loadPage("ProfileInformation.fxml");
        }

        @FXML
        private void loadMessagesPage() {
            setActiveButton(messagesButton);
            loadPage("Message.fxml");
        }

        @FXML
        private void loadHelpPage() {
            setActiveButton(helpButton);
            loadPage("CV-List-Client.fxml");
        }

        // 🔥 Load CV Forum (from Add CV Button)
        // 📌 Function to Load Views in contentArea
        public void loadView(Parent newView) {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(newView);
        }

        @FXML
        private void loadCVForum() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Frontoffice/CV-Forum.fxml"));
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