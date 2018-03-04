package core;

import java.io.IOException;

import utility.IOManager;

public class Config {

	private String path;
	private String dbName = "";
	private String dbDirectory = "dbs";
	private String schemaPath = formSchemaPath();

	public static final String DEFAULT_PATH = "planner.cfg";

	public Config(String path) throws InitializationException {

		this.path = path;

		if (IOManager.fileExists(this.path)) {

			this.load();

		} else {

			// Write default configuration to the desired path.
			this.write();
		}

		try {
			IOManager.createDirectory(this.dbDirectory);
		} catch (IOException e) {
			throw new InitializationException(
					"Non-directory file with same name as desired dbDirectory exists.");
		}

		writeSchema();
	}

	private void load() {

		String[] config = IOManager.loadFile(this.path);

		for (String c : config) {

			String[] keyValPair = c.split(",");

			switch (keyValPair[0]) {
			case "dbDirectory":
				if (keyValPair.length > 1) {
					System.out.println("Updating db directory & schema path.");
					this.dbDirectory = keyValPair[1];
					schemaPath = formSchemaPath();
				}
				break;
			case "dbName":
				if (keyValPair.length > 1) {
					System.out.println("Updating db name.");
					this.dbName = keyValPair[1];
				}
				break;
			default:
				break;
			}
		}
	}

	public void write() {

		String config = "dbDirectory," + this.dbDirectory + "\n" + "dbName,"
				+ this.dbName;

		IOManager.writeFile(config, this.path);
	}

	private String formSchemaPath() {

		return this.dbDirectory + "/" + "schema" + Main.versionExtension
				+ Main.currentVersion + ".sql";
	}

	private void writeSchema() {

	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public void setDbDirectory(String dbDirectory) {
		this.dbDirectory = dbDirectory;
	}

	public String getDbName() {
		return dbName;
	}

	public String getDbDirectory() {
		return dbDirectory;
	}

	public String getSchemaPath() {
		return schemaPath;
	}
}
