package org.example.pathfinder.Controller;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingFXUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;
import org.apache.http.util.EntityUtils;
import org.example.pathfinder.Model.LoggedUser;
import org.example.pathfinder.Model.User;
import org.example.pathfinder.Service.UserService;
import javafx.scene.web.WebView;
import org.apache.http.HttpResponse;

import org.apache.http.client.HttpClient;

import org.apache.http.client.methods.HttpGet;

import org.apache.http.client.methods.HttpPost;

import org.apache.http.impl.client.HttpClientBuilder;

public class LoginController {

    public WebView captchaWebView;
    @FXML
    private ResourceBundle resources;

    @FXML
    private TextField captchaInput;
    @FXML
    private URL location;

    @FXML
    private TextField EmailAddressText;

    @FXML
    private PasswordField passwordText;
    private final UserService userService = new UserService();
    private LoggedUser loggedInUser = LoggedUser.getInstance();
    @FXML
    private ImageView captchaImageView;
    private String captchaText;

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

        if(!captchaInput.getText().equalsIgnoreCase(captchaText)) {
            showAlert("Error", "Incorrect CAPTCHA. Try again.");
            return;
        }

        try {
            User u= userService.authenticateUser(email, password);
            if (u != null) {
                System.out.println("User logged in successfully: " + u.getName());
                System.out.println("Logged user: " + u);
                navigateToDashboard(event, u);
                LoggedUser.getInstance().setRole(u.getRole());
                LoggedUser.getInstance().setUserId(u.getId());
                LoggedUser.getInstance().setEmail(u.getEmail());
                LoggedUser.getInstance().setName(u.getName());
                LoggedUser.getInstance().setPassword(u.getPassword());
                LoggedUser.getInstance().setImage(u.getImage());
                System.out.println("User logged in successfully: " + LoggedUser.getInstance().getName());

                System.out.println("logged"+LoggedUser.getInstance().getUserId());
                System.out.println("logged"+LoggedUser.getInstance().getRole());
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
            dashboardPath = "/org/example/pathfinder/view/Frontoffice/main-frontoffice.fxml";
        } else if (user.getRole().equals("SEEKER")) {
            dashboardPath = "/org/example/pathfinder/view/Frontoffice/main-frontoffice.fxml";
        } else {
            dashboardPath = "/org/example/pathfinder/view/Backoffice/main-backoffice.fxml";
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(dashboardPath));
        Parent dashboardParent = loader.load();

        Object controller = loader.getController();

        // Call a method in the new controller (Ensure that method exists in both controllers)
      /*  if (user.getRole().equals("COMPANY")) {
            ((FrontHomeSeekerController) controller).initializeUser(user);
        } else if (user.getRole().equals("SEEKER")) {
            ((FrontHomeSeekerController) controller).initializeUser(user);
        } else {
            ((BackHomeController) controller).initializeUser(user);
        }


*/

        Scene dashboardScene = new Scene(dashboardParent);

        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setScene(dashboardScene);
        currentStage.setMaximized(true);
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
        generateNewCaptcha();
    }

    @FXML
    void signInWithFacebookClicked(MouseEvent event) {
        try {
            Stage stage = new Stage();
            WebView webView = new WebView();
            WebEngine webEngine = webView.getEngine();

            webEngine.setUserAgent(
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/125.0.0.0 Safari/537.36"
            );

            String authUrl = "https://www.facebook.com/v12.0/dialog/oauth?" +
                    "client_id=677244821297425" +
                    "&redirect_uri=" + URLEncoder.encode("http://localhost/facebook-callback", StandardCharsets.UTF_8) +
                    "&response_type=code" +
                    "&display=popup" +
                    "&scope=email";
            webEngine.setJavaScriptEnabled(true);

            webEngine.load(authUrl);

            // Listen for OAuth redirect
            webEngine.locationProperty().addListener((observable, oldValue, newValue) -> {
                System.out.println("URL Changed: " + newValue);

                if (newValue == null) return;

                // Check for Facebook OAuth success (code=...)
                if (newValue.contains("code=")) {
                    String code = newValue.split("code=")[1].split("&")[0];
                    System.out.println("Authorization code: " + code);
                    exchangeFacebookCodeForToken(code, stage);
                }
                // Check for Facebook OAuth errors (e.g., user denied permissions)
                else if (newValue.contains("error=")) {
                    String error = newValue.split("error=")[1].split("&")[0];
                    String errorDescription = newValue.split("error_description=")[1].split("&")[0];
                    errorDescription = URLDecoder.decode(errorDescription, StandardCharsets.UTF_8); // Decode URL-encoded message

                    String finalErrorDescription = errorDescription;
                    Platform.runLater(() -> {
                        showAlert("Facebook Login Failed", finalErrorDescription);
                        stage.close(); // Close the WebView window on error
                    });
                }
            });

            stage.setScene(new Scene(webView, 600, 600));
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Failed to initialize Facebook login.");
        }
    }

