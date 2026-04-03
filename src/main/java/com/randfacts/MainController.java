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

	// tracks previous page for back-navigation logic
	private String previousPagePath = "";

	@FXML
	public void initialize(){
		// terminal status for startup check
		System.out.println("\u001b[32mRandFacts Project: maven build success on your machine\u001b[0m");
		
		// load landing page by default
		loadPage("Homepage", navHomepage);
	}

	@FXML
	public void goToHomepage() { 
		loadPage("Homepage", navHomepage);
	}

	@FXML
	public void goToSavedFacts() { 
		loadPage("SavedFacts", navSavedFacts);
	}

	@FXML
	public void goToHistory() { 
		loadPage("History", navHistory);
	}

	@FXML
	public void goToDashboard() { 
		loadPage("Dashboard", navDashboard);
	}

	@FXML
	public void goToAboutUs() { 
		loadPage("AboutUs", navAboutUs);
	}

	/**
	 * handles fragment swapping and navigation highlighting
	 * keeps highlighting active even if child fragments are loaded
	 */
	public void loadPage(String page, Label activeNav) {
		try {
			// determine path based on standard naming convention
			String fxmlPath = page.equals("Homepage") ? "Homepage.fxml" : page + "Page.fxml";
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
			Parent root = loader.load();

			// commit fragment swap and update history
			contentArea.getChildren().setAll(root);
			previousPagePath = fxmlPath;
			
			// provide main controller reference to the fragment controller
			Object controller = loader.getController();
			if (controller instanceof SavedFactsController) {
				((SavedFactsController) controller).setMainController(this);
			}

			if (activeNav != null) {
				setActiveNavItem(activeNav);
			}

			System.out.println("navigation engine: loaded " + fxmlPath);
		} catch (IOException e) {
			System.err.println("engine error: failed to load " + page);
			e.printStackTrace();
		}
	}

	/**
	 * specialized loader for extended detail views with data passing
	 */
	public void loadExtendedPageWithData(String page, Fact fact) {
		try {
			String fxmlPath = page + ".fxml";
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
			Parent root = loader.load();

			contentArea.getChildren().setAll(root);
			
			// data handoff to the extended view controller
			ExtendedSavedFactsPageController controller = loader.getController();
			controller.setMainController(this);
			controller.setFactData(fact.getTitle(), fact.getDate(), fact.getContent());

			// maintain highlighting for the respective section
			setActiveNavItem(navSavedFacts);

			System.out.println("navigation engine: detailed view active -> " + fact.getTitle());
		} catch (IOException e) {
			System.err.println("engine error: detailed load failure -> " + page);
			e.printStackTrace();
		}
	}

	private void setActiveNavItem(Label activeLabel) {
		Label[] navLabels = {navHomepage, navSavedFacts, navHistory, navDashboard, navAboutUs};
		for (Label label : navLabels) {
			label.getStyleClass().remove("active");
		}
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
}
