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
		public void setFactData(String title, String date, String content){
				titleLabel.setText(title);
				dataLabel.setText(date);
				contentTextArea.setText(content);
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
