package core;

import java.io.File;

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

public class Main extends Application {

	public static Stage window;

	public static SqliteWrapper sqlite;

	private static String logPath = "history.log";
	private static String configPath = "planner.cfg";
	private static String dbDirectory = "dbs/";

	private static String dbName = "";

	// This is the user-defined 'name' of the db, but the full name of the db
	// will include its version and extension.
	public static String dbNamePrefix = "";

	private static String dbExtension = ".db";
	private static String versionExtension = ".v";
	private static String currentVersion = "01";
	private static String dbVersion = currentVersion;

	private static String title = "Student Planner v" + currentVersion;

	// schemaPath is dependent upon dbDirectory, so always set schemaPath after
	// dbDirectory.
	public static String schemaPath = formSchemaPath();

	public static void main(String[] args)
			throws SqliteWrapperException, InitializationException {

		Logger.initialize(logPath);

		configure();

		if (!IOManager.fileExists(schemaPath)) {

			System.out.println("Schema file doesn't exist. Creating one..");

			writeSchema();
		}

		if (!IOManager.fileExists(dbDirectory)) {

			IOManager.createDirectory(dbDirectory);
		}

		sqlite = new SqliteWrapper(dbDirectory);

		if (!dbName.isEmpty()) {

			String dbPath = formDbPath();

			if (IOManager.fileExists(dbPath)) {

				// Db migration logic.
				dbVersion = extractVersion(dbName);

				if (!dbVersion.equals(currentVersion)) {

					throw new InitializationException(
							"Database has incompatible version.");
				}

				// Load existing db
				sqlite.connectToDb(dbName);

				// Set scene to main view

			} else {

				throw new InitializationException(
						"Database not found in path: " + dbPath);
			}

		} else {

			launch(args);
		}
	}

	@Override
	public void start(Stage window) throws Exception {

		Main.window = window;

		Parent root = FXMLLoader
				.load(Main.class.getClass().getResource("/views/Welcome.fxml"));

		Scene startup = new Scene(root, 1280, 960);

		startup.getStylesheets().add(Main.class.getClass()
				.getResource("/views/welcome.css").toExternalForm());
		window.setScene(startup);
		window.setTitle(title);

		window.getIcons()
				.add(new Image(Main.class.getResourceAsStream("/icon.png")));
		window.show();
	}

	private static void configure() {

		if (IOManager.fileExists(configPath)) {

			System.out.println("Config file exists. Loading it..");

			String[] config = IOManager.loadFile(configPath);

			for (String c : config) {

				String[] keyValPair = c.split(",");

				switch (keyValPair[0]) {
				case "dbDirectory":
					if (keyValPair.length > 0) {
						System.out.println(
								"Updating db directory & schema path.");
						dbDirectory = keyValPair[1];
						schemaPath = formSchemaPath();
					}
					break;
				case "dbName":
					if (keyValPair.length > 0) {
						System.out.println("Updating db name.");
						dbName = keyValPair[1];
					}
					break;
				default:
					break;
				}
			}

		} else {

			writeDefaultConfig();
		}
	}

	private static void writeDefaultConfig() {

		String defaultConfig = "dbDirectory,dbs/" + "\n" + "dbName,";

		IOManager.writeFile(defaultConfig, configPath);
	}

	private static String formDbPath() {

		return dbDirectory + dbNamePrefix + versionExtension + dbVersion
				+ dbExtension;
	}

	public static String formDbName() {

		return dbNamePrefix + versionExtension + dbVersion + dbExtension;
	}

	private static String formSchemaPath() {

		return dbDirectory + "schema" + versionExtension + currentVersion
				+ ".sql";
	}

	private static String extractVersion(String dbName) {

		return "01";
	}

	private static void writeSchema() {
		// TODO Auto-generated method stub

	}

	public static void restoreDefaults() {

		configPath = "planner.cfg";
		dbDirectory = "dbs/";
		dbNamePrefix = "";
		dbExtension = ".db";
		versionExtension = ".v";
		currentVersion = "01";
		dbVersion = currentVersion;
		schemaPath = formSchemaPath();
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

			dbName = formDbName();
			dbDirectory = chosen.getParent();

			Main.sqlite.connectToDb(dbName);

			writeCurrentConfig();
		}
	}

	private static void writeCurrentConfig() {

		String defaultConfig = "dbDirectory," + dbDirectory + "\n" + "dbName,"
				+ dbName;

		IOManager.writeFile(defaultConfig, configPath);
	}

	public static void initializeDb(String name) throws SqliteWrapperException {

		dbNamePrefix = name;

		try {

			sqlite.createDb(formDbName());

		} catch (SqliteWrapperException e) {

			showAlert(AlertType.ERROR, "Cannot create profile",
					"Profile with that name already exists");

		}

		sqlite.executeFromFile(schemaPath);
	}

	public static void showAlert(AlertType type, String issue, String reason) {

		Logger.post(new Log("showAlert", issue + ":" + "\n" + reason,
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
