package org.example.pathfinder.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import org.example.pathfinder.Service.ServiceOffreService;

import java.util.Map;

public class FreelancerDashboardController {

    @FXML
    private PieChart revenuePieChart; // Pie Chart UI Element

    private final ServiceOffreService serviceOffreService = new ServiceOffreService();
    private static final int HARD_CODED_FREELANCER_ID = 10; // Change this for testing different users

    @FXML
    public void initialize() {
        System.out.println("🔍 Loading revenue chart...");
        loadRevenueChart();
    }

    private void loadRevenueChart() {
        // Fetch revenue data from database
        Map<String, Double> revenueMap = serviceOffreService.getRevenueBreakdownForFreelancer(HARD_CODED_FREELANCER_ID);

        // Check if there is data
        if (revenueMap.isEmpty()) {
            System.out.println("⚠ No revenue data found!");
            return;
        }

        // Convert the Map into PieChart Data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : revenueMap.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        System.out.println("📊 Updating Pie Chart with " + pieChartData.size() + " entries.");
        // Set data to the PieChart
        revenuePieChart.setData(pieChartData);
    }

}
