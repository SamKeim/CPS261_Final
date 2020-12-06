package controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import application.HighScore;
import application.Main;
import application.PigGame;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;



public class HighScoresController {

	@FXML
	private TableView<HighScore> highScoreTable;
	@FXML
	private TableColumn<HighScore, LocalDateTime> dateColumn;
	@FXML
	private TableColumn<HighScore, String> playerNameColumn;
	@FXML
	private TableColumn<HighScore, Integer> numWinsColumn;

	public Main main = Main.getSource();

	public void showLeaderboard() {
		main.changeScene("History.fxml");
	}

	public void showHome() {
		main.changeScene("Home.fxml");
	}

	public ObservableList<HighScore> getHighScores() {

		// this whole method needs to be error checked. it compiles but does it work??
		List<PigGame> history = HistoryController.getHistory().stream().collect(Collectors.toList());
		ObservableList<HighScore> highScores = FXCollections.observableArrayList();
		// get list of unique names
		List<String> listOfNames = history.stream().filter(e -> e.isWinner()).map(e -> e.getPlayerName()).distinct()
				.collect(Collectors.toList());
		for (String name : listOfNames) {
			// for each name, get max date
			LocalDateTime date = history.stream().filter(e -> e.getPlayerName().equals(name)).map(e -> e.getDate())
					.max(LocalDateTime::compareTo).get();

			// for each name, get num wins
			Integer numWins = (int) history.stream().filter(e -> e.isWinner()).filter(e -> e.getPlayerName().equals(name)).count();

			// add to list
			highScores.add(new HighScore(date, name, numWins));
		}
		highScores.sort((HighScore hs1, HighScore hs2) -> hs2.getNumWins() - hs1.getNumWins());
		return highScores;
	}

	@FXML
	protected void initialize() {
		highScoreTable.setEditable(false);
		ObservableList<HighScore> hs = getHighScores();

		// create columns
		dateColumn = new TableColumn<HighScore, LocalDateTime>("Date");
		playerNameColumn = new TableColumn<HighScore, String>("Player's Name");
		numWinsColumn = new TableColumn<HighScore, Integer>("# Wins");

		// set width
		playerNameColumn.setMinWidth(130);
		numWinsColumn.setMinWidth(80);

		// load data
		dateColumn.setCellValueFactory(new PropertyValueFactory<HighScore, LocalDateTime>("date"));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy h:mm a");
		dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
		dateColumn.setCellFactory(col -> new TableCell<HighScore, LocalDateTime>() {
		    @Override
		    protected void updateItem(LocalDateTime item, boolean empty) {
		    	
		        super.updateItem(item, empty);
		        if (empty)
		            setText(null);
		        else
		            setText(String.format(item.format(formatter)));
		    }
		});

		playerNameColumn.setCellValueFactory(new PropertyValueFactory<HighScore, String>("playerName"));
		numWinsColumn.setCellValueFactory(new PropertyValueFactory<HighScore, Integer>("numWins"));

		// add to tableview
		highScoreTable.getColumns().add(dateColumn);
		highScoreTable.getColumns().add(playerNameColumn);
		highScoreTable.getColumns().add(numWinsColumn);
		highScoreTable.setItems(hs);
	}

}