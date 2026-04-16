package com.randfacts;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

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

				categoryChart.getData().clear();
				categoryChart.getData().add(series);
		}



}