    private void exchangeFacebookCodeForToken(String code, Stage stage) {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost("https://graph.facebook.com/v12.0/oauth/access_token?" +
                    "client_id=677244821297425" +
                    "&client_secret=2e65c7f5f0913f7c192efa3038c9bbff" +
                    "&redirect_uri=" + URLEncoder.encode("http://localhost/facebook-callback", StandardCharsets.UTF_8) +
                    "&code=" + code);

            HttpResponse response = client.execute(post);
            String jsonResponse = EntityUtils.toString(response.getEntity());
            JsonObject jsonObject = new Gson().fromJson(jsonResponse, JsonObject.class);
            String accessToken = jsonObject.get("access_token").getAsString();

            // Get user info
            HttpGet get = new HttpGet("https://graph.facebook.com/me?fields=id,name,email&access_token=" + accessToken);
            response = client.execute(get);
            jsonResponse = EntityUtils.toString(response.getEntity());
            jsonObject = new Gson().fromJson(jsonResponse, JsonObject.class);

            String email = jsonObject.get("email").getAsString();
            String name = jsonObject.get("name").getAsString();

            System.out.println("Email: "+email+"nname: "+name);
            // Handle user in your system
            /*User user = userService.findOrCreateSocialUser(email, name, "FACEBOOK");
            Platform.runLater(() -> {
                stage.close();
                try {
                    navigateToDashboard(null, user); // Use actual event parameter
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });*/

        } catch (Exception e) {
            Platform.runLater(() -> showAlert("Error", "Facebook authentication failed."));
        }
    }

    @FXML
    void signInWithGoogleClicked(MouseEvent event) {
        /*
        try {
            Stage stage = new Stage();
            WebView webView = new WebView();
            WebEngine webEngine = webView.getEngine();

            String authUrl = "https://accounts.google.com/o/oauth2/v2/auth?" +
                    "client_id=" + OAuthConfig.GOOGLE_CLIENT_ID +
                    "&redirect_uri=" + URLEncoder.encode(OAuthConfig.GOOGLE_REDIRECT_URI, StandardCharsets.UTF_8) +
                    "&response_type=code" +
                    "&scope=email%20profile";

            webEngine.load(authUrl);

            // Listen for OAuth redirect
            webEngine.locationProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && newValue.contains("code=")) {
                    String code = newValue.split("code=")[1].split("&")[0];
                    exchangeGoogleCodeForToken(code, stage);
                }
            });

            stage.setScene(new Scene(webView, 600, 600));
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Failed to initialize Google login.");
        }*/
    }

    private void exchangeGoogleCodeForToken(String code, Stage stage) {
        /*
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost("https://oauth2.googleapis.com/token");
            List params = new ArrayList<>();
            params.add(new BasicNameValuePair("code", code));
            params.add(new BasicNameValuePair("client_id", OAuthConfig.GOOGLE_CLIENT_ID));
            params.add(new BasicNameValuePair("client_secret", OAuthConfig.GOOGLE_CLIENT_SECRET));
            params.add(new BasicNameValuePair("redirect_uri", OAuthConfig.GOOGLE_REDIRECT_URI));
            params.add(new BasicNameValuePair("grant_type", "authorization_code"));
            post.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse response = client.execute(post);
            String jsonResponse = EntityUtils.toString(response.getEntity());
            JsonObject jsonObject = new Gson().fromJson(jsonResponse, JsonObject.class);
            String accessToken = jsonObject.get("access_token").getAsString();

            // Get user info
            HttpGet get = new HttpGet("https://www.googleapis.com/oauth2/v2/userinfo");
            get.setHeader("Authorization", "Bearer " + accessToken);
            response = client.execute(get);
            jsonResponse = EntityUtils.toString(response.getEntity());
            jsonObject = new Gson().fromJson(jsonResponse, JsonObject.class);

            String email = jsonObject.get("email").getAsString();
            String name = jsonObject.get("name").getAsString();

            // Handle user in your system
            User user = userService.findOrCreateSocialUser(email, name, "GOOGLE");
            Platform.runLater(() -> {
                stage.close();
                try {
                    navigateToDashboard(null, user); // Use actual event parameter
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (Exception e) {
            Platform.runLater(() -> showAlert("Error", "Google authentication failed."));
        }*/
    }

    @FXML
    void resetPassword(MouseEvent event) {
        try {
            // Load the reset password FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Authentification/resetPassword.fxml"));
            Parent resetParent = loader.load();

            // Create new stage for reset password
            Stage resetStage = new Stage();
            resetStage.setTitle("Reset Password");
            resetStage.setScene(new Scene(resetParent));

            // Show the stage
            resetStage.show();

            // Close the current login window (optional)
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load the reset password screen.");
        }
    }


    public void generateNewCaptcha() {
        captchaText = generateRandomString(5); // Generate a random 5-character CAPTCHA
        Image captchaImage = createCaptchaImage(captchaText);
        captchaImageView.setImage(captchaImage);
    }

    private String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder captcha = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            captcha.append(chars.charAt(random.nextInt(chars.length())));
        }

        return captcha.toString();
    }

    private Image createCaptchaImage(String text) {
        int width = 150, height = 50;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        // Set background color
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Add some noise
        Random rand = new Random();
        for (int i = 0; i < 100; i++) {
            g2d.setColor(new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            g2d.drawLine(x, y, x + 1, y + 1);
        }

        // Set font and draw the text
        g2d.setFont(new Font("Arial", Font.BOLD, 26));
        g2d.setColor(Color.BLACK);
        g2d.drawString(text, 25, 35);

        g2d.dispose();
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }
}