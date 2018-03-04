package views;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import core.InitializationException;
import core.Main;
import core.Term;
import javafx.fxml.FXML;
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

		if (Main.initializeDb(nameField.getText())) {
			Main.viewController.showTermsView();
		}
	}

	@FXML
	public void loadProfile()
			throws IOException, SqliteWrapperException, SQLException {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Load Profile");
		boolean loadSuccessful = false;

		while (!loadSuccessful) {

			try {

				File chosen = fileChooser.showOpenDialog(Main.window);
				Main.loadProfile(chosen);
				loadSuccessful = true;

			} catch (InitializationException e) {

				e.printStackTrace();

				Main.showAlert(AlertType.ERROR, "Cannot load profile",
						"Invalid file");

			} catch (SqliteWrapperException e) {

				e.printStackTrace();
			}
		}

		if (Term.getNumTerms() > 0) {

			Main.viewController.showMainView();

		} else {

			Main.viewController.showTermsView();
		}
	}
}
