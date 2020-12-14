package controllers;

import application.Main;
import application.PigGame;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

// gets username for display during game play and for history record keeping
public class GetNameController {
	public Main main = Main.getSource();
	@FXML
	private TextField nameEntry;
	@FXML
	private Button playGameButton;
	@FXML
	private Button exitButton;

	@FXML
	protected void initialize() {
		if (PigGame.getActiveGame() == null) {
			PigGame.newActiveGame();
		}
	}

	// Navigation
	public void showHome() {
		PigGame.deleteActiveGame();
		main.changeScene("Home.fxml");
	}
	public void showSettings() {
		main.changeScene("Settings.fxml");
	}

	// Save name and navigate to game
	public void showGame() {
		if (nameEntry.getText().isBlank()) {
			PigGame.getActiveGame().setPlayerName("User"); // Default name
		} else {
			PigGame.getActiveGame().setPlayerName(nameEntry.getText());
		}
		main.changeScene("Game.fxml");
	}
}
