package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import controllers.HistoryController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
	private static Main singleton = null;
	private static Stage localPrimaryStage = null;

	public static Main getSource() {
		if (singleton == null)
			singleton = new Main();
		return singleton;
	}

	@Override
	public void start(Stage primaryStage) {
		// save stage to make for easy switching of scenes later
		localPrimaryStage = primaryStage;
		
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
		primaryStage.getIcons().add(new Image("file:icon.jpg"));
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	public void changeScene(String filepath) {
		try {
			URL url = new File("src/fxmls/" + filepath).toURI().toURL();
			Parent parent = FXMLLoader.load(url);
			Scene scene = new Scene(parent);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			localPrimaryStage.setScene(scene);
			localPrimaryStage.show();
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
	}
}