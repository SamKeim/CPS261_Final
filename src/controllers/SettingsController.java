package controllers;

import application.Difficulty;
import application.Main;
import application.PigGame;
import application.Speed;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;

public class SettingsController {
	public Main main = Main.getSource();
	@FXML
	private RadioButton easy;
	@FXML
	private RadioButton medium;
	@FXML
	private RadioButton hard;
	@FXML
	private CheckBox fastForward;
	private Difficulty setDifficulty = Difficulty.MEDIUM;
	private Speed setSpeed = Speed.REGULAR;

	@FXML
	protected void initialize() {
		// set defaults based on existing settings, if available
		if (PigGame.getActiveGame().isCustomSettings()) {
			switch (PigGame.getActiveGame().getDifficulty()) {
			case EASY:
				easy.setSelected(true);
				break;
			case MEDIUM:
				medium.setSelected(true);
				break;
			case HARD:
				hard.setSelected(true);
				break;
			}
			switch (PigGame.getActiveGame().getSpeed()) {
			case REGULAR:
				fastForward.setSelected(false);
				break;
			case FAST:
				fastForward.setSelected(true);
				break;
			}
		} else {
			medium.setSelected(true);
			fastForward.setSelected(false);
		}

		// listeners
		easy.selectedProperty().addListener((e) -> setDifficulty = Difficulty.EASY);
		medium.selectedProperty().addListener((e) -> setDifficulty = Difficulty.MEDIUM);
		hard.selectedProperty().addListener((e) -> setDifficulty = Difficulty.HARD);
		fastForward.selectedProperty().addListener((observable, wasChecked, isChecked) -> {
			if (isChecked) {
				setSpeed = Speed.FAST;
			} else {
				setSpeed = Speed.REGULAR;
			}
		});
	}

	// method for "Apply" button, applies settings and navigates back to GetName screen
	public void showGetName() {
		setDifficulty();
		setFastForward();
		main.changeScene("GetName.fxml");
	}
	
	// back to default settings
	public void reset() {
		setDifficulty = Difficulty.MEDIUM;
		setSpeed = Speed.REGULAR;
		medium.setSelected(true);
		fastForward.setSelected(false);
	}

	public void setFastForward() {
		PigGame.getActiveGame().setSpeed(setSpeed);
	}

	public void setDifficulty() {
		PigGame.getActiveGame().setDifficulty(setDifficulty);
	}
}
