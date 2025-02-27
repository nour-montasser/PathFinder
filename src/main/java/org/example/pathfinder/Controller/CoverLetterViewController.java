package org.example.pathfinder.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.pathfinder.Model.CoverLetter;

import java.io.IOException;

public class CoverLetterViewController {

    @FXML
    private Label coverLetterContent;
    @FXML
    private Label coverLetterSubject;

    private long id;

    // Set the cover letter content to be displayed
    public void setCoverLetter(CoverLetter coverLetter) {
        coverLetterSubject.setText(coverLetter.getSubject());
        coverLetterContent.setText(coverLetter.getContent());
        id=coverLetter.getIdCoverLetter();// Assuming `getContent()` is the method to fetch the cover letter content
    }

    // Close the window when the "Close" button is clicked
    @FXML
    private void handleCloseButtonClick() {
        // Close the window
        ((Stage) coverLetterContent.getScene().getWindow()).close();
    }


    @FXML
    private void handleGeneratePdfButtonClick(ActionEvent event) {
        try {
            // Load the FXML file for CoverLetterTemplates
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Frontoffice/CoverLetterTemplates.fxml"));
            Parent root = loader.load();

            // Get the controller from the FXMLLoader
            CoverLetterTemplatesController controller = loader.getController();
            controller.setSubjectAndContent(coverLetterSubject.getText(), coverLetterContent.getText(),id);

            // Create a new stage (window) to display the template selection
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Choose a Template");

            // Make the new stage modal (blocking the interaction with other windows)
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            // Optional: Set a different style (optional based on your requirement)
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);   // More standard style for popups
            stage.showAndWait();  // Show the new window and wait for it to close

        } catch (IOException e) {
            e.printStackTrace(); // Log the error
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load template selection.");
        }
    }

    // Utility method to show alerts
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
