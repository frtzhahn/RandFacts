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
import javafx.scene.media.Media;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer;
import java.net.URL;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.util.Duration;
import javafx.scene.CacheHint;

public class MainController {

    @FXML
    private StackPane contentArea;

    @FXML
    private Label navHomepage, navSavedFacts, navHistory, navDashboard, navAboutUs;

    @FXML
    private MediaView backgroundMediaView;

    private double xOffset = 0;
    private double yOffset = 0;

    // tracks previous page for back-navigation logic
    private String previousPagePath = "";

    @FXML
    public void initialize() {
        // terminal status for startup check
        System.out.println("\u001b[32mRandFacts Project: maven build success on your machine\u001b[0m");

        // load landing page by default
        loadPage("Homepage", navHomepage);

        try {
            URL mediaUrl = getClass().getResource("/com/randfacts/images/background-lean.mp4");
            if (mediaUrl != null) {
                Media media = new Media(mediaUrl.toExternalForm());
                MediaPlayer mediaPlayer = new MediaPlayer(media);

                // background video loop and autoplay feature
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.setMute(true);

                backgroundMediaView.setMediaPlayer(mediaPlayer);

                // performance optimizations
                backgroundMediaView.setSmooth(false); 
								// GPU caching and prioritizes speed
                backgroundMediaView.setCache(true);   
                backgroundMediaView.setCacheHint(CacheHint.SPEED); 

                mediaPlayer.play();

                System.out.println("\u001b[32mbackground video successfully loaded\u001b[0m");
            } else {
                System.err.println("\u001b[31mbackground video failed to load\u001b[0m");
            }
        } catch (Exception e) {
            System.err.println("\u001b[31mbackground video failed to load\u001b[0m");
            e.printStackTrace();
        }
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

		// fade in and out effect enhanced with gpu caching method
    public void loadPage(String page, Label activeNav) {
        try {
            String fxmlPath = page.equals("Homepage") ? "Homepage.fxml" : page + "Page.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newNode = loader.load();

            // provide main controller reference to the fragment controller
            Object controller = loader.getController();
            if (controller instanceof SavedFactsController) {
                ((SavedFactsController) controller).setMainController(this);
            } else if (controller instanceof HistoryController) {
                ((HistoryController) controller).setMainController(this);
            }

            if (activeNav != null) {
                setActiveNavItem(activeNav);
            }

            Node oldNode = contentArea.getChildren().isEmpty() ? null : contentArea.getChildren().get(0);

            if (oldNode != null) {
                // new node on top of the old one
                newNode.setOpacity(0.0);
                contentArea.getChildren().add(newNode);

                // enables bitmap caching to offload transparency math to the GPU
                oldNode.setCache(true);
                newNode.setCache(true);
                oldNode.setCacheHint(CacheHint.SPEED);
                newNode.setCacheHint(CacheHint.SPEED);

                // creates parallel animations
                FadeTransition fadeOut = new FadeTransition(Duration.millis(400), oldNode);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(400), newNode);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);

                ParallelTransition pt = new ParallelTransition(fadeOut, fadeIn);
                pt.setOnFinished(e -> {
                    // cleanup old resources after transition
                    contentArea.getChildren().remove(oldNode);
                    oldNode.setCache(false);
                    newNode.setCache(false);
                });
                pt.play();
            } else {
                // first time load during program launch
                contentArea.getChildren().setAll(newNode);
            }

            previousPagePath = fxmlPath;
            System.out.println("successful page load: " + fxmlPath);

        } catch (IOException e) {
            System.err.println("navigation error page failed to load: " + page);
            e.printStackTrace();
        }
    }

    //specialized loader for extended detail views with data passing
    public void loadExtendedPageWithData(String fxmlName, Fact fact, Label navToHighlight) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlName + ".fxml"));
            Parent root = loader.load();

            contentArea.getChildren().setAll(root);

            Object controller = loader.getController();
            if (controller instanceof FactDetailController) {
                FactDetailController detailController = (FactDetailController) controller;
                detailController.setMainController(this);
                detailController.setFactData(fact);
            }

            //dynamic highlighting on nav bars
            if (navToHighlight != null) {
                setActiveNavItem(navToHighlight);
            }

            System.out.println("navigation engine load active: " + fact.getTitle());
        } catch (IOException e) {
            System.err.println("navigation error load failure on: " + fxmlName);
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

    // getter for history controller to access
    public Label getNavHistory() {
        return navHistory;
    }

    //getter for savedfacts page to access
    public Label getNavSavedFacts() {
        return navSavedFacts;
    }
}
