package org.example.pathfinder.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.pathfinder.Model.User;
import org.example.pathfinder.Service.UserService;

import java.io.IOException;
import java.sql.SQLException;

public class BackHomeController {

    @FXML
    private Button HomeButton;

    @FXML
    private Button Profile;

    @FXML
    private Button Profile1;

    @FXML
    private Label averageWorkoutDuration;

    @FXML
    private Button btn_workbench111111;

    @FXML
    private Label currentUserField;

    @FXML
    private Label dailyAdvicesText;

    @FXML
    private Label descriptionReclamation;

    @FXML
    private Label descriptionReclamation1;

    @FXML
    private Label idU;

    @FXML
    private Pane l;

    @FXML
    private Label longestWorkoutDuration;

    @FXML
    private Label nbReclamationText;

    @FXML
    private Pane pane_1111;

    @FXML
    private Pane pane_11111;

    @FXML
    private Pane pane_111111;

    @FXML
    private Pane pane_11112;

    @FXML
    private Pane pane_132;

    @FXML
    private HBox root;

    @FXML
    private AnchorPane side_ankerpane;

    @FXML
    private Label titreReclamationText;

    @FXML
    private Text userName;

    @FXML
    private Label userNameLabel;

    @FXML
    private ImageView userPhoto;


    @FXML
    private Label totalAdminsText;

    @FXML
    private Label totalCompanyText;

    @FXML
    private Label totalSeekersText;

    @FXML
    private Label totalUsersText;

    private final UserService userService = new UserService();


    @FXML
    public void logOut(ActionEvent actionEvent) {
        try {
            // Load the login.fxml (login page)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Authentification/login.fxml"));
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
    public void openProfile(ActionEvent actionEvent) {
        try {
            // Load the profile.fxml (profile page)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Dashboard/Back/BackProfile.fxml"));
            Parent profileParent = loader.load(); // Load the profile interface
            User currentUser = userService.getUserByEmail(userName.getText());
            // Create a new stage for the profile window
            Stage profileStage = new Stage();
            Scene profileScene = new Scene(profileParent);
            profileStage.setTitle("Profile"); // Title for the profile window
            Object controller = loader.getController();
            ((BackUserInformationController) controller).initializeUser(
                    currentUser,
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
    void openUsers(ActionEvent event) {
        try {
            // Load the profile.fxml (profile page)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Dashboard/Back/BackUserList.fxml"));
            Parent profileParent = loader.load(); // Load the profile interface

            // Create a new stage for the profile window
            Stage profileStage = new Stage();
            Scene profileScene = new Scene(profileParent);
            profileStage.setTitle("Users List"); // Title for the profile window
            Object controller = loader.getController();
            ((BackUserController) controller).initializeUser(userService.getUserByEmail(userName.getText()));

            // Set the scene and show the profile window
            profileStage.setScene(profileScene);
            profileStage.show();

            // Optionally, you can close the current registration window after opening the profile window
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close(); // Close the current registration window

        } catch (IOException e) {
            e.printStackTrace(); // Handle the IOException (e.g., file not found or issue with loading)
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void initializeUser(User user) {
        userName.setText(user.getEmail());

        int totalUsers = userService.getNbOfUsers();
        int companyUsers = userService.getNbOfUsersWithRole(1);
        int seekerUsers = userService.getNbOfUsersWithRole(2);
        int adminUsers = userService.getNbOfUsersWithRole(3);

        String adminStr = adminUsers>1?" Admins":" Admin";
        String companyStr = companyUsers>1?" Companies":" Company";
        String seekersStr = seekerUsers>1?" Seekers":" Seeker";
        String usersStr = totalUsers>1?" Users":" User";

        totalAdminsText.setText(adminUsers + adminStr);
        totalCompanyText.setText(companyUsers + companyStr);
        totalSeekersText.setText(seekerUsers + seekersStr);
        totalUsersText.setText(totalUsers + usersStr);
    }
    public void goUsers(ActionEvent actionEvent) {
        try {
            // Load the login.fxml (login page)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Dashboard/Back/BackUserList.fxml"));
            Parent loginParent = loader.load(); // Load the login interface

            // Create a new stage for the login window
            Stage loginStage = new Stage();
            Scene loginScene = new Scene(loginParent);
            loginStage.setTitle("Login"); // Title for the login window
            Object controller = loader.getController();
            ((BackUserController) controller).initializeUser(userService.getUserByEmail(userName.getText()));

            // Set the scene and show the login window
            loginStage.setScene(loginScene);
            loginStage.show();

            // Optionally, you can close the current registration window after opening the login window
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            currentStage.close(); // Close the current registration window

        } catch (IOException e) {
            e.printStackTrace(); // Handle the IOException (e.g., file not found or issue with loading)
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void goHome(ActionEvent actionEvent) {
        try {
            // Load the login.fxml (login page)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Dashboard/Back/BackHome.fxml"));
            Parent loginParent = loader.load(); // Load the login interface

            // Create a new stage for the login window
            Stage loginStage = new Stage();
            Scene loginScene = new Scene(loginParent);
            loginStage.setTitle("Login"); // Title for the login window
            Object controller = loader.getController();
            ((BackHomeController) controller).initializeUser(userService.getUserByEmail(userName.getText()));

            // Set the scene and show the login window
            loginStage.setScene(loginScene);
            loginStage.show();

            // Optionally, you can close the current registration window after opening the login window
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            currentStage.close(); // Close the current registration window

        } catch (IOException e) {
            e.printStackTrace(); // Handle the IOException (e.g., file not found or issue with loading)
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
