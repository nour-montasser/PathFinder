package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.pathfinder.Model.User;
import org.example.pathfinder.Service.UserService;

import java.awt.event.ActionEvent;
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

    }



    public void onDetailsClicked(javafx.event.ActionEvent actionEvent) {
        try {
            // Load the profile.fxml (profile page)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Dashboard/Back/BackUserInformation.fxml"));
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
}
