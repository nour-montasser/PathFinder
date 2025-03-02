package org.example.pathfinder.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.layout.Pane;
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

public class RegisterController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField EmailAddressText;


    @FXML
    private Pane SeekerPane;

    @FXML
    private Pane companyPane;

    @FXML
    private PasswordField confirmPasswordText;

    @FXML
    private TextField fullNameText;

    @FXML
    private PasswordField passwordText;

    private UserService serviceUser;

    private int chosenRole = -1;

    // Handles the sign-up click event
    @FXML
    void signUpClicked(MouseEvent event) {
        String email = EmailAddressText.getText();
        String fullName = fullNameText.getText();
        String password = passwordText.getText();
        String confirmPassword = confirmPasswordText.getText();

        if (!isValidEmail(email)) {
            showAlert("Erreur", "Veuillez entrer une adresse e-mail valide !");
            return;
        }
        // Check if email is unique
        if (serviceUser.isEmailTaken(email)) {
            showAlert("Erreur", "L'adresse e-mail est déjà utilisée !");
            return;
        }

        // Validate password complexity
        if (!isValidPassword(password)) {
            showAlert("Erreur", "Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et un symbole !");
            return;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            showAlert("Erreur", "Les mots de passe ne correspondent pas !");
            return;
        }

        // Validate role selection
        String role = "SEEKER";
        if (chosenRole == -1) {
            showAlert("Erreur", "Veuillez choisir un rôle !");
            return;
        } else if (chosenRole == 1) {
            role = "COMPANY";
        } else if (chosenRole == 2) {
            role = "SEEKER";
        }

        // Create a new User object
        User newUser = new User(0, fullName, email, role, password, null);

        // Register user
        serviceUser.addUser(newUser);
        showAlert("Succès", "Utilisateur enregistré avec succès !");
    }

    // Password validation method
    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        return email.matches(emailRegex);
    }


    // Méthode pour afficher une alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Other event handlers (login, role selection) can be implemented as needed
    @FXML
    void loginChange(MouseEvent event) {
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
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close(); // Close the current registration window

        } catch (IOException e) {
            e.printStackTrace(); // Handle the IOException (e.g., file not found or issue with loading)
        }
    }

    @FXML
    void roleChosenCompany(MouseEvent event) {
        // Set role as COMPANY (update UI to reflect the choice)

        companyPane.setStyle("-fx-background-color: #b49474;");
        SeekerPane.setStyle("-fx-background-color: black;");
        chosenRole = 1;

    }

    @FXML
    void roleChosenSeeker(MouseEvent event) {
        // Set role as SEEKER (update UI to reflect the choice)
        SeekerPane.setStyle("-fx-background-color: #b49474;");
        companyPane.setStyle("-fx-background-color: black;");
        chosenRole = 2;

    }

    // Initializer to ensure that FXML elements are injected correctly
    @FXML
    void initialize() {
        assert EmailAddressText != null : "fx:id=\"EmailAddressText\" was not injected: check your FXML file 'register.fxml'.";
        assert confirmPasswordText != null : "fx:id=\"confirmPasswordText\" was not injected: check your FXML file 'register.fxml'.";
        assert fullNameText != null : "fx:id=\"fullNameText\" was not injected: check your FXML file 'register.fxml'.";
        assert passwordText != null : "fx:id=\"passwordText\" was not injected: check your FXML file 'register.fxml'.";

        this.serviceUser = new UserService();
    }
}