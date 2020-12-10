package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.locks.ReentrantLock;

import application.Main;
import application.PigGame;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class GameController implements Initializable {

	@FXML
	private Label turnDisplay;
	@FXML
	private Label bankDisplay;
	@FXML
	private Label playerNameDisplay;
	@FXML
	private Label playerScore;
	@FXML
	private Label waddlesScore;
	@FXML
	private ImageView waddlesImg;
	@FXML
	private Button rollButton;
	@FXML
	private Button bankButton;
	@FXML
	private HBox playerControlls;
	@FXML
	private ImageView diceImg;

	private Thread takeComputerTurnThread;
	private int die;
	private int bank;
	private boolean isPlayerTurn;
	private Main main = Main.getSource();
	private PigGame game;
	private String playerName;
	private boolean isBank;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
//		Get and set local variables
		game = PigGame.getActiveGame();
		playerName = PigGame.getActiveGame().getPlayerName();
		isBank = false;

//		Set text fields
		playerNameDisplay.setText(playerName + "'s Score");
		turnDisplay.setText(playerName + "'s Turn:");

//		Set isPlayerTurn and call togglePlayerControlls() to setup first turn, player moves first
		isPlayerTurn = true;
		togglePlayerControls();
		bankButton.setDisable(bank == 0);
		
		
		///////////TESTING
		game.setPlayerScore(90);
		game.setComputerScore(90);
	}

	public void rollAnimation() {
		System.out.println("==========\nrollAnimation() start : " + Thread.currentThread().toString());
		
		die = game.roll();
//		die = 1;
		System.out.println("rollAnimation() die : " + die);

		
		// Wrapper transition
		SequentialTransition dieAnimation = new SequentialTransition();
		PauseTransition pause = new PauseTransition(Duration.millis(100));

		// Roll die mechanics
		FadeTransition switchImage = new FadeTransition(Duration.millis(0), diceImg);
		switchImage.setOnFinished(e -> 
			diceImg.setImage(new Image("img/die/" + (int)((Math.random() * 6) + 1) + ".png")));
		SequentialTransition rollAnimation = new SequentialTransition(switchImage, pause);
		rollAnimation.setCycleCount(7);

		// Set transition to die face that it landed on
		FadeTransition endImage = new FadeTransition(Duration.millis(0), diceImg);
		endImage.setOnFinished(e -> diceImg.setImage(new Image("img/die/" + die + ".png")));
		SequentialTransition endAnimation = new SequentialTransition(endImage, pause);
		endAnimation.setCycleCount(1);
		
		dieAnimation.getChildren().addAll(rollAnimation, endAnimation);
		dieAnimation.play();		

//		System.out.println("rollDice if(die == 1){} : " + (die == 1));
//		// FOR WHATEVER REASON this logic has to exist here. If it exists in the player's rollDie() method
//		// it will evaluate this code block /before/ it actually rolls a die resulting in a false negative
//		if (isPlayerTurn && die == 1) { // If player rolls a 1, take computer turn
//			bank = (die == 1 ? 0 : bank + die); // If 1, bank is emptied, otherwise add die to bank
//			updateDisplay();
//			takeComputerTurnThread = new Thread(this::takeComputerTurn);
//			takeComputerTurnThread.start();
//		} else {
//			System.out.println("ELSE! Die : " + die);
//		}
		System.out.println("rollAnimation() end\n==========\n");
		
	}

	public void updateDisplay() {
		System.out.println("updateDisplay() start");
		// In all situations except when the player rolls a 2-6,
		// wait a second to let player read output
		if (isBank || die == 1 || !isPlayerTurn) {
			try {
				Thread.sleep(1700);
			} catch (InterruptedException ie) {}
		}
		
		// Update labels on screen
		Platform.runLater(() -> {
			if (isBank && !isPlayerTurn) { // update computer bank
				waddlesScore.setText(String.valueOf(game.getComputerScore()));
			}
			if (isBank && isPlayerTurn) { // update player bank
				playerScore.setText(String.valueOf(game.getPlayerScore()));
			}
			// Update displays to show who's turn it is, the current die roll,
			// and the current bank
			bankButton.setDisable(bank == 0);
			turnDisplay.setText((isPlayerTurn ? playerName : "Waddles") + "'s Turn:");
			bankDisplay.setText("Bank: " + bank);
			isBank = false; // default
		});
		System.out.println("updateDisplay() end");
	}

	public void rollDice() {
		System.out.println("==========\nrollDice() start: " + Thread.currentThread().toString());

//		Thread tester = new Thread(this::rollAnimation);
//		tester.start();

		rollAnimation();
		bank = (die == 1 ? 0 : bank + die);
		updateDisplay();
		
		// MOVED TO rollAnimation()
		System.out.println("rollDice if(die == 1){} : " + (die == 1));
		if (die == 1) { // If player rolls a 1, take computer turn
			updateDisplay();
			takeComputerTurnThread = new Thread(this::takeComputerTurn);
			takeComputerTurnThread.start();
		} else {
			System.out.println("ELSE! Die : " + die);
		}

		// If this roll sets the player at over 100pts, player automatically wins.
		// Update bank, update screen, check that the game is complete
		// and exit via endGame() method.
		if (bank + game.getPlayerScore() >= 100) {
			bankPoints();
			if (game.isComplete()) {
				endGame();
			}
		}
		System.out.println("rollDice() end\n==========\n");
	}

	public void bankPoints() {
		game.bank(bank);
		// Update display and score
		isBank = true;
		updateDisplay();
		if(!game.isComplete()) {
			takeComputerTurnThread = new Thread(this::takeComputerTurn);
			takeComputerTurnThread.start();
		}
	}

	public void takeComputerTurn() {
		System.out.println("==========\ntakeComputerTurn() start");
		// Reset base values and update player controls
		bank = 0;
		updateDisplay();
		isPlayerTurn = false;
		togglePlayerControls();

//		GAME LOGIC: Hold at 20, source:
//		https://cupola.gettysburg.edu/cgi/viewcontent.cgi?article=1003&context=csfac
//		EDIT: It's a little too hard to beat the computer at 20, so I'm dropping it to 18
		do {
			rollAnimation();
			bank = (die == 1 ? 0 : bank + die); // If die is 1, empty the bank, otherwise add die to bank
			updateDisplay();

			// If bank is 18 or greater, or if this roll sets the computer over 100, bank
			// points.
			// Otherwise keep rolling.
			if (bank >= 18 || bank + game.getComputerScore() >= 100) {
				game.setComputerScore(game.getComputerScore() + bank);
				bank = 0; // Empty bank
				isBank = true;
				updateDisplay();
				if (game.isComplete()) {
					endGame();
				}
				break; // Exits do-while
			}
		} while (die != 1);

		if (die == 1) { // Gives the user a chance to see that Waddles rolled a 1
			updateDisplay();
			bank = 0;
		}

		// Set playerturn and toggle controls to end turn
		isPlayerTurn = true;
		togglePlayerControls();
		updateDisplay();
		System.out.println("takeComputerTurn() end\n==========\n");

	}

	public void togglePlayerControls() {
//		Set VBox's visibility based on who's turn it is
		playerControlls.setVisible(isPlayerTurn);
		waddlesImg.setVisible(!isPlayerTurn);
	}

	public void endGame() {
		updateDisplay();
		Platform.runLater(() -> main.changeScene("EndGame.fxml"));
	}
}
