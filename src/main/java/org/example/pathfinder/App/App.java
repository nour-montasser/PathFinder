package org.example.pathfinder.App;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.example.pathfinder.Model.JobOffer;
import org.example.pathfinder.Service.JobOfferService;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/JobOfferList.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("PathFinder");
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/org/example/pathfinder/Sources/pathfinder_logo_compass.png")));
        stage.setMaximized(true);
        stage.show();

    }

    public static void main(String[] args) {

        launch();
        DatabaseConnection.getInstance();
        JobOfferService service = new JobOfferService();

        /*// Create a new JobOffer object
        JobOffer jobOffer = new JobOffer(
                1L,                             // id_User
                "Java Developer",               // title
                "Develop Java applications",    // description
                "Full-time",                    // type
                5,                              // numberOfSpots
                "Bachelor's Degree",            // requiredEducation
                "2 years experience",           // requiredExperience
                "Java, Spring, Hibernate"       // skills
        );
        jobOffer.setDatePosted(new Timestamp(System.currentTimeMillis()));

        // Add a job offer
        System.out.println("Adding a job offer...");
        service.add(jobOffer);

        // Get all job offers
        System.out.println("\nList of job offers:");
        List<JobOffer> offers = service.getall();
        offers.forEach(System.out::println);

        // Modify the job offer
        System.out.println("\nModifying the job offer...");
        if (!offers.isEmpty()) {
            JobOffer offerToModify = offers.get(0);
            offerToModify.setTitle("Senior Java Developer");
            service.update(offerToModify);
            System.out.println("Job offer modified.");
        }

        // Get one job offer
        System.out.println("\nGetting one job offer:");
        JobOffer singleOffer = service.getone();
        if (singleOffer != null) {
            System.out.println(singleOffer);
        }

        // Delete the job offer
        System.out.println("\nDeleting the job offer...");
        if (!offers.isEmpty()) {
            service.delete(offers.get(0));
            System.out.println("Job offer deleted.");
        }

        // Verify deletion
        System.out.println("\nList of job offers after deletion:");
        service.getall().forEach(System.out::println);*/
    }
}