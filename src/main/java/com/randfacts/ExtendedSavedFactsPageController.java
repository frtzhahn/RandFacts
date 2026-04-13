package com.randfacts;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class ExtendedSavedFactsPageController implements FactDetailController {

    @FXML private Label titleLabel;
    @FXML private Label dataLabel;
    @FXML private TextArea contentTextArea;
    @FXML private VBox exitAuthOverlay;

    private MainController mainController;
    private boolean isModified = false;

    @FXML
    public void initialize() {
        // observer to track user edits in the text area
        contentTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            isModified = true;
        });
    }

    /**
     * populates UI elements with the provided fact data
     * ensures the modification flag is reset after population
     */
    public void setFactData(String title, String date, String content) {
        titleLabel.setText(title);
        dataLabel.setText(date);
        contentTextArea.setText(content);
        isModified = false;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleSave() {
        // commit changes to data storage here
        System.out.println("status: changes saved successfully");
        isModified = false;
    }

    @FXML
    private void handleExit() {
        if (isModified) {
            // activation of the unsaved changes overlay
            exitAuthOverlay.setVisible(true);
        } else {
            returnToListView();
        }
    }

    @FXML
    private void confirmExit() {
        // user chose to discard changes and exit
        isModified = false;
        returnToListView();
    }

    @FXML
    private void cancelExit() {
        // user cancelled exit to continue editing
        exitAuthOverlay.setVisible(false);
    }

    private void returnToListView() {
        if (mainController != null) {
            mainController.goToSavedFacts();
        }
    }
}
