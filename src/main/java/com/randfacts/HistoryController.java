package com.randfacts;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class HistoryController{
		
		@FXML
		private VBox historyContainer;
		private MainController mainController;

		public void setMainController(MainController mainController){
			this.mainController = mainController;

		}

		@FXML
		public void initialize(){
			java.util.List<Fact> mockHistory = java.util.Arrays.asList(
					new Fact("History Fact 1", "mock data test for history page", "2026-04-10"),
					new Fact("History Fact 1", "mock data test for history page", "2026-04-10")
					);                                                          

			loadHistoryItems(mockHistory);

		}

		private void loadHistoryItems(java.util.List<Fact> historyList){
			historyContainer.getChildren().clear();

			for(Fact fact: historyList){
				// creating rows
				javafx.scene.layout.HBox row = new javafx.scene.layout.HBox();
				row.setAlignment(javafx.geometry.Pos.CENTER);
				row.setSpacing(20.0);
				// topic labels
				javafx.scene.control.Label titleLabel= new javafx.scene.control.Label(fact.getTitle());
				titleLabel.getStyleClass().add("text-content");
				titleLabel.setPrefWidth(480);
				titleLabel.setWrapText(true);
				titleLabel.setStyle("-fx-background-color: rgba(225,225,225,0.05); -fx-padding: 20; -fx-background-radius: 15;");
				//topic date on labels
				javafx.scene.control.Label dateLabel= new javafx.scene.control.Label(fact.getDate());
				dateLabel.getStyleClass().add("text-content");
				dateLabel.setPrefWidth(480);
				dateLabel.setWrapText(true);
				dateLabel.setStyle("-fx-background-color: rgba(225,225,225,0.05); -fx-padding: 20; -fx-background-radius: 15;");
				// adds labels to row and the row to the  container
				row.getChildren() .addAll(titleLabel, dateLabel);
				historyContainer.getChildren().add(row);

				row.setOnMouseClicked(event -> {
						if(mainController != null){
								mainController.loadExtendedPageWithData("ExtendedHistoryPage", fact, mainController.getNavHistory());
						}
				});


			}



		}
}
