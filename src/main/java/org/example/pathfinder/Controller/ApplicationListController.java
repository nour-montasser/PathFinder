package org.example.pathfinder.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.pathfinder.Model.ApplicationJob;
import org.example.pathfinder.Model.CoverLetter;
import org.example.pathfinder.Model.LoggedUser;
import org.example.pathfinder.Service.ApplicationService;
import org.example.pathfinder.Model.JobOffer;
import org.example.pathfinder.Service.CoverLetterService;
import org.example.pathfinder.Service.JobOfferService;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationListController {

    @FXML
    private ListView<ApplicationJob> applicationListView;
    @FXML
    private ImageView searchIcon;
    private ApplicationService applicationService;
    private JobOfferService jobOfferService;
    @FXML
    private TextField searchField;

    private CoverLetterService coverLetterService;
    private long loggedInUserId = LoggedUser.getInstance().getUserId();
    @FXML
    public void initialize() {
        applicationService = new ApplicationService();
        applicationService = new ApplicationService();
        jobOfferService = new JobOfferService();
        String imagePath = getClass().getResource("/org/example/pathfinder/view/Sources/pathfinder_logo_compass.png").toString();
        searchIcon.setImage(new Image(imagePath));
        coverLetterService = new CoverLetterService();
        applicationListView.setItems(getApplicationsForUser());
        applicationListView.setCellFactory(param -> createApplicationCell());

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterApplicationsBySearch(newValue);

        });
    }

    private void filterApplicationsBySearch(String searchText) {
        ObservableList<ApplicationJob> filteredList;

        if (searchText == null || searchText.trim().isEmpty()) {
            // Recharger toutes les candidatures de l'utilisateur si le champ est vide
            filteredList = getApplicationsForUser();
        } else {
            String lowerCaseSearch = searchText.toLowerCase();

            // Filtrer les candidatures de l'utilisateur connecté uniquement
            filteredList = FXCollections.observableArrayList(

                    getApplicationsForUser().stream()
                            .filter(application -> {
                                String name = jobOfferService.getById(application.getJobOfferId()).getTitle();
                                return name != null && name.toLowerCase().contains(lowerCaseSearch);
                            })
                            .collect(Collectors.toList())
            );
        }

        applicationListView.setItems(filteredList);
    }

    private ObservableList<ApplicationJob> getApplicationsForUser() {
        ObservableList<ApplicationJob> applications = FXCollections.observableArrayList();
        try {
            List<ApplicationJob> applicationList = applicationService.getApplicationsForUser(loggedInUserId);
            applications.addAll(applicationList);
        } catch (SQLException e) {
            showError("Error retrieving applications: " + e.getMessage());
        }
        return applications;
    }

    private ListCell<ApplicationJob> createApplicationCell() {
        return new ListCell<ApplicationJob>() {
            @Override
            protected void updateItem(ApplicationJob item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    HBox card = createApplicationCard(item);
                    card.setFocusTraversable(true);
                    card.setStyle("-fx-padding: 10; -fx-border-color: lightgray; " +
                            "-fx-border-radius: 10; -fx-background-color: white; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 2, 2); " +
                            "-fx-margin: 10 0 10 0; -fx-background-radius: 10;");
                    setGraphic(card);
                } else {
                    setGraphic(null);
                }
            }
        };
    }

    private HBox createApplicationCard(ApplicationJob application) {
        HBox card = new HBox(10);
        card.setStyle("-fx-padding: 10; -fx-border-color: lightgray; -fx-border-radius: 10; " +
                "-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 2, 5);");

        VBox content = new VBox(5);
        JobOffer jobOffer = applicationService.getJobOfferById(application.getJobOfferId());
        String cvTitle = applicationService.getCVTitleById(application.getCvId());

        Text statusText = createStatusText(application);
        content.getChildren().add(new Text("Job Title: " + jobOffer.getTitle()));
        content.getChildren().add(new Text("Application Date: " + application.getDateApplication().toString()));
        content.getChildren().add(new Text("CV Title: " + cvTitle));
        content.getChildren().add(statusText);

        MenuButton actionsMenu = createActionsMenu(application);
        VBox buttonBox = new VBox(5);
        buttonBox.getChildren().add(actionsMenu);

        card.getChildren().addAll(content, buttonBox);
        return card;
    }

    private Text createStatusText(ApplicationJob application) {
        Text statusText = new Text("Status: " + application.getStatus());
        statusText.setFont(Font.font("Arial", 14));

        switch (application.getStatus()) {
            case "Accepted":
                statusText.setFill(Color.GREEN);
                break;
            case "Rejected":
                statusText.setFill(Color.RED);
                break;
            default:
                statusText.setFill(Color.GRAY);
                break;
        }

        return statusText;
    }

    private MenuButton createActionsMenu(ApplicationJob application) {
        MenuButton actionsMenu = new MenuButton();
        actionsMenu.setStyle("-fx-background-color: transparent; -fx-font-size: 16px;");

        if ("Pending".equalsIgnoreCase(application.getStatus())) {
            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(event -> handleDeleteApplication(application));
            MenuItem updateItem = new MenuItem("Update");
            updateItem.setOnAction(event -> handleUpdateApplication(application));



            actionsMenu.getItems().addAll(deleteItem, updateItem);
        }
        MenuItem showCoverLetterItem = new MenuItem("Show Cover Letter");
        showCoverLetterItem.setOnAction(event -> handleShowCoverLetter(application));
        MenuItem showCvItem = new MenuItem("Show Cv");
        actionsMenu.getItems().addAll( showCoverLetterItem,showCvItem);
        return actionsMenu;
    }

    @FXML
    private void handleUpdateApplication(ApplicationJob application) {
        JobOffer jobOffer = applicationService.getJobOfferById(application.getJobOfferId());
        System.out.println(jobOffer);

        if (jobOffer.getNumberOfSpots() == 0) {
            showError("Sorry, there are no available spots for this job offer.");
            return;
        }

        // Get the date of application creation and the current time
        Timestamp applicationTimestamp = application.getDateApplication();
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        // Calculate the difference between the current time and the application's creation time
        long timeDifference = currentTimestamp.getTime() - applicationTimestamp.getTime();

        // Check if the application was created less than 2 hours ago (2 hours = 7200000 milliseconds)
        if (timeDifference > 7200000) {
            showError("Sorry, you can only update your application within 2 hours of submission.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Frontoffice/JobOfferApplicationUpdate.fxml"));
            VBox form = loader.load();

            JobOfferApplicationUpdateController controller = loader.getController();

            // Fetch the CV ID associated with the application
            Long cvId = application.getCvId();

            // Pass the job offer and CV ID to the controller
            controller.setJobOffer(jobOffer, cvId);

            Stage applicationFormStage = new Stage();
            applicationFormStage.setTitle("Job Application Form Update");
            applicationFormStage.initModality(Modality.APPLICATION_MODAL);
            applicationFormStage.setResizable(false);
            applicationFormStage.initStyle(javafx.stage.StageStyle.UNDECORATED);

            StackPane overlay = new StackPane();
            overlay.getChildren().add(form);

            Scene applicationFormScene = new Scene(overlay);
            applicationFormStage.setScene(applicationFormScene);
            applicationFormStage.showAndWait();
        } catch (IOException e) {
            showError("Error opening application form: " + e.getMessage());
        }
    }





    private void handleDeleteApplication(ApplicationJob application) {
        applicationService.delete(application);
            applicationListView.getItems().remove(application);
            showInfo("Application deleted successfully.");
    }


    private void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Error", message);
    }

    private void showInfo(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Information", message);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBackToJobOffers() {
        try {
            // Load the Job Offer List scene (content view)
            FXMLLoader jobOfferListLoader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Frontoffice/JobOfferList.fxml"));
            Parent jobOfferListView = jobOfferListLoader.load();

            // Load the FrontOffice (navbar) view
            FXMLLoader frontOfficeLoader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Frontoffice/main-frontoffice.fxml"));
            Parent frontOfficeView = frontOfficeLoader.load();
            FrontOfficeController frontOfficeController = frontOfficeLoader.getController();

            // Create a StackPane to hold both the FrontOffice navbar and the Job Offer List content
            StackPane root = new StackPane();

            // Add the FrontOffice navbar at the top (it will stay fixed)
            root.getChildren().add(frontOfficeView);

            // Add the JobOfferList content as the main content area (this goes beneath the navbar)
            root.getChildren().add(jobOfferListView);

            // Create a new Scene with the combined layout (FrontOffice + JobOffer List)
            Scene newScene = new Scene(root);
            newScene.getStylesheets().add(getClass().getResource("/org/example/pathfinder/view/Frontoffice/styles.css").toExternalForm());

            // Get the current stage (the window)
            Stage stage = (Stage) applicationListView.getScene().getWindow();
            // Set the new scene
            stage.setScene(newScene);
            stage.setMaximized(false); // Temporarily disable maximization
            stage.setMaximized(true);  // Re-enable maximization
            // Show the stage with the new scene
            stage.show();

        } catch (IOException e) {
            showError("Error opening job offers list: " + e.getMessage());
        }
    }

    @FXML
    private void handleShowCoverLetter(ApplicationJob application) {
        try {
            // Fetch the cover letter associated with the application
            CoverLetter coverLetter = coverLetterService.getCoverLetterByApplication(application.getApplicationId());

            if (coverLetter != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Frontoffice/CoverLetterView.fxml"));
                VBox showCoverLetterView = loader.load();

                CoverLetterViewController controller = loader.getController();
                controller.setCoverLetter(coverLetter);  // Pass the cover letter to the controller

                Stage coverLetterStage = new Stage();
                coverLetterStage.setTitle("Cover Letter");
                coverLetterStage.initModality(Modality.APPLICATION_MODAL);
                coverLetterStage.setResizable(false);
                coverLetterStage.initStyle(javafx.stage.StageStyle.UNDECORATED);

                Scene coverLetterScene = new Scene(showCoverLetterView);
                coverLetterStage.setScene(coverLetterScene);
                coverLetterStage.showAndWait();
            } else {
                showError("No cover letter found for this application.");
            }

        } catch (IOException e) {
            showError("Error displaying cover letter: " + e.getMessage());
        }
    }
}
