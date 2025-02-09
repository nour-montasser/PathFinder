package org.example.pathfinder.App;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Load the FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/org/example/pathfinder/view/CV-Forum.fxml"));

        // Create the Scene
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);

        // Configure the Stage (Fullscreen Mode)
        stage.setTitle("CV Forum");
        stage.setScene(scene);
        stage.setMaximized(true); // This makes the window fullscreen
        stage.show();
    }

    public static void main(String[] args) {
        launch(); // Launch JavaFX application
    }
}
