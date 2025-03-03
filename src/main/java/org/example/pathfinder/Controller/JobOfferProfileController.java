package org.example.pathfinder.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.pathfinder.Model.ApplicationJob;
import org.example.pathfinder.Model.JobOffer;
import org.example.pathfinder.Model.LoggedUser;
import org.example.pathfinder.Model.User;
import org.example.pathfinder.Service.ApplicationService;
import org.example.pathfinder.Service.CoverLetterService;
import org.example.pathfinder.Service.JobOfferService;
import org.example.pathfinder.Service.UserService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class JobOfferProfileController {
    @FXML
    private Label cvIdLabel;
    @FXML
    private HBox jobOffersHBox;
    @FXML
    private Label applicationTimeLabel;
    @FXML
    private Label coverLetterSubjectLabel;
    @FXML
    private VBox applicationCard;
    @FXML
    private GridPane jobOffersGrid;

    @FXML
    private Label jobOfferTitle;

    @FXML
    private Label jobOfferDescription;

    @FXML
    private Label requiredEducationLabel;
    @FXML
    private ImageView searchIcon;
    @FXML
    private Label requiredExperienceLabel;

    @FXML
    private Label skillsLabel;

    @FXML
    private Label companyName;

    @FXML
    private Label companyAddress;

    @FXML
    private Label numberOfSpotsLabel;

    @FXML
    private ImageView companyImage;

    @FXML
    private Button closeButton;

    @FXML
    private Button applyButton;

    @FXML
    private Label myApplicationLabel;

    private ObservableList<ApplicationJob> applicationsObservableList = FXCollections.observableArrayList();
    private JobOffer jobOffer;
    private ApplicationService applicationService = new ApplicationService();
    private JobOfferService jobOfferService = new JobOfferService();
    private CoverLetterService coverLetterService = new CoverLetterService();
    private UserService userService = new UserService();
    private final String loggedInUserEmail = LoggedUser.getInstance().getEmail();
    private JobOffer selectedJobOffer;

    @FXML
    public void initialize() throws SQLException {


    }

    public void setJobOffer(JobOffer jobOffer) throws SQLException {
        this.jobOffer = jobOffer;
        System.out.println(jobOffer);
      //  loadCompanyJobOffers();
      /*  String imagePath = getClass().getResource("/org/example/pathfinder/view/Sources/pathfinder_logo_compass.png").toString();
        searchIcon.setImage(new Image(imagePath));*/
        // Fetch the company data from the user profile
        User companyUser = userService.getUserById(jobOffer.getIdUser());  // Assume jobOffer has a reference to the user ID

        // Set job offer details
        jobOfferTitle.setText(jobOffer.getTitle());
        jobOfferDescription.setText(jobOffer.getDescription());

        // Set the required details with labels and style them
        requiredEducationLabel.setText("");
        requiredEducationLabel.setGraphic(createStyledText("Required Education: ", jobOffer.getRequiredEducation()));

        requiredExperienceLabel.setText("");
        requiredExperienceLabel.setGraphic(createStyledText("Required Experience: ", jobOffer.getRequiredExperience()));

        skillsLabel.setText("");
        skillsLabel.setGraphic(createStyledText("Skills: ", jobOffer.getSkills()));

        numberOfSpotsLabel.setText("");
        numberOfSpotsLabel.setGraphic(createStyledText("Available spots: ",Integer.toString(jobOffer.getNumberOfSpots())));

        // Set company info
        companyName.setText(applicationService.getUserNameById(jobOffer.getIdUser()));  // assuming user profile contains the company name
        companyAddress.setText(jobOffer.getAddress());  // assuming user profile contains the address
        String url = applicationService.getUserProfilePicture(jobOffer.getIdUser());
        File imageFile = new File(url);
        if (imageFile.exists()) {
            companyImage.setImage(new Image(imageFile.toURI().toString()));
        }
        if(applicationService.hasUserAppliedForJob(jobOffer, LoggedUser.getInstance().getUserId())){
            applyButton.setVisible(false);
            ApplicationJob applicationJob = applicationService.getApplicationByJobOfferAndUser(jobOffer.getIdOffer(), LoggedUser.getInstance().getUserId());
            cvIdLabel.setText("CV ID: " + applicationJob.getCvId());
            applicationTimeLabel.setText("Applied on: " + applicationJob.getDateApplication());
            coverLetterSubjectLabel.setText("Cover Letter Subject: " + coverLetterService.getCoverLetterByApplication(applicationJob.getApplicationId()).getSubject());
        }else{
            applicationCard.setVisible(false);
            myApplicationLabel.setVisible(false);
        }


    }

    // Helper method to create a styled TextFlow
    private TextFlow createStyledText(String boldText, String normalText) {
        Text boldPart = new Text(boldText);
        boldPart.setStyle("-fx-font-weight: bold; -fx-text-fill: #777777FF;");

        Text normalPart = new Text(normalText);
        normalPart.setStyle("-fx-text-fill: #777777FF;");
        return new TextFlow(boldPart, normalPart);
    }
    @FXML
    private void handleClose() {
        try {
            // Charger le layout principal (navbar + contentArea)
            FXMLLoader frontOfficeLoader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Frontoffice/main-frontoffice.fxml"));
            Parent frontOfficeView = frontOfficeLoader.load();
            FrontOfficeController frontOfficeController = frontOfficeLoader.getController();

            // Charger la page des offres d'emploi et l'injecter dans le contentArea du FrontOffice
            Parent jobOfferListView = FXMLLoader.load(getClass().getResource("/org/example/pathfinder/view/Frontoffice/JobOfferList.fxml"));
            frontOfficeController.loadView(jobOfferListView); // Fonction à ajouter dans FrontOfficeController

            // Obtenir la fenêtre actuelle (Stage)
            Stage stage = (Stage) jobOfferTitle.getScene().getWindow();

            // Recréer la scène avec le nouveau contenu
            Scene newScene = new Scene(frontOfficeView);
            // newScene.getStylesheets().add(getClass().getResource("/org/example/pathfinder/view/Frontoffice/styles.css").toExternalForm());

            // Appliquer la nouvelle scène et forcer le redimensionnement
            stage.setScene(newScene);
            stage.setMaximized(false);
            stage.setMaximized(true);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load the Job Offer List", e.getMessage());
        }

    }
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void handleApply() {
        // Get the logged user ID and role
        String loggedUserRole = LoggedUser.getInstance().getRole();
        long loggedUserId = LoggedUser.getInstance().getUserId();  // Assuming you have a method to get user ID

        // Check if the logged user is a seeker
        if (!loggedUserRole.equals("SEEKER")) {
            showError("You do not have permission to apply for jobs.");
            return;  // Prevent further processing
        }

        // Check if the user has already applied for the job
        if (applicationService.hasUserAppliedForJob(jobOffer, loggedUserId)) {
            showError("You have already applied for this job.");
            return;  // Exit the method to prevent opening the application form
        }

        // Check if the number of spots is zero
        if (jobOffer.getNumberOfSpots() == 0) {
            showError("Sorry, there are no available spots for this job offer.");
            return;  // Exit the method to prevent opening the application form
        }

        // Existing apply logic here
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Frontoffice/JobOfferApplicationForm.fxml"));
            VBox form = loader.load();

            JobOfferApplicationFormController controller = loader.getController();
            controller.setJobOffer(jobOffer); // Pass the selected job offer

            Stage applicationFormStage = new Stage();
            applicationFormStage.setTitle("Job Application Form");
            applicationFormStage.initModality(Modality.APPLICATION_MODAL);  // Make it modal
            applicationFormStage.setResizable(false);
            applicationFormStage.initStyle(javafx.stage.StageStyle.UNDECORATED);

            StackPane overlay = new StackPane();
            overlay.getChildren().add(form);

            Scene applicationFormScene = new Scene(overlay);
            applicationFormStage.setScene(applicationFormScene);
            applicationFormStage.showAndWait();
        } catch (IOException e) {
            System.out.println("error opening form" + e.getMessage());
        }


    }
    private void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Error", null, message);
    }


   /* private void loadCompanyJobOffers() {
        jobOffersGrid.getChildren().clear();  // Clear existing cards

        List<JobOffer> companyJobOffers = jobOfferService.getByUserId(jobOffer.getIdUser()); // Load job offers for the specific company

        int columns = 2;  // Number of columns
        int row = 0;
        int col = 0;

        for (JobOffer jobOffer : companyJobOffers) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Frontoffice/JobOfferListCard.fxml"));
                VBox card = loader.load();

                JobOfferListCardController controller = loader.getController();
                controller.setJobOffer(jobOffer);
                controller.setParentController(this);  // Pass the parent controller

                // Set fixed size for the card
                card.setPrefSize(200, 150);


                // Ensure the card is visible
                card.setStyle("-fx-background-color: white; -fx-padding: 10;");

                // Add the card to the GridPane
                jobOffersGrid.add(card, col, row);
                GridPane.setMargin(card, new javafx.geometry.Insets(10));  // Add spacing

                col++;
                if (col == columns) {
                    col = 0;
                    row++;
                }

                // Debugging: Print card details
                System.out.println("Added card for job offer: " + jobOffer.getTitle());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/
}