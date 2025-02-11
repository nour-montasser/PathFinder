package tn.esprit.demo.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import tn.esprit.demo.Modele.User;
import javafx.scene.text.Text;
import tn.esprit.demo.Service.ServiceUser;

import java.io.IOException;
import java.sql.SQLException;

public class ProfileController {

    @FXML
    private Text username;

    private final ServiceUser userService = new ServiceUser();

    @FXML
    void goHome(ActionEvent event) {
        try {
            // Load the login.fxml (login page)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/demo/Front/Seeker/FrontHomeSeeker.fxml"));
            Parent loginParent = loader.load(); // Load the login interface

            // Create a new stage for the login window
            Stage loginStage = new Stage();
            Scene loginScene = new Scene(loginParent);
            loginStage.setTitle("Login"); // Title for the login window
            Object controller = loader.getController();
            ((FrontHomeSeekerController) controller).initializeUser(userService.getUserByEmail(username.getText()));

            // Set the scene and show the login window
            loginStage.setScene(loginScene);
            loginStage.show();

            // Optionally, you can close the current registration window after opening the login window
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close(); // Close the current registration window

        } catch (IOException e) {
            e.printStackTrace(); // Handle the IOException (e.g., file not found or issue with loading)
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void navigateToDashboard(MouseEvent event, User user) throws IOException {
        String dashboardPath = user.getRole().equals("COMPANY")
                ? "/tn/esprit/demo/Front/Company/FrontHomeCompany.fxml"
                : "/tn/esprit/demo/Front/Seeker/FrontHomeSeeker.fxml";

        FXMLLoader loader = new FXMLLoader(getClass().getResource(dashboardPath));
        Parent dashboardParent = loader.load();
        Scene dashboardScene = new Scene(dashboardParent);

        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setScene(dashboardScene);
        currentStage.show();
    }

    @FXML
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


    @FXML
    public void deleteButtonOnClick(ActionEvent actionEvent) {
    }

    @FXML
    public void submitButtonOnClick(ActionEvent actionEvent) {
    }

    @FXML
    public void changePasswordButtonOnClick(ActionEvent actionEvent) {

    }

    public void initializeUser(User user) {
        username.setText(user.getEmail());
    }
}
