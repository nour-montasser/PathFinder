package tn.esprit.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class HelloApplication extends Application {


    public static void main(String[] args) throws Exception {
        launch();


    }
    public void start(Stage stage)throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/demo/login.fxml"));
        Parent parent = loader.load(); // charger l'interface dans la variable parent

        Scene scene = new Scene(parent);
// Set background color to transparent
        scene.setFill(Color.TRANSPARENT);

        stage.initStyle(StageStyle.TRANSPARENT); // This will remove the default window decorations
        stage.setTitle("Ajouter un Utilisateur");
// injecter la scene dans la stage
        stage.setScene(scene);

        stage.show();



    }


}