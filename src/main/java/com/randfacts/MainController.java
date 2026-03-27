package com.randfacts;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
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

		System.out.println("\u001b[32mRandFacts Project: maven build success on your machine\u001b[0m");

		// Load Homepage by default
		loadPage("Homepage.fxml");
		setActiveNav(navHomepage);

	}

	private void setActiveNav(Label activeNav) {
		navHomepage.getStyleClass().remove("active");
		navSavedFacts.getStyleClass().remove("active");
		navHistory.getStyleClass().remove("active");
		navDashboard.getStyleClass().remove("active");
		navAboutUs.getStyleClass().remove("active");
		
		activeNav.getStyleClass().add("active");
	}

	private void loadPage(String fxml) {
		try {
			Parent page = FXMLLoader.load(getClass().getResource("/com/randfacts/" + fxml));
			contentArea.getChildren().setAll(page);
		} catch (IOException e) {
			System.err.println("Error loading page: " + fxml);
			e.printStackTrace();
		}
	}

	@FXML
	private void goToHomepage(MouseEvent event) {
		loadPage("Homepage.fxml");
		setActiveNav(navHomepage);
	}

	@FXML
	private void goToSavedFacts(MouseEvent event) {
		loadPage("SavedFactsPage.fxml");
		setActiveNav(navSavedFacts);
	}

	@FXML
	private void goToHistory(MouseEvent event) {
		loadPage("HistoryPage.fxml");
		setActiveNav(navHistory);
	}

	@FXML
	private void goToDashboard(MouseEvent event) {
		loadPage("DashboardPage.fxml");
		setActiveNav(navDashboard);
	}

	@FXML
	private void goToAboutUs(MouseEvent event) {
		loadPage("AboutUsPage.fxml");
		setActiveNav(navAboutUs);
	}

	@FXML
	private void minimizeWindow(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setIconified(true);
	}

	@FXML
	private void maximizeWindow(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setMaximized(!stage.isMaximized());
	}

	@FXML
	private void closeWindow(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	@FXML
	private void onMousePressed(MouseEvent event) {
		xOffset = event.getSceneX();
		yOffset = event.getSceneY();
	}

	@FXML
	private void onMouseDragged(MouseEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		if (!stage.isMaximized()) {
			stage.setX(event.getScreenX() - xOffset);
			stage.setY(event.getScreenY() - yOffset);
		}
	}
} 
 
