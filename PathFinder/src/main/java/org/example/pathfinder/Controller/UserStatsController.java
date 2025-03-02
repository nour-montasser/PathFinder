package org.example.pathfinder.Controller;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import org.example.pathfinder.Service.UserService;

public class UserStatsController {

    @FXML
    private BarChart<String, Number> ageChart;

    private final UserService userService = new UserService();

    public void initialize() {
        ageChart.getXAxis().setLabel("Age Groups"); // Set X-axis label
        ageChart.getYAxis().setLabel("Number of Users"); // Set Y-axis label

        // Load data into the chart
        loadAgeData();
    }

    private void loadAgeData() {
        // Fetch user data and calculate age groups
        int count14to18 = userService.getUserCountByAgeGroup(14, 18);
        int count18to22 = userService.getUserCountByAgeGroup(18, 22);
        int count22to40 = userService.getUserCountByAgeGroup(22, 40);
        int count40to60 = userService.getUserCountByAgeGroup(40, 60);

        // Create a data series for the chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("14-18", count14to18));
        series.getData().add(new XYChart.Data<>("18-22", count18to22));
        series.getData().add(new XYChart.Data<>("22-40", count22to40));
        series.getData().add(new XYChart.Data<>("40-60", count40to60));

        // Add the series to the chart
        ageChart.getData().add(series);
    }
}