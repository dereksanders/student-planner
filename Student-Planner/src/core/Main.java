package core;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import sqlite.SqliteWrapper;
import sqlite.SqliteWrapperException;
import utility.IOManager;
import utility.Log;
import utility.Logger;
import views.ViewController;

public class Main extends Application {

	public static Stage window;
	public static int prefWidth = 1024;
	public static int prefHeight = 768;

	public static TaskScheduler clock;
	public static Profile active;
	public static Logger logger;
	public static Config config;
	public static ViewController viewController;

	private static boolean dbNotFound = false;

	public static final int CURRENT_VERSION = 1;

	// This is the user-defined 'name' of the db, but the full name of the db
	// will include its version and extension.

	private static String title = "Student Planner v" + CURRENT_VERSION;

	public static void main(String[] args) throws SqliteWrapperException,
			InitializationException, IOException {

		clock = TaskScheduler.getInstance(LocalDateTime.now());
		logger = new Logger();
		config = new Config();
		viewController = new ViewController();

		if (config.getDbFilename() != null) {

			if (IOManager.fileExists(config.getDbPath())) {

				if (config.getDbFilename().getVersion() != CURRENT_VERSION) {

					migrateDb();
				}

				// Load existing db
				active = new Profile(config.getDbFilename().getNamePrefix(),
						new SqliteWrapper(config.getDbDirectory()));

				active.db.connectToDb(config.getDbFilename().toString());

			} else {

				dbNotFound = true;
			}
		}

		launch(args);
	}

	private static void migrateDb() throws InitializationException {

		if (config.getDbFilename().getVersion() != CURRENT_VERSION) {

			throw new InitializationException("Db has incompatible version.");
		}
	}

	@Override
	public void start(Stage window) throws Exception {

		Main.window = window;

		Parent root = null;
		Scene startup = null;

		if (config.getDbFilename() == null || dbNotFound) {

			root = FXMLLoader.load(Main.class.getClass()
					.getResource(ViewController.welcomeViewPath));

			startup = new Scene(root, prefWidth, prefHeight);

		} else {

			root = FXMLLoader.load(Main.class.getClass()
					.getResource(ViewController.mainViewPath));

			startup = new Scene(root, prefWidth, prefHeight);
		}

		window.setOnCloseRequest(event -> {
			clock.update.cancel();
		});

		window.setScene(startup);
		window.setTitle(title);
		window.getIcons()
				.add(new Image(Main.class.getResourceAsStream("/icon.png")));
		window.show();

		if (dbNotFound) {

			showAlert(AlertType.WARNING, "Cannot load db specified in config",
					"Database not found in path: " + config.getDbPath());
		}
	}

	public static void loadProfile(File chosen)
			throws InitializationException, SqliteWrapperException {

		DbFilename filename = new DbFilename(chosen.getName());
		config.setDbFilename(filename);
		config.setDbDirectory(chosen.getParent());

		active = new Profile(config.getDbFilename().getNamePrefix(),
				new SqliteWrapper(config.getDbDirectory()));

		active.db.connectToDb(config.getDbFilename().toString());
	}

	public static boolean initializeDb(String name)
			throws SqliteWrapperException {

		boolean isInitialized = false;

		config.setDbFilename(new DbFilename(name));

		active = new Profile(config.getDbFilename().getNamePrefix(),
				new SqliteWrapper(config.getDbDirectory()));

		try {

			active.db.createDb(config.getDbFilename().toString());
			isInitialized = true;

		} catch (SqliteWrapperException e) {

			showAlert(AlertType.ERROR, "Cannot create profile",
					"Profile with that name already exists");
		}

		if (isInitialized) {
			active.db.executeFromFile(config.getSchemaPath());
		}

		return isInitialized;
	}

	public static void showAlert(AlertType type, String issue, String reason) {

		logger.post(new Log("showAlert", issue + ":" + "\n" + reason,
				Log.Severity.INFO));

		Alert alert = new Alert(type);
		alert.setTitle(type.toString());
		alert.setHeaderText(issue);
		alert.setContentText(reason);
		alert.showAndWait();
	}
}
