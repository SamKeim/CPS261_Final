package controllers;

import application.Main;

// quick controller to navigate back to main
public class HowToPlayController {
	public Main main = Main.getSource();

	public void showMain() {
		main.changeScene("Home.fxml");
	}
}
