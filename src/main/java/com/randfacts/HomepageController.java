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
	public void initialize(){
		ObservableList<String> categories=FXCollections.observableArrayList(
				"Programming", "History", "Science", "Physics", "Mathematics", "Politics", "Finance", "Philosophy", "Astronomy", "General");
		categoryComboBox.setItems(categories);

		// debug message
		System.out.println("Homepage Engine: Fragment initialize categories loaded");

	}

	@FXML
	private void handleGenerate(){
		String selectedCategory = categoryComboBox.getValue();

		if(selectedCategory == null){
			factLabel.setText("Please Select a Category first");
			return;
		}

		factLabel.setText("you selected " + selectedCategory + "! The AI engine is ready to fire.");
		System.out.println("Generate Trigger: Category [" +  selectedCategory +"] selected");

	}

	@FXML
	private void handleSave(){
		System.out.println("Save Trigger: Engine ready to log facts");
	}


}

