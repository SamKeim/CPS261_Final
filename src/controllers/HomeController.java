package controllers;

import application.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

// controller for home page with navigation to history, how to play, and start game (via GetName screen)
public class HomeController {
	@FXML
	private Button playGame;
	@FXML
	private Button leaderboard;
	@FXML
	private Button howToPlay;
	public Main main = Main.getSource();

	
	public void showLeaderboard() {
		main.changeScene("History.fxml");		
	}
	
	public void showGetName() {
		main.changeScene("GetName.fxml");
	}
	
	public void showHowToPlay() {
		main.changeScene("HowToPlay.fxml");
	}
}
