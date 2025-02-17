package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.pathfinder.Model.CoverLetter;

public class CoverLetterViewController {

    @FXML
    private Label coverLetterContent;
    @FXML
    private Label coverLetterSubject;

    // Set the cover letter content to be displayed
    public void setCoverLetter(CoverLetter coverLetter) {
        coverLetterSubject.setText(coverLetter.getSubject());
        coverLetterContent.setText(coverLetter.getContent()); // Assuming `getContent()` is the method to fetch the cover letter content
    }

    // Close the window when the "Close" button is clicked
    @FXML
    private void handleCloseButtonClick() {
        // Close the window
        ((Stage) coverLetterContent.getScene().getWindow()).close();
    }
}
