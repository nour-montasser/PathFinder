package org.example.pathfinder.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
import org.example.pathfinder.Service.ApplicationService;
import org.example.pathfinder.Model.JobOffer;
import org.example.pathfinder.Service.CoverLetterService;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class ApplicationListController {

    @FXML
    private ListView<ApplicationJob> applicationListView;
    @FXML
    private ImageView searchIcon;
    private ApplicationService applicationService;

    private CoverLetterService coverLetterService;

    @FXML
    public void initialize() {
        applicationService = new ApplicationService();
        String imagePath = getClass().getResource("/org/example/pathfinder/Sources/pathfinder_logo_compass.png").toString();
        searchIcon.setImage(new Image(imagePath));
        coverLetterService = new CoverLetterService();
        applicationListView.setItems(getApplicationsForUser());
        applicationListView.setCellFactory(param -> createApplicationCell());
    }

    private ObservableList<ApplicationJob> getApplicationsForUser() {
        ObservableList<ApplicationJob> applications = FXCollections.observableArrayList();
        try {
            List<ApplicationJob> applicationList = applicationService.getApplicationsForUser(1L);  // Hardcoded user ID
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

            MenuItem showCoverLetterItem = new MenuItem("Show Cover Letter");
            showCoverLetterItem.setOnAction(event -> handleShowCoverLetter(application));
            MenuItem showCvItem = new MenuItem("Show Cv");

            actionsMenu.getItems().addAll(deleteItem, updateItem, showCoverLetterItem,showCvItem);
        }
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/View/JobOfferApplicationUpdate.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/View/JobOfferList.fxml"));
            Parent jobOfferListView = loader.load();
            Stage stage = (Stage) applicationListView.getScene().getWindow();
            Scene jobOfferScene = new Scene(jobOfferListView);
            stage.setScene(jobOfferScene);
            stage.setMaximized(false);
            stage.setMaximized(true);
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/View/CoverLetterView.fxml"));
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
