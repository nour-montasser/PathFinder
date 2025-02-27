package org.example.pathfinder.App;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;


public class    App extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/org/example/pathfinder/view/Frontoffice/Question.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
            stage.setTitle("PathFinder");
            stage.setScene(scene);
           // stage.setMaximized(true);
            stage.show();        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Test terminé !");
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn         = db.getCnx();

        if (conn != null) {
            System.out.println("🎯 La connexion est active !");
        } else {
            System.out.println("⚠️ La connexion a échoué !");
        }


        launch(); // Launch JavaFX application
    }
}
