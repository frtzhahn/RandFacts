// helps the JVM to locate what file to run 
package com.randfacts;

//features from javafx that this program going to include in the MainApp.java when running from JVM
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

//class declaration and declares that this program is now a javafx program
//the extends keyword transforms this class into a javafx engine and this class applies the inheritance concept in java programming
public class MainApp extends Application{

	// where the UI is built and override annotation for safety net
	@Override
	public void start(Stage stage) throws Exception{
		// String javaVersion = System.getProperty("java.version");
		// String javafxVersion = System.getProperty("javafx.version");

		// UI Components: Creating the 'Leaves' of the Scene Graph.
		// Label l = new Label("Hello from mocha " + javafxVersion + ", running on Java in my machine which is cachyOS kde plasma and the java version i am using is " + javaVersion + "."); 

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/randfacts/MainView.fxml"));
		Parent root = loader.load();

		// Layout & Scene: The 'Organizer' and 'Canvas' that hold the component.		
		Scene scene = new Scene(root, 1720, 880);

		// Rendering: Attaching the Canvas to the Frame and showing it to the OS.
		stage.setScene(scene);
		stage.setTitle("RandFacts on linux");
		stage.show();
	
	}

	// the main method and also the only method to launch the UI
	public static void main(String[] args){
		launch();
	}
}
