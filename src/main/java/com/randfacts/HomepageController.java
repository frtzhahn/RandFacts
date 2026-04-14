package com.randfacts;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

		// fact service connection
		Fact newFact = FactService.getInstance().generateFact(selectedCategory);

		// updating UIcomponents using data from fact object
		titleLabel.setText(newFact.getTitle());
		factLabel.setText(newFact.getContent());

		System.out.println("Homepage: generated: " + newFact.getTitle());

	}

	@FXML
	private void handleSave(){
		System.out.println("Saved Facts: ready to log facts");
	}


}

