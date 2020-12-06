package controllers;

import application.Main;
import application.PigGame;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

// simple controller to display winner with one button to navigate to home
public class EndGameController {
	public Main main = Main.getSource();
	@FXML
	private Label winnerLabel;
	
	@FXML
	protected void initialize() {
		winnerLabel.setText(PigGame.getActiveGame().getWinner());
	}
	public void showHome() {
		main.changeScene("Home.fxml");
	}
}
