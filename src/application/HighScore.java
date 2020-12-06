package application;

import java.time.LocalDateTime;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;


// a quick pojo to track the accumulated number of wins of the players
// does not write to file, this recalculates every time the high-score board is viewed
public class HighScore {
	private String playerName;
	private Integer numWins;
	private ObjectProperty<LocalDateTime> date = new SimpleObjectProperty<LocalDateTime>();

	public HighScore(LocalDateTime date, String playerName, Integer numWins) {
		this.date.set(date);
		this.playerName = playerName;
		this.numWins = numWins;
	}

	public String getPlayerName() {
		return playerName;
	}

	public Integer getNumWins() {
		return numWins;
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

	@Override
	public String toString() {
		return String.format("HighScore [%s, %s, %d]", date, playerName, numWins);
	}
}