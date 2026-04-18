package com.randfacts;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.event.ActionEvent;

public class AboutUsController {

    // handles my clicks and redirects to system browser services
    @FXML
    private void handleLinkClick(ActionEvent event) {
        if (event.getSource() instanceof Hyperlink) {
            Hyperlink link = (Hyperlink) event.getSource();
            // retrieve URL from the text and opens my default zen browser firefox based btw
            String url = link.getText();
            MainApp.openLink(url);
            System.out.println("\u001b[34mbro found the easter egg\u001b[0m");
        }
    }
}
