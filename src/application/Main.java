package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import controllers.HistoryController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
	private static Main singleton = null;
	private static Stage localPrimaryStage = null;

	// This isn't a proper singleton, since there's no private constructor, but it's
	// a relative singleton.
	// "I had a cousin named Singleton, he was an only child and his parents decided
	// to get creative with his name."
	public static Main getSource() {
		if (singleton == null)
			singleton = new Main();
		return singleton;
	}

	@Override
	public void start(Stage primaryStage) {
		// save stage to make for easy switching of scenes later
		localPrimaryStage = primaryStage;

		// load fxml
		StackPane root = null;
		try {
			root = (StackPane) FXMLLoader.load(getClass().getResource("../fxmls/Home.fxml"));
		} catch (Exception e) {
			root = new StackPane();
			root.getChildren().add(new Label("Unable to load FXML. Please try again."));
		}

		Scene scene = new Scene(root, 550, 550);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		// Load in history records and setup procedure for shut down
		HistoryController.loadRecords();
		primaryStage.setOnCloseRequest(event -> {
			HistoryController.writeRecords();
			System.exit(0);
		});

		primaryStage.setResizable(false);
		primaryStage.setTitle("The Game of Pig");
		primaryStage.getIcons().add(new Image("file:src/img/icon.jpg"));
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	public void changeScene(String filepath) {
		// Changes FXML file / scene in primary stage
		// resource https://www.youtube.com/watch?v=XCgcQTQCfJQ
		try {
			URL url = new File("src/fxmls/" + filepath).toURI().toURL();
			Parent parent = FXMLLoader.load(url);
			Scene scene = new Scene(parent);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			localPrimaryStage.setScene(scene);
			localPrimaryStage.show();
		} catch (IOException ioe) {
			// Shows error message on screen and sets up button to navigate home
			VBox root = new VBox();
			Button button = new Button("Back to Home.");
			button.setOnAction((e) -> changeScene("home.fxml"));
			root.setAlignment(Pos.CENTER);
			root.getChildren().add(new Label("Unable to load FXML. Please try again."));
			root.getChildren().add(button);
			localPrimaryStage.setScene(new Scene(root, 550, 550));
			localPrimaryStage.show();
		}
	}
}