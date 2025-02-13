package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.pathfinder.Model.CV;
import org.example.pathfinder.Service.CVService;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.List;
import java.util.ArrayList;

import java.net.URL;
import java.util.ResourceBundle;
import java.io.IOException;



public class CVShowController implements Initializable {
    @FXML
    private GridPane cvGridPane; // ✅ Matches fx:id from FXML

    private final CVService cvService = new CVService();
    private final int userId = 1; // 🔥 Change this to dynamically fetch the logged-in user
    @FXML
    private TextField searchField;
    private List<CV> allCVs = new ArrayList<>();
    @FXML
    private ImageView searchIcon;
    @FXML
    private Button addCVButton;
    @FXML
    private ImageView addIcon;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (cvGridPane == null) {
            System.err.println("❌ cvGridPane is NULL! Check FXML.");
            return;
        }

        System.out.println("✅ cvGridPane loaded successfully!");

        allCVs = cvService.getCVsByUserId(userId); // Fetch all CVs once
        loadCVsIntoGrid(allCVs); // Load CVs initially

        // 🔥 Search field live filtering (improved)
        searchField.textProperty().addListener((obs, oldVal, newVal) -> onSearchTextChanged(newVal));
        try {
            String imagePath = getClass().getResource("/org/example/pathfinder/view/Sources/pathfinder_logo_compass.png").toExternalForm();
            searchIcon.setImage(new Image(imagePath));
            String imagePath2 = getClass().getResource("/org/example/pathfinder/view/Sources/pathfinder_logo_compass.png").toExternalForm();
            addIcon.setImage(new Image(imagePath2));
        } catch (NullPointerException e) {
            System.err.println("⚠️ Image not found! Check the path.");
        }
        addCVButton.setOnAction(event -> openCVForm());
    }
    @FXML
    private void openCVForm() {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/CV-forum.fxml"));
            Parent newRoot = loader.load();

            // Get the current stage
            Stage stage = (Stage) addCVButton.getScene().getWindow();
            stage.getScene().setRoot(newRoot); // Replace the current scene content with the new one
        } catch (IOException e) {
            System.err.println("❌ Error loading CV-Forum.fxml: " + e.getMessage());
        }
    }



    private void loadCVsIntoGrid(List<CV> cvList) {
        cvGridPane.getChildren().clear(); // Clear previous entries

        int column = 0;
        int row = 0;
        for (CV cv : cvList) {
            VBox cvBox = createCVBox(cv);
            cvGridPane.add(cvBox, column, row);

            column++;
            if (column == 3) { // 3 columns per row
                column = 0;
                row++;
            }
        }
    }








    private VBox createCVBox(CV cv) {
        VBox box = new VBox();
        box.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-padding: 15; -fx-spacing: 10; "
                + "-fx-border-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 3); "
                + "-fx-alignment: CENTER;");

        ImageView imageView = new ImageView();
        try {
            String imagePath = getClass().getResource("/org/example/pathfinder/view/Sources/pathfinder_logo_compass.png").toExternalForm();
            imageView.setImage(new Image(imagePath));
        } catch (NullPointerException e) {
            System.err.println("⚠️ Image not found!");
        }
        imageView.setFitWidth(150);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);

        Label titleLabel = new Label(cv.getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-alignment: center;");

        Label dateLabel = new Label("Created: " + cv.getDateCreation().toString());
        dateLabel.setStyle("-fx-text-fill: #555; -fx-font-size: 14px; -fx-text-alignment: center;");

        // 🔥 Set Click Event - Open CV Forum with CV ID
        box.setOnMouseClicked(event -> openCVFormWithData(cv.getIdCV()));

        box.getChildren().addAll(imageView, titleLabel, dateLabel);
        return box;
    }
    @FXML
    private void openCVFormWithData(int cvId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/CV-forum.fxml"));
            Parent newRoot = loader.load();

            // 🔥 Pass the CV ID to CVController
            CVController cvController = loader.getController();
            cvController.loadCVData(cvId);

            // 🔥 Get the current stage and update the scene
            Stage stage = (Stage) cvGridPane.getScene().getWindow();
            stage.getScene().setRoot(newRoot);
        } catch (IOException e) {
            System.err.println("❌ Error loading CV-Forum.fxml: " + e.getMessage());
        }
    }

    private void onSearchTextChanged(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            loadCVsIntoGrid(allCVs); // 🔹 Show all if search is empty
            return;
        }

        List<CV> filteredCVs = allCVs.stream()
                .filter(cv -> cv.getTitle().toLowerCase().contains(searchText.toLowerCase()))
                .toList();

        loadCVsIntoGrid(filteredCVs);
    }





}
