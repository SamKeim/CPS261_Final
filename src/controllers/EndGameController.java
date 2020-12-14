package controllers;

import application.Main;
import application.PigGame;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

// Displays winner with one button to navigate to home
public class EndGameController {
	public Main main = Main.getSource();
	@FXML
	private Label winnerLabel;
	@FXML
	private Label endText;
	
	@FXML
	protected void initialize() {
		winnerLabel.setText(PigGame.getActiveGame().getWinner());
		if(PigGame.getActiveGame().isWinner()) {
			endText.setText("\"A simple congratulations from a simple pig.\"");
		} else {
			endText.setText("\"I'm losing to a pig!!\"");
		}
	}
	
	public void showHome() {
		main.changeScene("Home.fxml");
	}
}
