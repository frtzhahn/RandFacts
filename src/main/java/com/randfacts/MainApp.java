package com.randfacts;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.text.Font;

public class MainApp extends Application{
	@Override
	public void start(Stage stage) throws Exception{
		Font pixelFont = Font.loadFont(getClass().getResourceAsStream("/com/randfacts/fonts/PixelifySans-Regular.ttf"), 20);
		
		//Loading the Navigation Shell
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/randfacts/MainView.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root, 1720, 880);

		stage.setScene(scene);
		stage.setTitle("RandFacts - Industrial Grade UI");
		stage.show();
    
	}

	public static void main(String[] args) {
		launch();

	}
}
