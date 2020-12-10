package controllers;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import application.Main;
import application.PigGame;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class HistoryController {
	public Main main = Main.getSource();
	private final static String FILENAME = "PigGameHistory";
	private static ObjectInputStream ois = null;
	private static ObjectOutputStream oos = null;
	private static ObservableList<PigGame> history = null;
	@FXML
	TableColumn<PigGame, LocalDateTime> dateColumn;
	@FXML
	TableColumn<PigGame, String> playerNameColumn;
	@FXML
	TableColumn<PigGame, Integer> playerScoreColumn;
	@FXML
	TableColumn<PigGame, Integer> waddlesScoreColumn;
	@FXML
	TableColumn<PigGame, String> winnerColumn;
	@FXML
	TableView<PigGame> table = new TableView<PigGame>();

	// Sends history list to controllers/HighScoresController.java
	// to calculate number of wins per player.
	public static ObservableList<PigGame> getHistory() {
		return history;
	}

	// Startup method
	@FXML
	protected void initialize() {
		table.setEditable(false);

		// Create columns
		dateColumn = new TableColumn<PigGame, LocalDateTime>("Date");
		playerNameColumn = new TableColumn<PigGame, String>("Player's Name");
		playerScoreColumn = new TableColumn<PigGame, Integer>("P. Score");
		waddlesScoreColumn = new TableColumn<PigGame, Integer>("W. Score");
		winnerColumn = new TableColumn<PigGame, String>("Winner");

		// Set width
		playerNameColumn.setMinWidth(100);
		playerScoreColumn.setMinWidth(40);
		waddlesScoreColumn.setMinWidth(40);
		winnerColumn.setMinWidth(100);

		table.setItems(history);
		
		// Load data and format date
		dateColumn.setCellValueFactory(new PropertyValueFactory<PigGame, LocalDateTime>("date"));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy h:mm a");
		dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
		dateColumn.setCellFactory(col -> new TableCell<PigGame, LocalDateTime>() {
		    @Override
		    protected void updateItem(LocalDateTime item, boolean empty) {
		        super.updateItem(item, empty);
		        if (empty)
		            setText(null);
		        else
		            setText(String.format(item.format(formatter)));
		    }
		});
		
		// Load remaining columns
		playerNameColumn.setCellValueFactory(new PropertyValueFactory<PigGame, String>("playerName"));
		playerScoreColumn.setCellValueFactory(new PropertyValueFactory<PigGame, Integer>("playerScore"));
		waddlesScoreColumn.setCellValueFactory(new PropertyValueFactory<PigGame, Integer>("computerScore"));
		winnerColumn.setCellValueFactory(new PropertyValueFactory<PigGame, String>("winner"));
		
		// Add columns to tableview
		table.getColumns().add(dateColumn);
		table.getColumns().add(playerNameColumn);
		table.getColumns().add(playerScoreColumn);
		table.getColumns().add(waddlesScoreColumn);
		table.getColumns().add(winnerColumn);
	}

	// Navigation
	public void showMain() {
		main.changeScene("Home.fxml");
	}

	public void showHighScores() {
		main.changeScene("HighScore.fxml");
	}

	// Reads from file on startup. Only called once from application/Main.java start()
	public static void loadRecords() {
		try {
			history = FXCollections.observableArrayList();
			
			ois = new ObjectInputStream(new FileInputStream(FILENAME));
			while (true) {
				PigGame pg = (PigGame) ois.readObject();
				history.add(pg);
			}
		} catch (EOFException eofe) {			
		} catch (IOException ioe) {
			System.out.println(ioe);
		} catch (ClassNotFoundException cnfe) {
			System.out.println(cnfe);
		} finally {
			try {
				if (ois != null)
					ois.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	// Adds recently-played game to table upon completion.
	public static void addGame(PigGame game) {
		history.add(game);
	}

	// Write to file on exit
	public static void writeRecords() {
		if (ois != null) {
			if (oos == null) {
				try {
					oos = new ObjectOutputStream(new FileOutputStream(FILENAME));
					for (int i = 0; i < history.size(); i++) {
						oos.writeObject(history.get(i));
					}
				} catch (FileNotFoundException fnfe) {
					System.out.println(fnfe);
				} catch (IOException ioe) {
					System.out.println(ioe);
				}

			}
		} else {
			System.out.println("No file to close");
		}
	}

	// Make fakes for testing
	public static void makeFakes() {
		history = FXCollections.observableArrayList();
		String[] fakeNames = { "Novella", "Novella", "Novella", "Tamekia", "Tamekia", "Tamekia", "Brian", "Brian",
				"Brian", "Brian" };
		for (int i = 0; i < 10; i++) {
			if(i % 2 == 0) {
				history.add(new PigGame(fakeNames[i], 75 + (int)((Math.random() * 15) + 5), 99 + (int)((Math.random() * 6) + 1)));
			} else {
				history.add(new PigGame(fakeNames[i], 99 + (int)((Math.random() * 6) + 1), 75 + (int)((Math.random() * 15) + 5)));				
			}
		}
	}

}
