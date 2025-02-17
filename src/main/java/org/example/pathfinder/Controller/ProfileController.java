package org.example.pathfinder.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import org.example.pathfinder.Model.Profile;
import org.example.pathfinder.Model.User;
import org.example.pathfinder.Service.ProfileService;
import org.example.pathfinder.Service.UserService;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class ProfileController {
    @FXML
    private Button HomeButton;

    @FXML
    private TextField addressTextField;

    @FXML
    private TextArea bioTextArea;

    @FXML
    private DatePicker birthdateDatePicker;

    @FXML
    private Button delete;

    @FXML
    private TextField currentOccupationTextField;

    @FXML
    private TextField phoneTextField;

    @FXML
    private Button submit;

    @FXML
    private ImageView userPhoto;

    @FXML
    private Text usernameEmail;

    @FXML
    private TextField  userFullName;
    private final UserService serviceUser = new UserService();;

    @FXML
    private Text userRole;

    private final ProfileService profileService = new ProfileService();
    private final UserService userService = new UserService();
    private User currentUser;
    private Profile currentProfile;

    @FXML
    private PasswordField confirmNewPasswordTextField;


    @FXML
    private PasswordField newPasswordTextField;

    @FXML
    private PasswordField oldPasswordTextField;

    public void initializeUser(User user) {
        this.currentUser = user;
        this.usernameEmail.setText(user.getEmail());
        this.userFullName.setText(user.getName());
        this.userRole.setText(user.getRole());
        loadUserProfile();
    }

    private void loadUserProfile() {
        currentProfile = profileService.getOne(currentUser.getId());
        if (currentProfile != null) {
            addressTextField.setText(currentProfile.getAddress());
            bioTextArea.setText(currentProfile.getBio());
            phoneTextField.setText(currentProfile.getPhone());
            currentOccupationTextField.setText(currentProfile.getCurrent_occupation());
            if (currentProfile.getBirthday() != null) {
                birthdateDatePicker.setValue(
                        ((java.sql.Date) currentProfile.getBirthday()).toLocalDate()
                );
            }
        }
    }

    private void clearFields() {
        addressTextField.clear();
        bioTextArea.clear();
        phoneTextField.clear();
        currentOccupationTextField.clear();
        birthdateDatePicker.setValue(null);
        userPhoto.setImage(null);
        currentProfile = null;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    void goHome(ActionEvent event) {
        try {
            // Load the login.fxml (login page)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Dashboard/Front/FrontHomeSeeker.fxml"));
            Parent loginParent = loader.load(); // Load the login interface

            // Create a new stage for the login window
            Stage loginStage = new Stage();
            Scene loginScene = new Scene(loginParent);
            loginStage.setTitle("Login"); // Title for the login window
            Object controller = loader.getController();
            ((FrontHomeSeekerController) controller).initializeUser(userService.getUserByEmail(usernameEmail.getText()));

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

    public void UploadImageOnClick(ActionEvent actionEvent) {
    }


    public void submitButtonOnClick(ActionEvent actionEvent) {
        if (currentProfile == null) {
            currentProfile = new Profile();
            currentProfile.setId_user(currentUser.getId());
        }

        currentProfile.setAddress(addressTextField.getText());
        currentProfile.setBio(bioTextArea.getText());

        if (!phoneTextField.getText().matches("\\d{8}")) {
            showAlert("Validation Error", "Phone number must be exactly 8 digits.");
            return;
        }

        currentProfile.setPhone(phoneTextField.getText());

        currentProfile.setCurrent_occupation(currentOccupationTextField.getText());
        LocalDate localDate = birthdateDatePicker.getValue();
        if (localDate != null) {

            if (!localDate.isBefore(LocalDate.now())) {
                showAlert("Validation Error", "Birthdate must be before today's date.");
                return;
            }
            currentProfile.setBirthday(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        if (profileService.getOne(currentUser.getId()) != null) {
            profileService.update(currentProfile);
            showAlert("Success", "Profile updated successfully.");
        } else {
            profileService.add(currentProfile);
            showAlert("Success", "Profile created successfully.");
        }
    }

    @FXML
    private void changePasswordButtonOnClick(ActionEvent actionEvent) {
        String oldPassword = oldPasswordTextField.getText();
        String newPassword = newPasswordTextField.getText();
        String confirmNewPassword = confirmNewPasswordTextField.getText();

        // Validate fields
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            showAlert("Error", "All fields must be filled!");
            return;
        }

        // Check if new password and confirmation match
        if (!newPassword.equals(confirmNewPassword)) {
            showAlert("Error", "New password and confirmation do not match!");
            return;
        }

        if (!isValidPassword(newPassword)) {
            showAlert("Erreur", "Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et un symbole !");
            return;
        }

        if (serviceUser.verifyOldPassword(currentUser.getId(),oldPassword)) {
            serviceUser.updatePassword(currentUser.getId(),newPassword);
            showAlert("Success", "Password changed successfully!");
            oldPasswordTextField.setText("");
            newPasswordTextField.setText("");
            confirmNewPasswordTextField.setText("");
        } else {
            showAlert("Error", "Old password is incorrect!");
        }
    }


    // Password validation method
    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }


    public void deleteButtonOnClick(ActionEvent actionEvent) {
        if (currentProfile != null) {
            profileService.delete(currentUser.getId());
            showAlert("Success", "Profile deleted successfully.");
            clearFields();
        } else {
            showAlert("Error", "No profile to delete.");
        }
    }


    @FXML
    void saveNameButtonOnClick(ActionEvent event) {
        serviceUser.changeUserNameByEmail(usernameEmail.getText(), userFullName.getText());
        showAlert("Success", "User name updated successfully.");
    }

    @FXML
    void resetOnClicked(ActionEvent event) {
        try {
            User user = userService.getUserByEmail(usernameEmail.getText());

            userService.deleteUser(user.getId());

            this.logOut(event);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
