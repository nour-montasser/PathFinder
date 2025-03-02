package org.example.pathfinder.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.pathfinder.Model.User;
import org.example.pathfinder.Service.UserService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class BackUserController {

    @FXML
    private Button HomeButton;

    @FXML
    private Button HomeButton1;

    @FXML
    private Button Profile;

    @FXML
    private Button btn_workbench111111;

    @FXML
    private GridPane grid;

    @FXML
    private Button changepassword1;

    @FXML
    private Label currentUserField;

    @FXML
    private Label dailyAdvicesText;

    @FXML
    private Button delete;

    @FXML
    private Label idU;

    @FXML
    private Pane l;

    @FXML
    private HBox root;

    @FXML
    private AnchorPane side_ankerpane;

    @FXML
    private Pane userCard;

    @FXML
    private Text userFullName;

    @FXML
    private ImageView userImage;

    @FXML
    private Text userName;

    @FXML
    private Label userNameLabel;

    @FXML
    private ImageView userPhoto;

    @FXML
    private Text userRole;

    @FXML
    private TextField searchText;

    private final UserService userService = new UserService();
    private User currentUser;

    @FXML
    private ChoiceBox<String> sortBy;

    @FXML
    private ChoiceBox<String> sortOrder;

    @FXML
    public void initialize() {
        sortBy.getItems().addAll("Name", "Email", "Role");
        sortBy.setValue("Name"); // Default sorting criteria

        sortOrder.getItems().addAll("Ascending", "Descending");
        sortOrder.setValue("Ascending"); // Default sorting order
    }


    @FXML
    void goHome(ActionEvent event) {
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
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close(); // Close the current registration window

        } catch (IOException e) {
            e.printStackTrace(); // Handle the IOException (e.g., file not found or issue with loading)
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    @FXML
    void deleteButtonOnClick(ActionEvent event) {

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


    public void initializeUser(User user) {
        this.currentUser = user;
        userName.setText(user.getEmail());

        List<User> users = userService.getAllUsers(); // Fetch users from DB
        grid.getChildren().clear(); // Clear previous entries
        int column = 0;
        int row = 0;

        for (User userData : users) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Dashboard/Back/UserCard.fxml"));
                Pane userCard = loader.load();
                UserCardController controller = loader.getController();
                controller.setUserData(userData, user); // Set user data dynamically

                // Add to GridPane at (row, column)
                grid.add(userCard, column, row);

                // Increment column, move to new row if necessary
                column++;
                if (column == 3) { // Change '3' to the desired number of columns
                    column = 0;
                    row++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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


    @FXML
    void searchUser(ActionEvent event) {
        String searchText = this.searchText.getText().trim().toLowerCase();

        if (searchText.isEmpty()) {
            initializeUser(currentUser);
        }

        List<User> allUsers = userService.getAllUsers();
        List<User> filteredUsers = allUsers.stream()
                .filter(user -> user.getName().toLowerCase().contains(searchText) ||
                        user.getEmail().toLowerCase().contains(searchText) ||
                        user.getRole().toLowerCase().contains(searchText))
                .toList();
        grid.getChildren().clear(); // Clear previous entries
        int column = 0;
        int row = 0;

        for (User userData : filteredUsers) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Dashboard/Back/UserCard.fxml"));
                Pane userCard = loader.load();
                UserCardController controller = loader.getController();
                controller.setUserData(userData, currentUser); // Set user data dynamically

                // Add to GridPane at (row, column)
                grid.add(userCard, column, row);

                // Increment column, move to new row if necessary
                column++;
                if (column == 3) { // Change '3' to the desired number of columns
                    column = 0;
                    row++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Show a message if no users match the search criteria
        if (filteredUsers.isEmpty()) {
            showAlert("No Results", "No users found matching your search criteria.");
        }

    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void sortUsers(ActionEvent event) {
        String sortCriteria = sortBy.getValue();
        String sortDirection = sortOrder.getValue();
        String sortOrderValue = "asc";

        if (sortCriteria == null || sortDirection == null) {
            showAlert("Sorting Error", "Please select both sorting criteria and order.");
            return;
        }

        if(sortDirection.equals("Descending"))
            sortOrderValue = "desc";

        List<User> users = userService.getAllUsersSorted(sortCriteria,sortOrderValue);

        // Clear the grid and re-populate it with sorted users
        grid.getChildren().clear();
        int column = 0;
        int row = 0;

        for (User userData : users) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Dashboard/Back/UserCard.fxml"));
                Pane userCard = loader.load();
                UserCardController controller = loader.getController();
                controller.setUserData(userData, currentUser); // Set user data dynamically

                // Add to GridPane at (row, column)
                grid.add(userCard, column, row);

                // Increment column, move to new row if necessary
                column++;
                if (column == 3) { // Change '3' to the desired number of columns
                    column = 0;
                    row++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    void showStats(ActionEvent event) {

        int count14to18 = userService.getUserCountByAgeGroup(14, 18);
        int count18to22 = userService.getUserCountByAgeGroup(18, 22);
        int count22to40 = userService.getUserCountByAgeGroup(22, 40);
        int count40to60 = userService.getUserCountByAgeGroup(40, 60);

        if (count14to18 == 0 && count18to22 == 0 && count22to40 == 0 && count40to60 == 0) {
            showAlert("Error","No users found in any age group. (Check user profiles)");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Dashboard/Back/UserStats.fxml"));
            Parent statsParent = loader.load(); // Load the stats interface

            Stage statsStage = new Stage();
            Scene statsScene = new Scene(statsParent);
            statsStage.setTitle("User Age Statistics"); // Title for the stats window

            statsStage.setScene(statsScene);
            statsStage.show();

        } catch (IOException e) {
            e.printStackTrace(); // Handle the IOException (e.g., file not found or issue with loading)
        }
    }
}
