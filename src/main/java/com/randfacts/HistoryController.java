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

public class HistoryController {
    
    @FXML private VBox historyContainer;
    @FXML private VBox deleteConfirmationOverlay;
    
    private MainController mainController;
    private Fact factToBeDeleted;
    private HBox rowToBeDeleted;

    // preloads images for high performance hover animation and to reduce lag on my old ahh laptop
    private final Image binOpen = new Image(getClass().getResourceAsStream("/com/randfacts/images/bin-open.png"));
    private final Image binClose = new Image(getClass().getResourceAsStream("/com/randfacts/images/bin-close.png"));

    // sets the main controller reference for navigation
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    // initializes view and loads existing facts
    @FXML
    public void initialize() {
        loadHistoryItems(FactService.getInstance().getHistory());
    }

    // generates dynamic rows for the history list with preview hover logic
    private void loadHistoryItems(java.util.List<Fact> historyList) {
        historyContainer.getChildren().clear();

        for(Fact fact : historyList) {
            HBox cell = new HBox(20.0);
            cell.setAlignment(Pos.CENTER);
						cell.setPrefHeight(60.0);
						cell.setMinHeight(60.0);
						cell.setMaxHeight(60.0);
            cell.setStyle("-fx-background-color: transparent;");

            // category display label (660px for preview space)
            Label titleLabel = new Label(fact.getTitle());
            titleLabel.getStyleClass().add("text-content");
            titleLabel.setPrefWidth(660); 
            titleLabel.setWrapText(false);
            titleLabel.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);
            titleLabel.setStyle("-fx-background-color: rgba(225,225,225,0.05); -fx-padding: 0 20 0 20; -fx-background-radius: 15;");
            titleLabel.setMaxHeight(Double.MAX_VALUE);

            // date display label (reduced for balance)
            Label dateLabel = new Label(fact.getDate());
            dateLabel.getStyleClass().add("text-content");
            dateLabel.setPrefWidth(190); 
            dateLabel.setWrapText(false);
            dateLabel.setStyle("-fx-background-color: rgba(225,225,225,0.05); -fx-padding: 0 20 0 20; -fx-background-radius: 15;");
            dateLabel.setMaxHeight(Double.MAX_VALUE);

            // animated bin asset
            ImageView binImageView = new ImageView(binClose);
            binImageView.setFitWidth(30.0);
            binImageView.setFitHeight(30.0);
            binImageView.setPreserveRatio(true);

            // industrial delete button 
            Button deleteBtn = new Button();
            deleteBtn.setGraphic(binImageView);
            deleteBtn.setOpacity(0.0);
            deleteBtn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0; -fx-cursor: hand;");

            // toggle delete bin, swap image, and trigger preview dissolve on hover
            cell.setOnMouseEntered(e -> {
                deleteBtn.setOpacity(1.0);
                binImageView.setImage(binOpen);
                
                // dissolve text effect on hovering cells
                FadeTransition fadeOut = new FadeTransition(Duration.millis(75), titleLabel);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(event -> {
                    String preview = fact.getContent().replace("Random Fact: ", "").replace("\n", " ").trim();
                    if (preview.length() > 75) preview = preview.substring(0, 75) + "...";
                    titleLabel.setText(preview);
                    
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(75), titleLabel);
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
                FadeTransition fadeOut = new FadeTransition(Duration.millis(75), titleLabel);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(event -> {
                    titleLabel.setText(fact.getTitle());
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(75), titleLabel);
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();
                });
                fadeOut.play();
            });

            // triggers the delete confirmation prompt
            deleteBtn.setOnAction(e -> {
                this.factToBeDeleted = fact;
                this.rowToBeDeleted = cell;
                deleteConfirmationOverlay.setVisible(true);
            });

            // navigate to extended view on click
            cell.setOnMouseClicked(event -> {
                if (event.getTarget() != deleteBtn && mainController != null) {
                    mainController.loadExtendedPageWithData("ExtendedHistoryPage", fact, mainController.getNavHistory());
                }
            });

            cell.getChildren().addAll(titleLabel, dateLabel, deleteBtn);
            historyContainer.getChildren().add(cell);
        }
    }

    // closes the delete confirmation overlay
    @FXML
    private void cancelDelete() {
        deleteConfirmationOverlay.setVisible(false);
        this.factToBeDeleted = null;
        this.rowToBeDeleted = null;
    }

    // performs absolute deletion and runs exit animation
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
}
