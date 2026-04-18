package com.randfacts;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;


public class HomepageController{

	@FXML
	private ComboBox<String> categoryComboBox;

	@FXML
	private Button generateButton;

	@FXML
	private Button saveButton;

	@FXML
	private Label factLabel;

	@FXML
	private Label titleLabel;

	private Fact currentFact;

	@FXML
	private Label typingLabel;

	private int stringIndex = 0;
	private int charIndex = 0;
	private final String[] messages = {
			"How should we start today?",
			"Don't forget to save your facts",
			"Always double check everything you read"
	};

	@FXML
	public void initialize(){
		ObservableList<String> categories=FXCollections.observableArrayList(
				"Programming", "History", "Science", "Physics", "Mathematics", "Politics", "Finance", "Philosophy", "Astronomy", "General", "Relationships", "Human Nature", "Psychology");
				categoryComboBox.setItems(categories);

				this.currentFact = FactService.getInstance().getLatestFact();
				if(currentFact != null){
						titleLabel.setText(currentFact.getTitle());
						factLabel.setText(currentFact.getContent());
				}

				// typing animation
				startTypingAnimation();
	}

	@FXML
	private void handleGenerate(){
		String selectedCategory = categoryComboBox.getValue();

		if(selectedCategory == null){
			factLabel.setText("Please Select a Category first");
			return;
		}

		generateButton.setDisable(true);
		factLabel.setText("Aight bro gemini is about to give a pretty niche content on " + selectedCategory + "...");

		FactService.getInstance().generateFactFromAI(selectedCategory)
				.thenAccept(newFact -> {
						javafx.application.Platform.runLater(() -> {
								this.currentFact = newFact;
								titleLabel.setText(currentFact.getTitle());
								factLabel.setText(currentFact.getContent());

								generateButton.setDisable(false);
								System.out.println("\u001b[31mhomepage: gemini generated fact recieved\u001b[0m");
						});
				})
					.exceptionally(ex -> {
							javafx.application.Platform.runLater(() -> {
									factLabel.setText("error fetching fact: " + ex.getMessage());
									generateButton.setDisable(false);
							});
							return null;
					});


	}

	@FXML
	private void handleSave(){
			if(currentFact != null){
					FactService.getInstance().saveFact(currentFact);

					// UI feedback for ux
					String originalText = saveButton.getText();
					saveButton.setText("...");

					//prevents double clicks and ui bugs
					saveButton.setDisable(true);

					//1.5 sec delay
					PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
					pause.setOnFinished(event -> {
							saveButton.setText(originalText);
							saveButton.setDisable(false);
					});
					pause.play();

					System.out.println("Homepage: fact saved and UI effects triggered");
			}
	}

	private void startTypingAnimation(){
			Timeline timeline = new Timeline();

			KeyFrame keyFrame = new KeyFrame(Duration.millis(100), event -> {
					String currentMessage = messages[stringIndex];

					if(charIndex <= currentMessage.length()){
							typingLabel.setText(currentMessage.substring(0, charIndex));
							charIndex ++;
					}
					else{
						timeline.pause();

						PauseTransition pause = new PauseTransition(Duration.seconds(2));
						pause.setOnFinished(e -> {
								charIndex = 0;
								stringIndex = (stringIndex + 1) % messages.length;
								timeline.play();
						});
						pause.play();
					}

			});

			timeline.getKeyFrames().add(keyFrame);
			timeline.setCycleCount(Timeline.INDEFINITE);
			timeline.play();
	}


}

