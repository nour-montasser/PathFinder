package tn.esprit.demo.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.esprit.demo.Modele.User;

import java.io.IOException;

public class FrontHomeCompanyController {
    public void openProfile(ActionEvent actionEvent) {
    }

    public void logOut(ActionEvent actionEvent) {
        try {
            // Load the login.fxml (login page)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/demo/login.fxml"));
            Parent loginParent = loader.load(); // Load the login interface

            // Create a new stage for the login window
            Stage loginStage = new Stage();
            Scene loginScene = new Scene(loginParent);
            loginStage.setTitle("Login"); // Title for the login window

            // Set the scene and show the login window
            loginStage.setScene(loginScene);
            loginStage.show();

            // Optionally, you can close the current registration window after opening the login window
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            currentStage.close(); // Close the current registration window

        } catch (IOException e) {
            e.printStackTrace(); // Handle the IOException (e.g., file not found or issue with loading)
        }
    }

    public void initializeUser(User user) {
    }
}
