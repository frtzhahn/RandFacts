package com.randfacts;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import java.util.Map;

public class DashboardController{

		@FXML
		private BarChart<String, Number> categoryChart;

		@FXML
		public void initialize(){
				updateChartData();
		}

		private void updateChartData(){
				XYChart.Series<String, Number> series = new XYChart.Series<>();
				series.setName("Searches");
				// fetches facts from factservice
				Map<String, Integer> stats = FactService.getInstance().getCategoryStats();
				// lamba integration to populate chart data points
				stats.forEach((category, count) ->{
						series.getData().add(new XYChart.Data<>(category, count));

				});

				categoryChart.getData().clear();
				categoryChart.getData().add(series);
		}



}
