package org.example.pathfinder.App;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Authentification/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("PathFinder");
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/org/example/pathfinder/Sources/pathfinder_logo_compass.png")));
        stage.show();

    }

    public static void main(String[] args) {

        launch();
        DatabaseConnection.getInstance();

    }
}