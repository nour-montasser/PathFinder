package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.pathfinder.Model.User;
import org.example.pathfinder.Service.UserService;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Properties;

public class ResetPassword {

    @FXML
    private TextField EmailAddressText;

    private UserService userService = new UserService(); // Add UserService instance

    @FXML
    void resetClicked(MouseEvent event) {
        String email = EmailAddressText.getText().trim();

        if(email.isEmpty()) {
            showAlert("Error", "Please enter your email address");
            return;
        }

        try {
            User user = userService.getUserByEmail(email);
            if (user == null) {
                showAlert("Error", "No account found with this email address.");
                return;
            }

            // Generate a new random password
            String newPassword = generateRandomPassword();

            // Update the user's password in the database
            userService.updatePassword(user.getId(), newPassword);

            // Send the new password via email
            sendPasswordResetEmail(email, newPassword);

            showAlert("Success", "A new password has been sent to your email. Please check your inbox.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage());
        } catch (MessagingException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to send reset email: " + e.getMessage());
        }
    }

    // Generate a secure random password
    private String generateRandomPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*()_+";
        String allChars = upper + lower + digits + special;
        SecureRandom random = new SecureRandom();

        StringBuilder password = new StringBuilder();

        // Ensure at least one character from each set
        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(lower.charAt(random.nextInt(lower.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special.charAt(random.nextInt(special.length())));

        // Fill remaining length (total 12 characters)
        for (int i = 0; i < 8; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Shuffle the combined characters
        return shuffleString(password.toString());
    }

    // Helper to shuffle the password string
    private String shuffleString(String input) {
        char[] characters = input.toCharArray();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < characters.length; i++) {
            int randomIndex = random.nextInt(characters.length);
            char temp = characters[i];
            characters[i] = characters[randomIndex];
            characters[randomIndex] = temp;
        }
        return new String(characters);
    }

    // Updated to include newPassword in email
    private void sendPasswordResetEmail(String recipientEmail, String newPassword) throws MessagingException {
        String senderEmail = "rakahproject@gmail.com";
        String password = "hsiy dguz vubr qbrg"; // Replace with actual password
        String smtpHost = "smtp.gmail.com";
        int smtpPort = 587;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
        message.setSubject("Your New Password");

        String emailBody = "Your new password is: " + newPassword + "\n"
                + "Please login and change your password immediately for security reasons.\n\n";

        message.setText(emailBody);

        Transport.send(message);
    }

    // Existing showAlert and loginChange methods remain unchanged
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void loginChange(MouseEvent mouseEvent) {
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
            Stage currentStage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            currentStage.close(); // Close the current registration window

        } catch (IOException e) {
            e.printStackTrace(); // Handle the IOException (e.g., file not found or issue with loading)
        }    }
}