package org.example.pathfinder.Controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.pathfinder.Model.LoggedUser;
import org.example.pathfinder.Model.ServiceOffre;
import org.example.pathfinder.Service.ServiceOffreService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.example.pathfinder.Model.ServiceOffre;
import org.example.pathfinder.Service.ServiceOffreService;

public class ServiceListCardController {
    @FXML private Label titleLabel;
    @FXML private Label priceLabel;
    @FXML private Label experienceLabel;
    @FXML private Button applyButton;

    private ServiceOffre service;

    public void setService(ServiceOffre service) {
        this.service = service;
        titleLabel.setText(service.getTitle());
        priceLabel.setText("$" + service.getPrice());
        experienceLabel.setText(service.getExperience_level());

        applyButton.setOnAction(event -> applyForService());
    }

    private void applyForService() {
        System.out.println("Applying for: " + service.getTitle());
    }
}
