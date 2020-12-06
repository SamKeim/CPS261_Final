package controllers;

import application.Main;
import application.PigGame;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class GetNameController {
	public Main main = Main.getSource();
	@FXML
	private TextField nameEntry;
	@FXML
	private Button playGameButton;
	@FXML
	private Button exitButton;

	public void showHome() {
		main.changeScene("Home.fxml");
	}

	public void showGame() {
		if (nameEntry.getText().isBlank()) {
			PigGame.setActiveGame("User");
		} else {
			PigGame.setActiveGame(nameEntry.getText());
		}
		main.changeScene("Game.fxml");
	}

	public String getName() {
		if (nameEntry.getText().isBlank()) {
			return "User";
		} else {
			return nameEntry.getText();
		}
	}

}
