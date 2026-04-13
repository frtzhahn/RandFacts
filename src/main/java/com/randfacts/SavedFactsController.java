package com.randfacts;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import java.util.ArrayList;
import java.util.List;

public class SavedFactsController {

    @FXML
    private VBox historyContainer;

    private MainController mainController;

    // mock list of dummy data for functionality testing
    private List<Fact> savedFacts = new ArrayList<>();

    @FXML
    public void initialize() {
        // population of mock data for testing clicks
        savedFacts.add(new Fact("the origin of coffee", "coffee was discovered by a goat herder in ethiopia after he noticed his goats became energetic.", "2026-04-01 10:30:00"));
        savedFacts.add(new Fact("honey never spoils", "archaeologists have found pots of honey in ancient egyptian tombs that are over 3,000 years old.", "2026-04-02 14:15:00"));
        savedFacts.add(new Fact("octopus hearts", "an octopus has three hearts and blue blood.", "2026-04-03 09:45:00"));

        populateSavedFacts();
    }

    /**
     * dynamically generates cell rows from the mock data list
     * adds click listeners to transition into the extended view
     */
    private void populateSavedFacts() {
        historyContainer.getChildren().clear();

        for (Fact fact : savedFacts) {
            HBox cell = createFactCell(fact);
            historyContainer.getChildren().add(cell);
        }
    }

    private HBox createFactCell(Fact fact) {
        HBox cell = new HBox(20.0);
        cell.setAlignment(javafx.geometry.Pos.CENTER);
        cell.setPrefHeight(70.0);
        cell.setMinHeight(70.0);
        cell.setMaxHeight(70.0);
        cell.setStyle("-fx-cursor: hand;");

        // topic label with truncation and locking
        Label topicLabel = new Label(fact.getTitle());
        topicLabel.getStyleClass().add("text-content");
        topicLabel.setPrefWidth(480);
        topicLabel.setWrapText(false);
        topicLabel.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);
        topicLabel.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-padding: 0 20 0 20; -fx-background-radius: 15;");
        topicLabel.setMaxHeight(Double.MAX_VALUE);

        // date label for consistency across rows
        Label dateLabel = new Label(fact.getDate());
        dateLabel.getStyleClass().add("text-content");
        dateLabel.setPrefWidth(480);
        dateLabel.setWrapText(false);
        dateLabel.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);
        dateLabel.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-padding: 0 20 0 20; -fx-background-radius: 15;");
        dateLabel.setMaxHeight(Double.MAX_VALUE);

        // implementation of click logic for cell transition
        cell.setOnMouseClicked(event -> {
            if (mainController != null) {
                mainController.loadExtendedPageWithData("ExtendedSavedFactsPage", fact,  mainController.getNavSavedFacts());
            }
        });

        cell.getChildren().addAll(topicLabel, dateLabel);
        return cell;
    }

    //reference setter to allow main controller navigation calls
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
