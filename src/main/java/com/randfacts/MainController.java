package com.randfacts;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;

public class MainController{

	@FXML
	private StackPane contentArea;

	@FXML
	private Label navHomepage, navSavedFacts, navHistory, navDashboard, navAboutUs;

	private double xOffset = 0;
	private double yOffset = 0;

	@FXML
	public void initialize(){
		// terminal success project build message
		System.out.println("\u001b[32mRandFacts Project: maven build success on your machine\u001b[0m");
		// Reference: RandFacts/src/main/resources/com/randfacts/Homepage.fxml
		loadPage("Homepage");
		setActiveNavItem(navHomepage);
	}

	@FXML
	private void goToHomepage() { 
		loadPage("Homepage"); 
		setActiveNavItem(navHomepage);
	}

	@FXML
	private void goToSavedFacts() { 
		loadPage("SavedFacts"); 
		setActiveNavItem(navSavedFacts);
	}

	@FXML
	private void goToHistory() { 
		loadPage("History"); 
		setActiveNavItem(navHistory);
	}

	@FXML
	private void goToDashboard() { 
		loadPage("Dashboard"); 
		setActiveNavItem(navDashboard);
	}

	@FXML
	private void goToAboutUs() { 
		loadPage("AboutUs"); 
		setActiveNavItem(navAboutUs);
	}

	private void setActiveNavItem(Label activeLabel) {
		// Reset all labels to default state
		Label[] navLabels = {navHomepage, navSavedFacts, navHistory, navDashboard, navAboutUs};
		for (Label label : navLabels) {
			label.getStyleClass().remove("active");
		}
		// Apply active class to the clicked label
		if (!activeLabel.getStyleClass().contains("active")) {
			activeLabel.getStyleClass().add("active");
		}
	}

	@FXML
	private void closeWindow(javafx.event.ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	@FXML
	private void minimizeWindow(javafx.event.ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setIconified(true);
	}

	@FXML
	private void maximizeWindow(javafx.event.ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setMaximized(!stage.isMaximized());
	}

	@FXML
	private void onMousePressed(MouseEvent event) {
		xOffset = event.getSceneX();
		yOffset = event.getSceneY();
	}

	@FXML
	private void onMouseDragged(MouseEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setX(event.getScreenX() - xOffset);
		stage.setY(event.getScreenY() - yOffset);
	}

	private void loadPage(String page){
		try{
			String fxmlPath = page.equals("Homepage") ? "Homepage.fxml" : page + "Page.fxml";

			Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));

			contentArea.getChildren().setAll(root);
			System.out.println("Navigation Engine: Successfully loaded " + fxmlPath);
		}

		catch(IOException e){
			System.err.println("Error: Could not load the page " + page);
			e.printStackTrace();
		}


	}
} 
