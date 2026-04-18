package com.randfacts;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.text.Font;

public class MainApp extends Application {
    
    // static reference to system services for global hyperlink access
    private static HostServices hostServices;

    @Override
    public void start(Stage stage) throws Exception {
        // capture host services on my local device (this is for url easter egg ahh)
        hostServices = getHostServices();

				// loads up the local fonts
        Font.loadFont(getClass().getResourceAsStream("/com/randfacts/fonts/PixelifySans-SemiBold.ttf"), 20);
        Font.loadFont(getClass().getResourceAsStream("/com/randfacts/fonts/PixelifySans-Regular.ttf"), 20);
        Font.loadFont(getClass().getResourceAsStream("/com/randfacts/fonts/FiraCode-Regular.ttf"), 20);
        Font.loadFont(getClass().getResourceAsStream("/com/randfacts/fonts/FiraCode-Bold.ttf"), 20);
        
        // loading the navigation shell
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/randfacts/MainView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1820, 980);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setTitle("RandFacts");
        stage.show();
    }

    // global accessor for opening external documents in my default browser
    public static void openLink(String url) {
        if (hostServices != null) {
            hostServices.showDocument(url);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
