package org.example.pathfinder.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.pathfinder.Model.ServiceOffre;
import org.example.pathfinder.Service.ServiceOffreService;

import java.util.List;

public class DashboardServiceController {

    @FXML
    private TableView<ServiceOffre> serviceTable;

    @FXML
    private TableColumn<ServiceOffre, Integer> idServiceColumn, idUserColumn;

    @FXML
    private TableColumn<ServiceOffre, String> titleColumn, descriptionColumn, fieldColumn, experienceColumn, educationColumn, skillsColumn;

    @FXML
    private TableColumn<ServiceOffre, Double> priceColumn;

    @FXML
    private TableColumn<ServiceOffre, String> datePostedColumn;

    private final ServiceOffreService serviceOffreService = new ServiceOffreService();
    private final ObservableList<ServiceOffre> serviceList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadServices();
    }

    private void setupTableColumns() {
        idServiceColumn.setCellValueFactory(new PropertyValueFactory<>("id_service"));
        idUserColumn.setCellValueFactory(new PropertyValueFactory<>("id_user"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        fieldColumn.setCellValueFactory(new PropertyValueFactory<>("field"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        experienceColumn.setCellValueFactory(new PropertyValueFactory<>("required_experience"));
        educationColumn.setCellValueFactory(new PropertyValueFactory<>("required_education"));
        skillsColumn.setCellValueFactory(new PropertyValueFactory<>("skills"));
        datePostedColumn.setCellValueFactory(new PropertyValueFactory<>("date_posted"));
    }

    private void loadServices() {
        List<ServiceOffre> services = serviceOffreService.getAll(); // Fetch all services
        serviceList.setAll(services);
        serviceTable.setItems(serviceList);
    }
}
