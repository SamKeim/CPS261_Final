package application;

// Sets number of milliseconds for the rollDice animation in controllers/GameController.java
// The die animates 5 times, pausing for x milliseconds between animations.
// This also helps determine the length of the pause when updating the screen.
public enum Speed{
	REGULAR(100),
	FAST(50);

	private int value;
	
	Speed(int i) {
		this.value = i;
	}
	
	public int getValue() {
		return value;
	}
}

