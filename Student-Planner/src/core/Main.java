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

	public static String dbNamePrefix = "";
	private static String dbExtension = ".db";
	protected static String versionExtension = ".v";
	protected static String currentVersion = "01";
	private static String dbVersion = currentVersion;

	// This is the user-defined 'name' of the db, but the full name of the db
	// will include its version and extension.

	private static String title = "Student Planner v" + currentVersion;

	public static void main(String[] args) throws SqliteWrapperException,
			InitializationException, IOException {

		clock = TaskScheduler.getInstance(LocalDateTime.now());
		logger = new Logger(Logger.DEFAULT_PATH);
		config = new Config(Config.DEFAULT_PATH);

		if (!config.getDbName().isEmpty()) {

			dbNamePrefix = extractPrefix(config.getDbName());

			String dbPath = formDbPath();

			if (IOManager.fileExists(dbPath)) {

				// Db migration logic.
				dbVersion = extractVersion(config.getDbName());

				if (!dbVersion.equals(currentVersion)) {

					throw new InitializationException(
							"Database has incompatible version.");
				}

				// Load existing db
				active = new Profile(dbNamePrefix,
						new SqliteWrapper(config.getDbDirectory()));

				active.db.connectToDb(config.getDbName());

			} else {

				throw new InitializationException(
						"Database not found in path: " + dbPath);
			}
		}

		launch(args);
	}

	private static String extractPrefix(String dbName) {

		String[] components = dbName.split("\\.");

		return components[0];
	}

	@Override
	public void start(Stage window) throws Exception {

		Main.window = window;

		Parent root = null;
		Scene startup = null;

		if (config.getDbName().isEmpty()) {

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
	}

	private static String formDbPath() {

		return config.getDbDirectory() + "/" + dbNamePrefix + versionExtension
				+ dbVersion + dbExtension;
	}

	public static String formDbName() {

		return dbNamePrefix + versionExtension + dbVersion + dbExtension;
	}

	private static String extractVersion(String dbName) {

		System.out.println(dbName);

		String[] components = dbName.split("\\.");

		for (String s : components) {

			System.out.println(s);
		}

		return components[1].substring(1, components[1].length());
	}

	public static void loadProfile(File chosen)
			throws InitializationException, SqliteWrapperException {

		String filename = chosen.getName();

		String[] filenameComponents = filename.split("\\.");

		System.out.println(filename);

		for (String s : filenameComponents) {

			System.out.println(s);
		}

		if (filenameComponents.length != 3) {

			throw new InitializationException("Invalid file format.");

		} else {

			dbNamePrefix = filenameComponents[0];

			if (filenameComponents[1].charAt(0) != 'v'
					|| filenameComponents[1].length() != 3) {

				throw new InitializationException("Invalid file format.");

			} else {

				dbVersion = filenameComponents[1].substring(1);

				if (!isValidVersion(dbVersion)) {

					throw new InitializationException("Invalid db version.");
				}
			}

			if (!filenameComponents[2].equals(dbExtension.substring(1))) {

				throw new InitializationException("Invalid db extension.");

			}

			config.setDbName(formDbName());
			config.setDbDirectory(chosen.getParent());

			active = new Profile(dbNamePrefix,
					new SqliteWrapper(config.getDbDirectory()));

			active.db.connectToDb(config.getDbName());
		}
	}

	public static boolean initializeDb(String name)
			throws SqliteWrapperException {

		boolean isInitialized = false;
		dbNamePrefix = name;
		config.setDbName(formDbName());

		active = new Profile(dbNamePrefix,
				new SqliteWrapper(config.getDbDirectory()));

		try {

			active.db.createDb(config.getDbName());
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

	private static boolean isValidVersion(String dbVersion) {

		if (dbVersion.equals("01")) {

			return true;

		} else {

			return false;
		}
	}
}
