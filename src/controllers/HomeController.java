package controllers;

import application.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

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
