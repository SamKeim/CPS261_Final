package controllers;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import application.Main;
import application.PigGame;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class GameController implements Initializable {

	@FXML
	private Label turnDisplay;
	@FXML
	private Label diceDisplay;
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
//		get and set local variables
		game = PigGame.getActiveGame();
		playerName = PigGame.getActiveGame().getPlayerName();
		isBank = false;

//		set text fields
		playerNameDisplay.setText(playerName + "'s Score");
		turnDisplay.setText(playerName + "'s Turn:");

//		set isPlayerTurn and call togglePlayerControlls() to setup first turn, player moves first
		isPlayerTurn = true;
		togglePlayerControls();
	}

	public void updateDisplay() {
		// in all situations except when the player rolls a 2-6,
		// wait a second to let player read output
		System.out.println("Die : " + die);
		if (!(isPlayerTurn && (die >= 2 && die <= 6))) {
			try {
				Thread.sleep(1500);
			} catch (InterruptedException ie) {}
		}

		// update labels on screen
		Platform.runLater(() ->	{
			if (isBank && !isPlayerTurn) { // update computer bank
				waddlesScore.setText(String.valueOf(game.getComputerScore()));
			}
			if (isBank && isPlayerTurn) { // update player bank
				playerScore.setText(String.valueOf(game.getPlayerScore()));
			}
			// update displays to show who's turn it is, the current die roll,
			// and the current bank
			turnDisplay.setText((isPlayerTurn ? playerName : "Waddles") + "'s Turn:");
			diceDisplay.setText((die == 0 ? "" : String.valueOf(die)));
			bankDisplay.setText("Bank: " + bank);
			isBank = false; // default
		});
	}

	public void rollDice() {
		die = game.roll();
		bank = (die == 1 ? 0 : bank + die); // if 1, bank is 0, otherwise add die to bank
		updateDisplay();
		
		if (die == 1) { // if player rolls a 1, take computer turn
			takeComputerTurnThread = new Thread(this::takeComputerTurn);
			takeComputerTurnThread.start();
		}

		// if this roll sets the player at over 100pts, player automatically wins
		// update bank, update screen, check that the game is complete
		// and exit via endGame() method
		if (bank + game.getPlayerScore() >= 100) {
			game.bank(bank);
			isBank = true;
			updateDisplay();
			if (game.isComplete()) {
				endGame();
			}
		}
	}

	public void bankPoints() {
		game.bank(bank);
		// update display and score
		isBank = true;
		updateDisplay();
		takeComputerTurnThread = new Thread(this::takeComputerTurn);
		takeComputerTurnThread.start();
	}

	public void takeComputerTurn() {
		// reset base values and update player controls
		bank = 0;
		die = 0;
		updateDisplay();
		isPlayerTurn = false;
		togglePlayerControls();

//		GAME LOGIC: Hold at 20, source:
//		https://cupola.gettysburg.edu/cgi/viewcontent.cgi?article=1003&context=csfac
//		EDIT: It's a little too hard to beat the computer at 20, so I'm dropping it to 18
		do {
			die = game.roll();
			bank = (die == 1 ? 0 : bank + die); // if die is 1, set bank to 0, otherwise add die to bank
			updateDisplay();
			
			// If bank is 18 or greater, or if this roll sets the computer over 100, bank
			// otherwise keep rolling
			if (bank >= 18 || bank + game.getComputerScore() >= 100) {
				game.setComputerScore(game.getComputerScore() + bank);
				bank = 0; // empty bank
				if (game.isComplete()) {
					endGame();
				}
				isBank = true;
				updateDisplay();
				break; // exits do-while
			}
		} while (die != 1);

		if (die == 1) { // gives the user a chance to see that Waddles rolled a 1
			updateDisplay();
			die = 0;
			bank = 0;
		}

		// set playerturn and toggle controls to end turn
		isPlayerTurn = true;
		togglePlayerControls();
		updateDisplay();

	}

	public void togglePlayerControls() {
//		set vbox's visibility based on who's turn it is
		playerControlls.setVisible(isPlayerTurn);
		waddlesImg.setVisible(!isPlayerTurn);
	}

	public void endGame() {
		main.changeScene("EndGame.fxml");
	}
}
