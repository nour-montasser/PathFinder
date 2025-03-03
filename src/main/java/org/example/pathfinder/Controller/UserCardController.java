package org.example.pathfinder.Controller;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.pathfinder.Model.User;
import org.example.pathfinder.Service.UserService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

public class UserCardController {

    @FXML
    private Text usernameEmail;

    @FXML
    private Text userFullName;

    @FXML
    private Text userRole;


    @FXML
    private ImageView userImage;

    @FXML
    private Button delete;

    @FXML
    private Button changepassword1;


    private User currentUser;

    private final UserService userService = new UserService();

    public void setUserData(User user, User currentUser) {
        usernameEmail.setText(user.getEmail());
        userFullName.setText(user.getName());
        userRole.setText(user.getRole());
        this.currentUser = currentUser;

        if (user.getImage() != null && !user.getImage().isEmpty())
            loadProfilePhoto(user.getImage());

    }

    private void loadProfilePhoto(String imagePath) {
        try {
            System.out.println(imagePath);
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Image profileImage = new Image(imageFile.toURI().toString());
                userImage.setImage(profileImage);
            }
        } catch (Exception e) {
            System.err.println("Error loading profile image: " + e.getMessage());
        }
    }



    public void onDetailsClicked(javafx.event.ActionEvent actionEvent) {
        try {
            // Load the profile.fxml (profile page)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Backoffice/BackUserInformation.fxml"));
            Parent profileParent = loader.load(); // Load the profile interface

            // Create a new stage for the profile window
            Stage profileStage = new Stage();
            Scene profileScene = new Scene(profileParent);
            profileStage.setTitle("Profile"); // Title for the profile window
            Object controller = loader.getController();
            ((BackUserInformationController) controller).initializeUser(
                    userService.getUserByEmail(usernameEmail.getText()),
                    currentUser);

            // Set the scene and show the profile window
            profileStage.setScene(profileScene);
            profileStage.show();

            // Optionally, you can close the current registration window after opening the profile window
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            currentStage.close(); // Close the current registration window

        } catch (IOException e) {
            e.printStackTrace(); // Handle the IOException (e.g., file not found or issue with loading)
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    void printUser(javafx.event.ActionEvent event) {
        try {
            // Open a file chooser dialog to let the user choose where to save the PDF
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File selectedFile = fileChooser.showSaveDialog(new Stage());

            if (selectedFile != null) {
                // Call the method to generate the PDF at the selected location
                printPDFDocument(selectedFile.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void printPDFDocument(String filePath) {
        try (PdfWriter writer = new PdfWriter(filePath);
             PdfDocument pdfDoc = new PdfDocument(writer)) {

            Document document = new Document(pdfDoc);


            Paragraph title = new Paragraph("User Information")
                    .setFontSize(16)
                    .setBold();
            document.add(title);

            // Add user information to the document
            document.add(new Paragraph("Username/Email: " + usernameEmail.getText()));
            document.add(new Paragraph("Full Name: " + userFullName.getText()));
            document.add(new Paragraph("Role: " + userRole.getText()));

            // Close the document
            document.close();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
