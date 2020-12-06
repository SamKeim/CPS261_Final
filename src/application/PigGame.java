package application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;

import controllers.HistoryController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class PigGame implements Serializable {
	// generated serialVersionUID
	private static final long serialVersionUID = -6217557016052696340L;
	private transient String playerName;
	private transient int playerScore;
	private transient int computerScore;
	private transient ObjectProperty<LocalDateTime> date = new SimpleObjectProperty<LocalDateTime>();
	private transient boolean isWinner;
	private transient boolean isComplete;
	private transient static PigGame activeGame = null;

	public static void setActiveGame(String playerName) {
		activeGame = new PigGame(playerName);
	}

	public static PigGame getActiveGame() {
		return activeGame;
	}

	// to generate fakes for testing
	public PigGame(String playerName, int score, int opScore) {
		this.playerName = playerName;
		this.date.set(LocalDateTime.of(2020, 11, (int) ((Math.random() * 28) + 1), (int) ((Math.random() * 13) + 9),
				(int) ((Math.random() * 59) + 1), (int) ((Math.random() * 59) + 1)));

		this.playerScore = score;
		this.computerScore = opScore;
		isComplete = true;
		setWinner();
	}

	public PigGame(String playerName) {
		this.playerName = playerName;
		date.set(LocalDateTime.now());
		playerScore = 0;
		computerScore = 0;
		isComplete = false;
	}

	public void bank(int score) {
		playerScore += score;
	}

	public int roll() {
		return (int) (Math.random() * 6) + 1;
	}

	public String getWinner() {
		return (isWinner ? playerName : "Waddles");
	}

	public boolean isComplete() {
		if (playerScore >= 100 || computerScore >= 100) {
			setWinner();
			HistoryController.addGame(activeGame);
			isComplete = true;
		}
		return isComplete;
	}

	public boolean isWinner() {
		return isWinner;
	}

	public void setWinner() {
		isWinner = playerScore >= computerScore;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public int getPlayerScore() {
		return playerScore;
	}

	public void setPlayerScore(int playerScore) {
		this.playerScore = playerScore;
	}

	public int getComputerScore() {
		return computerScore;
	}

	public void setComputerScore(int computerScore) {
		this.computerScore = computerScore;
	}

	public ObjectProperty<LocalDateTime> dateProperty() {
		return date;
	}

	public LocalDateTime getDate() {
		return date.get();
	}

	public void setDate(LocalDateTime value) {
		date.set(value);
	}

	// I wanted to be able to format the date property in the high scores tables
	// so I needed to use ObjectProperty for LocalDateTime
	// which is not serializable, so I had to override the writeObject and
	// readObject methods below
	private void writeObject(ObjectOutputStream s) throws IOException {
		s.defaultWriteObject();
		s.writeObject(playerName);
		s.writeInt(playerScore);
		s.writeInt(computerScore);
		s.writeObject(((ObjectProperty<LocalDateTime>) date).get());
		s.writeBoolean(isWinner);
	}

	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
		playerName = (String) s.readObject();
		playerScore = s.readInt();
		computerScore = s.readInt();
		date = new SimpleObjectProperty<LocalDateTime>();
		date.set((LocalDateTime) s.readObject());
		isWinner = s.readBoolean();
	}

	@Override
	public String toString() {
		return "PigGame [playerName=" + playerName + ", playerScore=" + playerScore + ", computerScore=" + computerScore
				+ ", date=" + date + ", isWinner=" + isWinner + "]";
	}
}
