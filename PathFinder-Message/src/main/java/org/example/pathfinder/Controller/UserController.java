package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.example.pathfinder.Model.User;
import org.example.pathfinder.Service.UserService;

import java.util.List;

public class UserController {
    private UserService userService;  // Ensure this is initialized

    // Reference to the ListView in your FXML
    @FXML
    private ListView<User> userListView;

    public UserController() {
        userService = new UserService();  // Initialize the UserService
    }

    @FXML
    private void initialize() {
        List<User> users = userService.getall();  // Retrieve all users
        userListView.getItems().setAll(users);  // Populate the ListView with users
    }




    // Handle the user click event



    // Method to create or get the channel between user 1 and the selected user

    private User selectedUser;  // The currently selected user

    // Method to set the selected user
    public void setSelectedUser(User user) {
        this.selectedUser = user;
    }

    // Method to get the selected user’s ID
    public Long getSelectedUserId() {
        if (selectedUser != null) {
            return selectedUser.getId();  // Return the selected user's ID
        }
        return null;  // Return null if no user is selected
    }

}
