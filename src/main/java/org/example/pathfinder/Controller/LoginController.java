package org.example.pathfinder.Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.pathfinder.Model.User;
import org.example.pathfinder.Service.UserService;

public class LoginController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField EmailAddressText;

    @FXML
    private PasswordField passwordText;
    private final UserService userService = new UserService();

    // Handles the event when the user clicks "Register" to switch to the registration screen
    @FXML
    void registerChange(MouseEvent event) {
        // Switch to registration page (could be done via loading a new scene or setting the visible state)
        try {
            // Load the login.fxml (login page)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Authentification/register.fxml"));
            Parent registerParent = loader.load(); // Load the login interface

            // Create a new stage for the login window
            Stage registerStage = new Stage();
            Scene registerScene = new Scene(registerParent);
            registerStage.setTitle("Login"); // Title for the login window

            // Set the scene and show the login window
            registerStage.setScene(registerScene);
            registerStage.show();

            // Optionally, you can close the current registration window after opening the login window
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close(); // Close the current registration window

        } catch (IOException e) {
            e.printStackTrace(); // Handle the IOException (e.g., file not found or issue with loading)
        }
    }

    // Handles the event when the user clicks "Sign In"
    @FXML
    void signInClicked(MouseEvent event) {
        String email = EmailAddressText.getText().trim();
        String password = passwordText.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter both email and password.");
            return;
        }

        try {
            User loggedInUser = userService.authenticateUser(email, password);
            if (loggedInUser != null) {
                System.out.println("User logged in successfully: " + loggedInUser.getName());
                navigateToDashboard(event, loggedInUser);
            } else {
                showAlert("Error", "Invalid credentials. Please try again.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Navigate to dashboard based on user role
    private void navigateToDashboard(MouseEvent event, User user) throws IOException {
        String dashboardPath = "";

        if (user.getRole().equals("COMPANY")) {
            dashboardPath = "/org/example/pathfinder/view/Dashboard/Front/FrontHomeSeeker.fxml";
        } else if (user.getRole().equals("SEEKER")) {
            dashboardPath = "/org/example/pathfinder/view/Dashboard/Front/FrontHomeSeeker.fxml";
        } else {
            dashboardPath = "/org/example/pathfinder/view/Dashboard/Back/BackHome.fxml";
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(dashboardPath));
        Parent dashboardParent = loader.load();

        Object controller = loader.getController();

        // Call a method in the new controller (Ensure that method exists in both controllers)
        if (user.getRole().equals("COMPANY")) {
            ((FrontHomeSeekerController) controller).initializeUser(user);
        } else if (user.getRole().equals("SEEKER")) {
            ((FrontHomeSeekerController) controller).initializeUser(user);
        } else {
            ((BackHomeController) controller).initializeUser(user);
        }

        Scene dashboardScene = new Scene(dashboardParent);

        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setScene(dashboardScene);
        currentStage.show();
    }

    // Show alert messages
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Initialize method to ensure FXML elements are properly injected
    @FXML
    void initialize() {
        assert EmailAddressText != null : "fx:id=\"EmailAddressText\" was not injected: check your FXML file 'login.fxml'.";
        assert passwordText != null : "fx:id=\"passwordText\" was not injected: check your FXML file 'login.fxml'.";
    }
}