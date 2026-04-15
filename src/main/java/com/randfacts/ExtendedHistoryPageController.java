package com.randfacts;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class ExtendedHistoryPageController implements FactDetailController{
		@FXML private Label titleLabel;
		@FXML private Label dataLabel;
		@FXML private TextArea contentTextArea;

		private MainController mainController;

		@Override
		public void setFactData(Fact fact){
				titleLabel.setText(fact.getTitle());
				dataLabel.setText(fact.getDate());
				contentTextArea.setText(fact.getContent());
		}

		@Override
		public void setMainController(MainController mainController){
				this.mainController = mainController;
		}

		@FXML
		public void handleExit(){
				if(mainController != null){
						mainController.goToHistory();
				}
		}
}
