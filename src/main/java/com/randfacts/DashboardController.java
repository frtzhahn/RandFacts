package com.randfacts;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;
import java.util.Map;

public class DashboardController {

    @FXML private BarChart<String, Number> categoryChart;
    @FXML private CategoryAxis xAxis;
    @FXML private ComboBox<String> viewToggle;
    @FXML private Label chartHeaderLabel;

    // axis precision settings
    @FXML
    public void initialize() {
        // axis rotation and animation set up
        xAxis.setTickLabelRotation(45);
        xAxis.setAnimated(false); 
        viewToggle.setItems(FXCollections.observableArrayList("GENERAL", "HISTORY", "SAVED FACTS"));
        viewToggle.setValue("GENERAL");

        // listener for real-time data fetch on project db
        viewToggle.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateChartHeader(newVal);
                updateChartData(newVal);
            }
        });

        // data loadss
        updateChartData("GENERAL");
    }

    // updates the mini header for dynamic purposes
    private void updateChartHeader(String viewName) {
        chartHeaderLabel.setText("FREQUENT CATEGORY SEARCHES - " + viewName);
    }

    // refreshes chart data with surgical axis synchronization
    private void updateChartData(String mode) {
        // invalidates existing categories to prevent misalignment and ui bugs of course
        xAxis.getCategories().clear();
        categoryChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(mode);

        // fetch filtered statistics from factservice
        Map<String, Integer> stats = FactService.getInstance().getCategoryStats(mode);

        // populate series with data points
        stats.forEach((category, count) -> {
            series.getData().add(new XYChart.Data<>(category, count));
        });

        // injects new data series
        categoryChart.getData().add(series);
        
        System.out.println("\u001b[34mdashboard: " + mode + "\u001b[0m");
    }
}
