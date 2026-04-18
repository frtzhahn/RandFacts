package com.randfacts;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.geometry.Pos;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import java.util.List;

public class SavedFactsController {

    @FXML private VBox historyContainer;
    @FXML private VBox deleteConfirmationOverlay;

    private MainController mainController;
    private Fact factToBeDeleted;
    private HBox rowToBeDeleted;

    // preloaded images for highperformance hover animation
    private final Image binOpen = new Image(getClass().getResourceAsStream("/com/randfacts/images/bin-open.png"));
    private final Image binClose = new Image(getClass().getResourceAsStream("/com/randfacts/images/bin-close.png"));

    // loads saved facts from factservice
    @FXML
    public void initialize() {
        populateSavedFacts(FactService.getInstance().getSavedFacts());
    }

    // clears and repopulates the list container with fact cells
    private void populateSavedFacts(List<Fact> facts) {
        historyContainer.getChildren().clear();

        for (Fact fact : facts) {
            HBox cell = createFactCell(fact);
            historyContainer.getChildren().add(cell);
        }
    }

    // generates an individual fact cell with animated bin and preview hover logic
    private HBox createFactCell(Fact fact) {
        HBox cell = new HBox(20.0);
        cell.setAlignment(Pos.CENTER);
        cell.setPrefHeight(60.0);
        cell.setMinHeight(60.0);
        cell.setMaxHeight(60.0);
        cell.setStyle("-fx-cursor: hand; -fx-background-color: transparent;");

        // topic title display label (660px for preview space)
        Label topicLabel = new Label(fact.getTitle());
        topicLabel.getStyleClass().add("text-content");
        topicLabel.setPrefWidth(660); 
        topicLabel.setWrapText(false);
        topicLabel.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);
        topicLabel.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-padding: 0 20 0 20; -fx-background-radius: 15;");
        topicLabel.setMaxHeight(Double.MAX_VALUE);

        // creation date display label (reduced for balance)
        Label dateLabel = new Label(fact.getDate());
        dateLabel.getStyleClass().add("text-content");
        dateLabel.setPrefWidth(190); 
        dateLabel.setWrapText(false);
        dateLabel.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);
        dateLabel.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-padding: 0 20 0 20; -fx-background-radius: 15;");
        dateLabel.setMaxHeight(Double.MAX_VALUE);

        // animated bin asset
        ImageView binImageView = new ImageView(binClose);
        binImageView.setFitWidth(30.0);
        binImageView.setFitHeight(30.0);
        binImageView.setPreserveRatio(true);

        // delete button 
        Button deleteBtn = new Button();
        deleteBtn.setGraphic(binImageView);
        deleteBtn.setOpacity(0.0);
        deleteBtn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0; -fx-cursor: hand;");

        // toggle bin visibility, image swap, and trigger preview dissolve on hover
        cell.setOnMouseEntered(e -> {
            deleteBtn.setOpacity(1.0);
            binImageView.setImage(binOpen);
           
						// manages dissolve effect on cell hover
            FadeTransition fadeOut = new FadeTransition(Duration.millis(75), topicLabel);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> {
                String preview = fact.getContent().replace("Random Fact: ", "").replace("\n", " ").trim();
                if (preview.length() > 75) preview = preview.substring(0, 75) + "...";
                topicLabel.setText(preview);
                
                FadeTransition fadeIn = new FadeTransition(Duration.millis(75), topicLabel);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            fadeOut.play();
        });

        cell.setOnMouseExited(e -> {
            deleteBtn.setOpacity(0.0);
            binImageView.setImage(binClose);
            
            // restore original title with fast dissolve
            FadeTransition fadeOut = new FadeTransition(Duration.millis(75), topicLabel);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> {
                topicLabel.setText(fact.getTitle());
                FadeTransition fadeIn = new FadeTransition(Duration.millis(75), topicLabel);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            fadeOut.play();
        });

        // trigger delete prompt overlay
        deleteBtn.setOnAction(e -> {
            this.factToBeDeleted = fact;
            this.rowToBeDeleted = cell;
            deleteConfirmationOverlay.setVisible(true);
        });

        // handle navigation to extended detail view
        cell.setOnMouseClicked(event -> {
            if (event.getTarget() != deleteBtn && mainController != null) {
                mainController.loadExtendedPageWithData("ExtendedSavedFactsPage", fact,  mainController.getNavSavedFacts());
            }
        });

        cell.getChildren().addAll(topicLabel, dateLabel, deleteBtn);
        return cell;
    }

    // closes the delete confirmation pop up overlay
    @FXML
    private void cancelDelete() {
        deleteConfirmationOverlay.setVisible(false);
        this.factToBeDeleted = null;
        this.rowToBeDeleted = null;
    }

    // executes backend deletion and runs removal animation
    @FXML
    private void confirmDelete() {
        if (factToBeDeleted != null && rowToBeDeleted != null) {
            FactService.getInstance().deleteFactPermanently(factToBeDeleted);

            FadeTransition ft = new FadeTransition(Duration.millis(300), rowToBeDeleted);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.setOnFinished(e -> historyContainer.getChildren().remove(rowToBeDeleted));
            ft.play();

            deleteConfirmationOverlay.setVisible(false);
        }
    }

    // reference setter for navigation logic
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
