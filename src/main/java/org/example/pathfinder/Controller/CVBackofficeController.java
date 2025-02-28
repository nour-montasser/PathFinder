package org.example.pathfinder.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.pathfinder.Model.CV;
import org.example.pathfinder.Service.CVService;

import java.util.List;

public class CVBackofficeController {

    @FXML private TableView<CV> cvTable;
    @FXML private TableColumn<CV, Integer> colId;
    @FXML private TableColumn<CV, String> colUserName;
    @FXML private TableColumn<CV, String> colTitle;
    @FXML private TableColumn<CV, String> colSkills;
    @FXML private TableColumn<CV, String> colExperiences;
    @FXML private TableColumn<CV, String> colLanguages;
    @FXML private TableColumn<CV, String> colCertificates;

    private final CVService cvService = new CVService(); // Service to fetch CVs

    @FXML
    public void initialize() {
        System.out.println("✅ CVBackofficeController Initialized!");

        // Bind table columns to CV attributes
        colId.setVisible(false);
        colId.setCellValueFactory(new PropertyValueFactory<>("idCV"));
        colUserName.setCellValueFactory(new PropertyValueFactory<>("username")); // Ensure CV has userName
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colSkills.setCellValueFactory(new PropertyValueFactory<>("skills"));
        colExperiences.setCellValueFactory(new PropertyValueFactory<>("formattedExperiences"));
        colLanguages.setCellValueFactory(new PropertyValueFactory<>("formattedLanguages"));
        colCertificates.setCellValueFactory(new PropertyValueFactory<>("formattedCertificates"));

        // Load CVs into the table
        loadCVData();
    }

    private void loadCVData() {
        List<CV> cvList = cvService.getAllCVsWithDetails();
        ObservableList<CV> observableCVList = FXCollections.observableArrayList(cvList);
        cvTable.setItems(observableCVList);
    }
}
