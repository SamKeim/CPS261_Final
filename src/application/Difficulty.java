package application;

// Difficulty is determined by giving either the computer or the user a head start in the game
public enum Difficulty{
	EASY(10, 0),
	MEDIUM(0, 0),
	HARD(0, 10);

	private int playerStartingScore;
	private int computerStartingScore;
	
	Difficulty(int playerStartingScore, int computerStartingScore) {
		this.playerStartingScore = playerStartingScore;
		this.computerStartingScore = computerStartingScore;

	}
	
	public int getPlayerStartingScore() {
		return playerStartingScore;
	}

	public int getComputerStartingScore() {
		return computerStartingScore;
	}
	
}