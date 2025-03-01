package org.example.pathfinder.App;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.pathfinder.Service.MessageService;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Initialize services
        MessageService messageService = new MessageService();

        // Test MessageService (CRUD operations)



        // Load JavaFX interface

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/Frontoffice/main-frontoffice.fxml"));
        Parent root = loader.load();  // Load the FXML layout
        Scene scene = new Scene(root);


        stage.setTitle("PathFinder App");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();  // Display the window
    }

    public static void main(String[] args) {
        launch();  // Launch the JavaFX application
    }
}
