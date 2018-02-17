package views;

import java.io.File;
import java.io.IOException;

import core.InitializationException;
import core.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import sqlite.SqliteWrapperException;

public class WelcomeController {

	@FXML
	private TextField nameField;

	@FXML
	public void initialize() {

	}

	@FXML
	public void createProfile() throws SqliteWrapperException, IOException {

		Main.initializeDb(nameField.getText());

		showTermsView();
	}

	private void showTermsView() throws IOException {

		Parent terms = FXMLLoader
				.load(this.getClass().getResource("/views/Terms.fxml"));
		Scene addTerms = new Scene(terms, 1280, 960);
		Main.window.setScene(addTerms);
	}

	@FXML
	public void loadProfile() throws IOException {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Load Profile");

		File chosen = fileChooser.showOpenDialog(Main.window);

		try {

			Main.loadProfile(chosen);

		} catch (InitializationException e) {

			e.printStackTrace();

			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Cannot load profile");
			alert.setContentText("Invalid file");
			alert.showAndWait();

		} catch (SqliteWrapperException e) {

			e.printStackTrace();
		}

		Parent main = FXMLLoader
				.load(this.getClass().getResource("/views/Main.fxml"));
		Scene mainScene = new Scene(main, 1280, 960);
		Main.window.setScene(mainScene);
	}
}
