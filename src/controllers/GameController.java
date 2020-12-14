package controllers;

import application.Main;
import application.PigGame;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class GameController {

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
	private Button rollButton;
	@FXML
	private Button bankButton;
	@FXML
	private HBox playerControlls;
	@FXML
	private ImageView diceImg;
	@FXML
	private ImageView waddlesImg;

	private Main main = Main.getSource();
	private PigGame game;
	private Thread takeComputerTurnThread;
	private Thread endGameThread;
	private String playerName;
	private int die;
	private int bank;
	private boolean isPlayerTurn;
	private boolean isBank;

	@FXML
	public void initialize() {
//		Get and set local variables
		game = PigGame.getActiveGame();
		playerName = PigGame.getActiveGame().getPlayerName();
		endGameThread = new Thread(this::endGame);
		// Originally isBank was being passed into the update() method as a hard-coded boolean, but
		// without a name on the boolean it was getting difficult to follow the logic of
		// when and why it was being passed in
		isBank = false; // Default

//		Set text fields
		playerNameDisplay.setText(playerName + "'s Score");
		turnDisplay.setText(playerName + "'s Turn:");

//		Set isPlayerTurn and call togglePlayerControlls() to setup first turn, player moves first
		isPlayerTurn = true;
		togglePlayerControls();
		bankButton.setDisable(bank == 0);

//		Set event listener to allow user to click die to roll
		diceImg.setOnMouseClicked((e) -> {
			if (isPlayerTurn)
				rollDice();
		});

		// Update for difficulty
		playerScore.setText(String.valueOf(game.getPlayerScore()));
		waddlesScore.setText(String.valueOf(game.getComputerScore()));
	}

	public void rollAnimation() {
		die = game.roll();
		// Wrapper transition
		SequentialTransition dieAnimation = new SequentialTransition();
		PauseTransition pause = new PauseTransition(Duration.millis(game.getSpeed().getValue()));

		// Roll die mechanics
		FadeTransition switchImage = new FadeTransition(Duration.millis(0), diceImg);
		switchImage
				.setOnFinished(e -> diceImg.setImage(new Image("img/die/" + (int) ((Math.random() * 6) + 1) + ".png")));
		SequentialTransition rollAnimation = new SequentialTransition(switchImage, pause);
		rollAnimation.setCycleCount(5);

		// Set transition to die face that it landed on
		FadeTransition endImage = new FadeTransition(Duration.millis(0), diceImg);
		endImage.setOnFinished(e -> diceImg.setImage(new Image("img/die/" + die + ".png")));
		SequentialTransition endAnimation = new SequentialTransition(endImage, pause);
		endAnimation.setCycleCount(1);

		dieAnimation.getChildren().addAll(rollAnimation, endAnimation);
		dieAnimation.play();
	}

	public void updateDisplay() {
		// Pause if it's not the player's turn, let them see what Waddles rolled
		if (!isPlayerTurn) {
			pause();
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
	}

	public void rollDice() {
		rollAnimation();
		bank = (die == 1 ? 0 : bank + die);
		updateDisplay();

		if (die == 1) { // If player rolls a 1, take computer turn
			updateDisplay();
			takeComputerTurnThread = new Thread(this::takeComputerTurn);
			takeComputerTurnThread.start();
		}

		// If this roll sets the player at over 100pts, player automatically wins.
		// Update bank, update screen, check that the game is complete
		// and exit via endGame() method.
		if (bank + game.getPlayerScore() >= 100) {
			bankPoints();
			if (game.isComplete()) {
				endGameThread.start();
			}
		}
	}

	// Bank user's points, update, and end turn
	public void bankPoints() {
		game.bank(bank);
		bank = 0;
		// Update display and score
		isBank = true;
		updateDisplay();
		if (!game.isComplete()) {
			takeComputerTurnThread = new Thread(this::takeComputerTurn);
			takeComputerTurnThread.start();
		}
	}

	public void pause() {
		// Professor Bai, I don't know why this works. It will animate and roll the player's 1 if
		// it runs Thread.sleep() from a launched thread.
		// So I moved the wait time to this pause() method, and it gets called at the
		// start of the pig's turn (which is run from a thread)
		// and it shows the player's 1 animation.
		// I have more to learn about threading.
		try {
			Thread.sleep(game.getSpeed().getValue() * 12);
		} catch (InterruptedException ie) {
		}
	}

	public void takeComputerTurn() {
		// Pause to let player see that their turn is over
		pause();

		// Reset base values and update player controls
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

			// If bank is 18 or greater, or if this roll sets the
			// computer over 100, bank points. Otherwise keep rolling.
			if (bank >= 18 || bank + game.getComputerScore() >= 100) {
				game.setComputerScore(game.getComputerScore() + bank);
				bank = 0; // Empty bank
				isBank = true;
				updateDisplay();
				if (game.isComplete()) {
					endGameThread.start();
				}
				break; // Exits do-while
			}
		} while (die != 1);

		if (die == 1) { // Gives the user a chance to see that Waddles rolled a 1
			bank = 0;
			updateDisplay();
		}

		// Set playerturn and toggle controls to end turn
		isPlayerTurn = true;
		togglePlayerControls();
		updateDisplay();
	}

	public void togglePlayerControls() {
//		Set VBox's visibility based on who's turn it is
		playerControlls.setVisible(isPlayerTurn);
		waddlesImg.setVisible(!isPlayerTurn);
	}

	public void endGame() {
		// pause to let player see end scores
		pause();
		game.endGame();
		updateDisplay();
		Platform.runLater(() -> main.changeScene("EndGame.fxml"));
	}
}
