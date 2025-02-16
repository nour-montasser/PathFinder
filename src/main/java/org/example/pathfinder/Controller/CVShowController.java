package org.example.pathfinder.Controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Parent;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.pathfinder.Model.CV;

import org.example.pathfinder.Service.CVService;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

import java.net.URL;
import java.util.ResourceBundle;
import java.io.IOException;

import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;






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
    @FXML
    private StackPane deleteConfirmationOverlay;

    @FXML
    private Button confirmDeleteButton;
    @FXML
    private Button cancelDeleteButton;

    private int cvToDeleteId; // Store the CV ID to delete
    private TextField currentlyEditingField = null;
    @FXML
    private MenuButton sortDropdown; // Sorting dropdown


    @FXML
    private MenuItem sortMostRelevant;
    @FXML
    private MenuItem sortNewest;
    @FXML
    private MenuItem sortOldest;
    @FXML
    private MenuItem sortAZ;
    @FXML
    private MenuItem sortZA;

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
        cancelDeleteButton.setOnAction(e -> cancelDelete());
        confirmDeleteButton.setOnAction(e -> confirmDelete());
        initializeSortingDropdown();


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

    private void initializeSortingDropdown() {
        // 🔥 Create sorting options dynamically
        sortMostRelevant = createMenuItem("Most Relevant", "org/example/pathfinder/view/Sources/star_filled.png");
        sortNewest = createMenuItem("Newest Edited", "org/example/pathfinder/view/Sources/sort_newest.png");
        sortOldest = createMenuItem("Oldest Edited", "org/example/pathfinder/view/Sources/sort_oldest.png");
        sortAZ = createMenuItem("Alphabetical (A-Z)", "org/example/pathfinder/view/Sources/sort_az.png");
        sortZA = createMenuItem("Alphabetical (Z-A)", "org/example/pathfinder/view/Sources/sort_za.png");


        // 🔥 Add sorting options to dropdown
        sortDropdown.getItems().addAll(sortMostRelevant, sortNewest, sortOldest, sortAZ, sortZA);

        // 🔥 Set event handlers for sorting selection
        sortMostRelevant.setOnAction(e -> updateSortSelection(sortMostRelevant));
        sortNewest.setOnAction(e -> updateSortSelection(sortNewest));
        sortOldest.setOnAction(e -> updateSortSelection(sortOldest));
        sortAZ.setOnAction(e -> updateSortSelection(sortAZ));
        sortZA.setOnAction(e -> updateSortSelection(sortZA));
    }

    private void updateSortSelection(MenuItem selectedItem) {
        sortDropdown.setText(selectedItem.getText()); // Update text
        sortCVs(selectedItem.getText()); // Apply sorting logic
        if (selectedItem.getGraphic() instanceof ImageView) {
            sortDropdown.setGraphic(new ImageView(((ImageView) selectedItem.getGraphic()).getImage())); // Update icon
        }
    }


    private void sortCVs(String criteria) {
        switch (criteria) {
            case "Most Relevant":
                // Logic for sorting by relevance (Modify based on your own logic)
                allCVs.sort((cv1, cv2) -> Integer.compare(cv2.getIdCV(), cv1.getIdCV())); // Example: Latest first
                break;
            case "Newest Edited":
                allCVs.sort((cv1, cv2) -> cv2.getDateCreation().compareTo(cv1.getDateCreation()));
                break;
            case "Oldest Edited":
                allCVs.sort(Comparator.comparing(CV::getDateCreation));
                break;
            case "Alphabetical (A-Z)":
                allCVs.sort((cv1, cv2) -> cv1.getTitle().compareToIgnoreCase(cv2.getTitle()));
                break;
            case "Alphabetical (Z-A)":
                allCVs.sort((cv1, cv2) -> cv2.getTitle().compareToIgnoreCase(cv1.getTitle()));
                break;
        }

        loadCVsIntoGrid(allCVs); // Refresh Grid with Sorted CVs
    }
    private MenuItem createMenuItem(String text, String iconPath) {
        URL resourceUrl = getClass().getResource(iconPath);

        if (resourceUrl == null) {
            System.err.println("⚠️ Icon not found: " + iconPath); // Debugging output
            return new MenuItem(text); // Return without icon if not found
        }

        ImageView icon = new ImageView(new Image(resourceUrl.toExternalForm()));
        icon.setFitWidth(16);
        icon.setFitHeight(16);
        icon.setPreserveRatio(true);

        return new MenuItem(text, icon);
    }



    private void loadCVsIntoGrid(List<CV> cvList) {
        cvGridPane.getChildren().clear(); // Clear previous entries

        int column = 0;
        int row = 0;
        for (CV cv : cvList) {
            VBox cvBox = createCVBox(cv);
            cvGridPane.add(cvBox, column, row);

            column++;
            if (column == 5) { // 3 columns per row
                column = 0;
                row++;
            }
        }
    }
    private VBox createCVBox(CV cv) {
        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-padding: 15; "
                + "-fx-border-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 3);"
                + "-fx-alignment: top-left;");

        // 🔹 Top Menu (Favorite & Dropdown)
        HBox topMenu = createTopMenu(cv);

        // 🔹 CV Thumbnail (A4 Size)
        ImageView imageView = getResizedIcon("/org/example/pathfinder/view/Sources/pathfinder_logo_compass.png", 180, 240);

        // 🔹 Title Section with Editing
        VBox titleEditor = createTitleEditor(cv);

        // 🔹 Date Label Section
        VBox dateLabel = createDateLabel(cv);

        // 🔹 Text Container (Title + Date)
        VBox textContainer = new VBox(5);
        textContainer.setStyle("-fx-alignment: center-left; -fx-spacing: 5; -fx-padding: 10 0 0 5;");
        textContainer.getChildren().addAll(titleEditor, dateLabel);

        // 🔹 Assemble Final Box
        box.getChildren().addAll(topMenu, imageView, textContainer);
        imageView.setPickOnBounds(true); // Ensures clicks are detected even on transparent parts
        imageView.setOnMouseClicked(event -> {
            event.consume(); // Prevent event from propagating
            openCVFormWithData(cv.getIdCV());
        });
        dateLabel.setOnMouseClicked(event -> openCVFormWithData(cv.getIdCV()));



        // 📏 Set A4 Paper Dimensions
        box.setPrefSize(275, 400);
        box.setMaxSize(275, 400);
        box.setMinSize(275, 400);

        return box;
    }

    private HBox createTopMenu(CV cv) {
        HBox topMenu = new HBox(10);
        topMenu.setStyle("-fx-alignment: top-right;");

        // ⭐ Favorite Button (Toggle)
        ToggleButton favoriteButton = new ToggleButton();
        favoriteButton.setGraphic(getResizedIcon("/org/example/pathfinder/view/Sources/star_outline.png", 20, 20));
        favoriteButton.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");

        // Toggle Star Icon on Click
        favoriteButton.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            String iconPath = isSelected ? "/org/example/pathfinder/view/Sources/star_filled.png"
                    : "/org/example/pathfinder/view/Sources/star_outline.png";
            favoriteButton.setGraphic(getResizedIcon(iconPath, 20, 20));
        });

        // ⋮ Dropdown Menu (MenuButton)
        MenuButton menuButton = new MenuButton();
        menuButton.setGraphic(getResizedIcon("/org/example/pathfinder/view/Sources/menu_dots.png", 20, 20));
        menuButton.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");

        // 📌 Menu Items (With Icons)
        MenuItem copyItem = new MenuItem("Make A Copy", getResizedIcon("/org/example/pathfinder/view/Sources/copy_icon.png", 16, 16));
        MenuItem deleteItem = new MenuItem("Move To Trash", getResizedIcon("/org/example/pathfinder/view/Sources/delete_icon.png", 16, 16));
        MenuItem downloadItem = new MenuItem("Download", getResizedIcon("/org/example/pathfinder/view/Sources/download_icon.png", 16, 16));

        // 📌 Delete Confirmation & Remove from UI
        deleteItem.setOnAction(e -> showDeleteConfirmation(cv.getIdCV()));

        // 📌 Duplicate & Refresh Grid
        copyItem.setOnAction(e -> {
            cvService.makeCopyOfCV(cv.getIdCV());
            refreshCVGrid();
        });

        menuButton.getItems().addAll(copyItem, deleteItem, downloadItem);
        topMenu.getChildren().addAll(favoriteButton, menuButton);

        return topMenu;
    }
    // Global Variable to Track Currently Editing TitleField


    private VBox createTitleEditor(CV cv) {
        VBox titleContainer = new VBox();
        titleContainer.setStyle("-fx-alignment: center-left; -fx-spacing: 5; -fx-padding: 5;");

        // 🔹 StackPane to Stack Title Label and TextField
        StackPane titleStack = new StackPane();
        titleStack.setMaxWidth(220);
        titleStack.setMinWidth(220);

        // 🔹 Title Label
        Label titleLabel = new Label(cv.getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-wrap-text: true;");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(220);
        titleLabel.setMinWidth(220);

        // 🔹 Title TextField (Initially Hidden)
        TextField titleField = new TextField(cv.getTitle());
        titleField.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        titleField.setMaxWidth(220);
        titleField.setMinWidth(220);
        titleField.setVisible(false); // Initially hidden

        // ✏️ Edit Button (Pen Icon)
        Button editButton = new Button();
        editButton.setGraphic(getResizedIcon("/org/example/pathfinder/view/Sources/edit_icon.png", 16, 16));
        editButton.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");

        // 🔥 Click Anywhere in TitleContainer to Enable Editing
        titleContainer.setOnMouseClicked(e -> {
            if (currentlyEditingField != null && currentlyEditingField != titleField) {
                // Hide any previously edited field before opening another one
                currentlyEditingField.setVisible(false);
                currentlyEditingField.getParent().getChildrenUnmodifiable().get(0).setVisible(true);
            }
            System.out.println(currentlyEditingField);
            // Set this field as currently editing
            currentlyEditingField = titleField;
            System.out.println(currentlyEditingField);
            titleLabel.setVisible(false);
            titleField.setVisible(true);
            titleField.requestFocus();
            titleField.selectAll();
        });

        // 🔥 Submit on Enter
        titleField.setOnAction(e -> {
            String newTitle = titleField.getText().trim();
            if (!newTitle.isEmpty() && isTitleUnique(newTitle, cv.getIdCV())) {
                cv.setTitle(newTitle);
                cvService.update(cv);

                titleLabel.setText(newTitle);
                titleLabel.setVisible(true);
                titleField.setVisible(false);
                currentlyEditingField = null; // Reset the currently editing field
            } else {
                System.err.println("❌ Title is either empty or already exists.");
            }
        });

        // 🔹 Add Title Label and TextField to StackPane (Overlapping)
        titleStack.getChildren().addAll(titleLabel, titleField);

        // 🔹 Add Components to Container
        HBox titleRow = new HBox(5);
        titleRow.setStyle("-fx-alignment: center-left; -fx-spacing: 5;");
        titleRow.getChildren().addAll(titleStack, editButton);

        titleContainer.getChildren().add(titleRow);

        // ✅ Click Outside to Hide Input Field


        return titleContainer;
    }





    private VBox createDateLabel(CV cv) {
        VBox dateContainer = new VBox();
        dateContainer.setStyle("-fx-alignment: center-left; -fx-padding: 5 0 0 5;");

        Label dateLabel = new Label("Created: " + cv.getDateCreation().toString());
        dateLabel.setStyle("-fx-text-fill: #555; -fx-font-size: 14px; -fx-text-alignment: left;");

        dateContainer.getChildren().add(dateLabel);
        return dateContainer;
    }
    private void refreshCVGrid() {
        allCVs = cvService.getCVsByUserId(userId);
        loadCVsIntoGrid(allCVs);
    }
    @FXML
    private void openCVFormWithData(int cvId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pathfinder/view/CV-forum.fxml"));
            Parent newRoot = loader.load();
            cvService.updateLastViewed(cvId);
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

    private ImageView getResizedIcon(String resourcePath, double width, double height) {
        try {
            ImageView icon = new ImageView(new Image(getClass().getResource(resourcePath).toExternalForm()));
            icon.setFitWidth(width);
            icon.setFitHeight(height);
            icon.setPreserveRatio(true);
            return icon;
        } catch (NullPointerException e) {
            System.err.println("⚠️ Icon not found: " + resourcePath);
            return new ImageView(); // Return empty if icon not found
        }
    }
    @FXML
    private void showDeleteConfirmation(int cvId) {
        cvToDeleteId = cvId;
        deleteConfirmationOverlay.setVisible(true);
    }

    @FXML
    private void cancelDelete() {
        deleteConfirmationOverlay.setVisible(false);
    }

    @FXML
    private void confirmDelete() {
        if (cvToDeleteId > 0) {
            cvService.delete(cvToDeleteId); // Delete from database
            allCVs.removeIf(cv -> cv.getIdCV() == cvToDeleteId); // Remove from list
            loadCVsIntoGrid(allCVs); // Refresh UI
        }
        deleteConfirmationOverlay.setVisible(false); // Hide modal
    }
    private boolean isTitleUnique(String newTitle, int cvId) {

        for (CV cv : allCVs) {
            if (cv.getTitle().equalsIgnoreCase(newTitle) && cv.getIdCV() != cvId) {
                return false; // Title already exists for a different CV
            }
        }
        return true; // Title is unique
    }



}
