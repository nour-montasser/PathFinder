package org.example.pathfinder.Controller;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.UserCredentials;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.example.pathfinder.Model.*;
import org.example.pathfinder.Service.ApplicationService;
import org.example.pathfinder.Service.CoverLetterService;
import org.example.pathfinder.Service.JobOfferService;
import org.example.pathfinder.Service.UserService;

import java.io.*;

import java.net.URL;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class JobOfferApplicationListController {

    @FXML
    private Label jobOfferTitle;

    @FXML
    private Label jobOfferDescription;

    @FXML
    private Label requiredEducationLabel;

    @FXML
    private Label requiredExperienceLabel;

    @FXML
    private Label skillsLabel;
    @FXML
    private Label companyName,companyAddress,numberOfSpotsLabel;

    @FXML
    private ListView<ApplicationJob> applicationsListView;
    @FXML
    private ImageView searchIcon,companyImage;

    @FXML
    private ComboBox<String> statusFilterComboBox;

    @FXML
    private TextField searchField;

    private ApplicationService applicationJobService;
    private ObservableList<ApplicationJob> displayedApplications;

    private ObservableList<ApplicationJob> applicationsObservableList = FXCollections.observableArrayList();
    private JobOffer jobOffer;
    private ApplicationService applicationService = new ApplicationService();
    private JobOfferService jobOfferService = new JobOfferService();
    private CoverLetterService coverLetterService = new CoverLetterService();
    private UserService userService = new UserService();
    private final String loggedInUserEmail = LoggedUser.getInstance().getEmail();

    public void setJobOffer(JobOffer jobOffer) throws SQLException {
        this.jobOffer = jobOffer;
        System.out.println(jobOffer);

        // Set the search icon
        String imagePath = getClass().getResource("/org/example/pathfinder/view/Sources/pathfinder_logo_compass.png.png").toString();
        searchIcon.setImage(new Image(imagePath));

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
        companyName.setText(applicationJobService.getUserNameById(jobOffer.getIdUser()));  // assuming user profile contains the company name
        companyAddress.setText(jobOffer.getAddress());  // assuming user profile contains the address
        String url = applicationService.getUserProfilePicture(jobOffer.getIdUser());
        File imageFile = new File(url);
        if (imageFile.exists()) {
            companyImage.setImage(new Image(imageFile.toURI().toString()));
        }
        // Load applications for the job offer
        loadApplicationsForJobOffer();
    }
    // Helper method to create a styled TextFlow
    private TextFlow createStyledText(String boldText, String normalText) {
        Text boldPart = new Text(boldText);
        boldPart.setStyle("-fx-font-weight: bold; -fx-text-fill: #777777FF;");

        Text normalPart = new Text(normalText);
normalPart.setStyle("-fx-text-fill: #777777FF;");
        return new TextFlow(boldPart, normalPart);
    }

    private void loadApplicationsForJobOffer() throws SQLException {
        applicationsObservableList.clear();
        applicationsObservableList.addAll(applicationService.getApplicationsForJobOffer(jobOffer.getIdOffer()));
        applicationsListView.setItems(applicationsObservableList);

        applicationsListView.setCellFactory(param -> new ListCell<ApplicationJob>() {
            @Override
            protected void updateItem(ApplicationJob item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox card = new VBox(5);
                    card.setStyle("-fx-padding: 7; -fx-border-radius: 10; -fx-background-color: white; -fx-border-color: lightgray;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 2, 5); -fx-background-radius: 10;");

                    // Fetch profile picture
                    String profilePhotoPath = applicationService.getUserProfilePicture(item.getIdUser());

                    // Profile Picture
                    ImageView profileImageView = new ImageView();
                    if (profilePhotoPath != null && !profilePhotoPath.isEmpty()) {
                        try {
                            profileImageView.setImage(new Image(profilePhotoPath, 40, 40, true, true));
                        } catch (Exception e) {
                            profileImageView.setImage(getDefaultProfileImage());
                        }
                    } else {
                        profileImageView.setImage(getDefaultProfileImage());
                    }
                    profileImageView.setFitWidth(40);
                    profileImageView.setFitHeight(40);
                    profileImageView.setStyle("-fx-border-radius: 50%; -fx-background-radius: 50%;");

                    String userName = applicationService.getUserNameById(item.getIdUser());
                    Label userLabel = new Label(userName);
                    userLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

                    // MenuButton for actions
                    MenuButton actionsMenu = new MenuButton();
                    actionsMenu.setStyle("-fx-background-color: transparent; -fx-font-size: 16px;");
                    MenuItem showCoverLetterItem = new MenuItem("Show Cover Letter");
                    showCoverLetterItem.setOnAction(event -> handleShowCoverLetter(item));
                    MenuItem showCvItem = new MenuItem("Show CV");
                    actionsMenu.getItems().addAll(showCoverLetterItem, showCvItem);

                    // Align profile image, user name to the left, and menu to the right
                    HBox headerBox = new HBox(10);
                    HBox.setHgrow(userLabel, Priority.ALWAYS);
                    headerBox.setAlignment(Pos.CENTER_LEFT);
                    headerBox.getChildren().addAll(profileImageView, userLabel, actionsMenu);
                    headerBox.setSpacing(10);
                    headerBox.setStyle("-fx-alignment: center-left;");

                    Label statusLabel = new Label("Status: " + item.getStatus());
                    statusLabel.setStyle("-fx-text-fill: " + (item.getStatus().equals("Accepted") ? "#4CAF50" :
                            item.getStatus().equals("Rejected") ? "#f44336" : "#777") + ";");

                    Label dateLabel = new Label("Applied on: " + item.getDateApplication());
                    dateLabel.setStyle("-fx-text-fill: #777;");

                    HBox buttonBox = new HBox(5);
                    buttonBox.setStyle("-fx-alignment: center-right;");

                    if ("pending".equals(item.getStatus())) {
                        Button acceptButton = new Button("Accept");
                        acceptButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 12px;");
                        acceptButton.setOnAction(event -> handleStatusUpdate(item, "Accepted"));

                        Button rejectButton = new Button("Reject");
                        rejectButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 12px;");
                        rejectButton.setOnAction(event -> handleStatusUpdate(item, "Rejected"));

                        buttonBox.getChildren().addAll(acceptButton, rejectButton);
                    }

                    // Add everything to the card
                    card.getChildren().addAll(headerBox, statusLabel, dateLabel, buttonBox);

                    setText(null);
                    setGraphic(card);
                }
            }
        });
    }


    private Image getDefaultProfileImage() {
        URL resource = getClass().getResource("/org/example/pathfinder/view/Sources/default_profile.jpeg");
        if (resource != null) {
            return new Image(resource.toExternalForm(), 40, 40, true, true);
        }
        return new Image("https://via.placeholder.com/40"); // Fallback URL if resource is missing
    }


    private void handleStatusUpdate(ApplicationJob application, String newStatus) {
        if (!"pending".equals(application.getStatus())) {
            showAlert(Alert.AlertType.WARNING, "Status Update Not Allowed", null, "You can't change the status of this application again.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Update Status");
        alert.setHeaderText("Are you sure you want to update the status to " + newStatus + "?");
        alert.setContentText("This action will change the application's status to " + newStatus + ".");
        if (decrementJobOfferSpots()) {
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    application.setStatus(newStatus);
                    applicationService.update(application);  // Update status in the database
                    applicationsListView.refresh();  // Refresh UI

                    // Update the number of spots label
                    numberOfSpotsLabel.setText("");
                    numberOfSpotsLabel.setGraphic(createStyledText("Available spots: ", Integer.toString(jobOffer.getNumberOfSpots())));

                    // Get applicant's email (Assuming application.getUserEmail() retrieves the email)
                    String recipientEmail = userService.getUserById(application.getIdUser()).getEmail();
                    String subject = "Your Job Application Update";
                    String messageBody;

                    if ("Accepted".equals(newStatus)) {
                        // Open the Meet Schedule Dialog
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Frontoffice/ApplicationMeetSetup.fxml"));
                            Parent root = loader.load();
                            ApplicationMeetSetupController controller = loader.getController();

                            Stage stage = new Stage();
                            stage.setTitle("Schedule Google Meet");
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
                            stage.setScene(new Scene(root));
                            stage.showAndWait();

                            if (controller.isSubmitClicked()) {
                                LocalDate selectedDate = controller.getSelectedDate();
                                LocalTime selectedTime = controller.getSelectedTime();

                                // Generate Google Meet link
                                String googleMeetLink = generateGoogleMeetLink(recipientEmail, loggedInUserEmail, selectedDate, selectedTime);

                                // Open the Google Meet link in JxBrowser
                                controller.openMeetInDefaultBrowser(googleMeetLink);

                                messageBody = "Dear Applicant,\n\n" +
                                        "Congratulations! Your application has been accepted. We would like to invite you to a Google Meet interview.\n\n" +
                                        "Join the Google Meet interview using the link below:\n" +
                                        googleMeetLink + "\n\n" +
                                        "The interview is scheduled for " + selectedDate + " at " + selectedTime + ".\n\n" +
                                        "Best regards,\nPathfinder Team";
                            } else {
                                messageBody = "Dear Applicant,\n\nYour application has been accepted, but no Google Meet link was scheduled.\n\nBest regards,\nPathfinder Team";
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            messageBody = "Dear Applicant,\n\nYour application has been accepted, but there was an error scheduling the Google Meet.\n\nBest regards,\nPathfinder Team";
                        }
                    } else {
                        messageBody = "Dear Applicant,\n\nYour application has been " + newStatus + ".\n\nBest regards,\nPathfinder Team";
                    }

                    // Send email notification
                    sendEmail(recipientEmail, subject, messageBody);

                    showAlert(Alert.AlertType.INFORMATION, "Status Updated", null, "The application status has been updated to " + newStatus + ". An email notification has been sent to the user.");
                }
            });
        }
    }private String generateGoogleMeetLink(String attendeeEmail,String companyAttendeeEmail,  LocalDate date, LocalTime time) {
        try {
            GoogleCredentials credentials = getGoogleCredentials();

            // Initialize the Google Calendar API service
            Calendar calendarService = new Calendar.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName("Your Application Name")
                    .build();

            // Define the event details
            String summary = "Job Interview";
            String description = "Interview for the job position";

            // Set the start time based on user input
            Date startTime;
            if (date != null && time != null) {
                // Use the selected date and time
                startTime = java.sql.Timestamp.valueOf(date.atTime(time));
            } else {
                // Use the current time (if "Start Meeting Now" is selected)
                startTime = new Date(System.currentTimeMillis());
            }

            // Set the end time (1 hour after the start time)
            Date endTime = new Date(startTime.getTime() + 60 * 60 * 1000); // 1 hour duration

            // Create the event
            Event event = new Event()
                    .setSummary(summary)
                    .setDescription(description);

            EventDateTime start = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(startTime))
                    .setTimeZone(TimeZone.getDefault().getID());
            event.setStart(start);

            EventDateTime end = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(endTime))
                    .setTimeZone(TimeZone.getDefault().getID());
            event.setEnd(end);

            EventAttendee[] attendees = new EventAttendee[] {
                    new EventAttendee().setEmail(attendeeEmail),
                    new EventAttendee().setEmail(companyAttendeeEmail)
            };
            event.setAttendees(Arrays.asList(attendees));

            // Set up reminders (optional)
            EventReminder[] reminderOverrides = new EventReminder[] {
                    new EventReminder().setMethod("email").setMinutes(24 * 60),
                    new EventReminder().setMethod("popup").setMinutes(10),
            };
            Event.Reminders reminders = new Event.Reminders()
                    .setUseDefault(false)
                    .setOverrides(Arrays.asList(reminderOverrides));
            event.setReminders(reminders);

            // Create the event with a Google Meet link
            event.setConferenceData(new ConferenceData()
                    .setCreateRequest(new CreateConferenceRequest()
                            .setRequestId(UUID.randomUUID().toString())));

            // Insert the event into the calendar
            Event createdEvent = calendarService.events().insert("primary", event)
                    .setConferenceDataVersion(1)
                    .execute();

            // Return the Google Meet link
            return createdEvent.getHangoutLink();
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback link in case of an error
            return "https://meet.google.com/abc-defg-hij";
        }
    }private GoogleCredentials getGoogleCredentials() throws IOException, GeneralSecurityException {
        // Load client secrets
        InputStream in = new FileInputStream("C:\\Users\\nourm\\Documents\\esprit\\3eme\\Project\\PathFinder\\src\\main\\resources\\org\\example\\pathfinder\\view\\Sources\\credentials.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), new InputStreamReader(in));

        // Build the authorization flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                clientSecrets,
                Collections.singleton(CalendarScopes.CALENDAR))
                .setDataStoreFactory(new FileDataStoreFactory(new File("tokens")))
                .setAccessType("offline") // Request a refresh token
                .build();

        // Use LocalServerReceiver for auto-auth
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        // Convert Credential to UserCredentials
        return UserCredentials.newBuilder()
                .setClientId(clientSecrets.getDetails().getClientId())
                .setClientSecret(clientSecrets.getDetails().getClientSecret())
                .setRefreshToken(credential.getRefreshToken()) // Set the refresh token
                .build();
    }
    private Boolean decrementJobOfferSpots() {
        int currentSpots = jobOffer.getNumberOfSpots();
        if (currentSpots > 0) {
            jobOffer.setNumberOfSpots(currentSpots - 1);
            jobOfferService.update(jobOffer);  // Assuming updateJobOffer() method in ApplicationService to update the job offer in DB
            return  true;
        } else {
            showAlert(Alert.AlertType.WARNING, "No Available Spots", null, "The job offer has no available spots left.");
            return false;
        }
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

    private void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Error", null, message);
    }

    private void showInfo(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Information", null, message);
    }

    @FXML
    public void initialize() {
        applicationJobService = new ApplicationService();
        displayedApplications = FXCollections.observableArrayList();

        statusFilterComboBox.setItems(FXCollections.observableArrayList("All", "Pending", "Accepted", "Rejected"));
        statusFilterComboBox.getSelectionModel().select("All");
        statusFilterComboBox.setOnAction(event -> loadApplications(statusFilterComboBox.getSelectionModel().getSelectedItem()));
        loadApplications("All");

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterApplicationsBySearch(newValue);
        });
    }

    private void filterApplicationsBySearch(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            applicationsListView.setItems(applicationsObservableList);
        } else {
            String lowerCaseSearch = searchText.toLowerCase();
            ObservableList<ApplicationJob> filteredList = applicationsObservableList.filtered(applicationJob ->
                    applicationService.getUserNameById(applicationJob.getIdUser()).toLowerCase().contains(lowerCaseSearch)
            );

            applicationsListView.setItems(filteredList);
        }
    }


    private void loadApplications(String status) {
        List<ApplicationJob> applications = applicationJobService.getByStatus(status);
        displayedApplications.setAll(applications);
        applicationsListView.setItems(displayedApplications);
    }

    public static void sendEmail(String recipientEmail, String subject, String messageBody) {
        final String senderEmail = "nourmo49@gmail.com";  // Your Gmail address
        final String senderPassword = "bfxp ccty rkea tkqd";  // Your Gmail app password (if 2FA enabled)

        try {
            // Create an instance of the HtmlEmail class (Apache Commons Email)
            HtmlEmail email = new HtmlEmail();

            // Set SMTP server properties
            email.setHostName("smtp.gmail.com");
            email.setSmtpPort(587);
            email.setAuthentication(senderEmail, senderPassword);
            email.setStartTLSEnabled(true);

            // Set the from address, to address, subject, and message body
            email.setFrom(senderEmail);
            email.addTo(recipientEmail);
            email.setSubject(subject);
            email.setMsg(messageBody);

            // Send the email
            email.send();
            System.out.println("Email sent successfully to " + recipientEmail);

        } catch (EmailException e) {
            e.printStackTrace();
            System.err.println("Error sending email: " + e.getMessage());
        }
    }
}