package controllers;

import application.Main;

public class HowToPlayController {
	public Main main = Main.getSource();

	public void showMain() {
		main.changeScene("Home.fxml");
	}
}
