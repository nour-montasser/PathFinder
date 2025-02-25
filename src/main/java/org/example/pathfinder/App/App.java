package org.example.pathfinder.App;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.pathfinder.Model.LoggedUser;
import org.example.pathfinder.Model.User;
import org.example.pathfinder.Service.UserService;

import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Set up the FXML scene
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Frontoffice/main-frontoffice.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Set up the window title, icon, and scene
        stage.setTitle("PathFinder");
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/org/example/pathfinder/view/Sources/pathfinder_logo_compass.png")));
        stage.setMaximized(true);

        // Show the stage (window)
        stage.show();

        // Set the logged user ID here (after the stage is shown)


    }

    public static void main(String[] args) {
        long logged = 7L;
        UserService userService = new UserService();
        User u= userService.getUserById(logged);

        LoggedUser.getInstance().setRole(u.getRole());
        LoggedUser.getInstance().setUserId(logged);

        System.out.println("logged"+LoggedUser.getInstance().getUserId());
        System.out.println("logged"+LoggedUser.getInstance().getRole());
        launch();

    }
}
