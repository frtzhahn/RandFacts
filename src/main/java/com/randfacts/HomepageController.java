package com.randfacts;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class HomepageController{

	@FXML
	private ComboBox<String> categoryComboBox;

	@FXML
	private Button generateButton;

	@FXML
	private Button saveButton;

	@FXML
	private Label factLabel;

	@FXML
	private Label titleLabel;

	private Fact currentFact;

	@FXML
	public void initialize(){
		ObservableList<String> categories=FXCollections.observableArrayList(
				"Programming", "History", "Science", "Physics", "Mathematics", "Politics", "Finance", "Philosophy", "Astronomy", "General");
		categoryComboBox.setItems(categories);

	}

	@FXML
	private void handleGenerate(){
		String selectedCategory = categoryComboBox.getValue();

		if(selectedCategory == null){
			factLabel.setText("Please Select a Category first");
			return;
		}

		// fact service call to store current fact
		this.currentFact = FactService.getInstance().generateFact(selectedCategory);

		// updating UIcomponents using data from fact object
		titleLabel.setText(currentFact.getTitle());
		factLabel.setText(currentFact.getContent());

		// terminal message to verify success
		System.out.println("Homepage: generated: " + currentFact.getTitle());

	}

	@FXML
	private void handleSave(){
			if(currentFact != null){
					FactService.getInstance().saveFact(currentFact);

					// UI feedback for ux
					String originalText = saveButton.getText();
					saveButton.setText("...");

					//prevents double clicks and ui bugs
					saveButton.setDisable(true);

					//1.5 sec delay
					PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
					pause.setOnFinished(event -> {
							saveButton.setText(originalText);
							saveButton.setDisable(false);
					});
					pause.play();

					System.out.println("Homepage: fact saved and UI effects triggered");
			}
	}


}

