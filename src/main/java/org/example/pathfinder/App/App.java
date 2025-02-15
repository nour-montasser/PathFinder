package org.example.pathfinder.App;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.pathfinder.Service.TestResultService;

import java.sql.Connection;


public class App extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/org/example/pathfinder/view/Question.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 900, 700);
            stage.setTitle("Skill Test Manager");
            stage.setScene(scene);
            stage.show();        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TestResultService testResultService = new TestResultService();
        System.out.println("Test terminé !");
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getCnx();

        if (conn != null) {
            System.out.println("🎯 La connexion est active !");
        } else {
            System.out.println("⚠️ La connexion a échoué !");
        }

        // Close the connection when done
        db.closeConnection();
        launch(); // Launch JavaFX application
    }
}
