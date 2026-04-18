package com.randfacts;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class ExtendedSavedFactsPageController implements FactDetailController {

    @FXML private Label titleLabel;
    @FXML private Label dataLabel;
    @FXML private TextArea contentTextArea;
    @FXML private VBox exitAuthOverlay;
    
    // buttons for industrial ui feedback
    @FXML private Button saveButton;
    @FXML private Button exitButton;

    private MainController mainController;
    private boolean isModified = false;
    private Fact currentFact;

    @FXML
    public void initialize() {
        // observer to track my edits in the text area
        contentTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            isModified = true;
        });
    }

    // populates ui elements with provided fact data and resets modification state
    @FXML
    @Override
    public void setFactData(Fact fact) {
        this.currentFact = fact;
        titleLabel.setText(fact.getTitle());
        dataLabel.setText(fact.getDate());
        contentTextArea.setText(fact.getContent());
        isModified = false;
    }

    // sets reference to main navigation controller
    @FXML
    @Override
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    // handles database synchronization with industrial success feedback
    @FXML
    private void handleSave() {
        // only trigger if a real data mutation is detected
        if (currentFact != null && isModified) {
            FactService.getInstance().updateSavedFact(currentFact, contentTextArea.getText());
            isModified = false;

            // swap icon for loading state similar to the save button on homepage
            String originalIcon = saveButton.getText(); 
            saveButton.setText("...");
            saveButton.setDisable(true);

            // 1.5 second success confirmation period
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(event -> {
                saveButton.setText(originalIcon);
                saveButton.setDisable(false);
                System.out.println("status: changes synchronized with local storage");
            });
            pause.play();
        }
    }

    // manages exit flow with unsaved changes detection
    @FXML
    private void handleExit() {
        if (isModified) {
            exitAuthOverlay.setVisible(true);
        } else {
            returnToListView();
        }
    }

    // confirms discard of changes and returns to list
    @FXML
    private void confirmExit() {
        isModified = false;
        returnToListView();
    }

    // cancels exit request and hides overlay
    @FXML
    private void cancelExit() {
        exitAuthOverlay.setVisible(false);
    }

    // triggers main controller navigation back to saved facts
    private void returnToListView() {
        if (mainController != null) {
            mainController.goToSavedFacts();
        }
    }
}
