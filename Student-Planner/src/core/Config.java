package core;

import java.io.IOException;

import utility.IOUtil;

public class Config {

	private String path;

	private DbFilename dbFilename;
	private String dbDirectory = "dbs";
	private String schemaPath = formSchemaPath();

	public static final String DEFAULT_PATH = "planner.cfg";

	public Config() throws InitializationException {

		this.path = Config.DEFAULT_PATH;

		if (IOUtil.fileExists(this.path)) {

			this.load();

		} else {

			// Write default configuration to the desired path.
			this.write();
		}

		try {
			IOUtil.createDirectory(this.dbDirectory);
		} catch (IOException e) {
			throw new InitializationException(
					"Non-directory file with same name as desired dbDirectory exists.");
		}

		writeSchema();
	}

	private void load() {

		String[] config = IOUtil.loadFile(this.path);

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
					this.dbFilename = new DbFilename(keyValPair[1]);
				}
				break;
			default:
				break;
			}
		}
	}

	public void write() {

		String config = "dbDirectory," + this.dbDirectory + "\n" + "dbName,"
				+ this.dbFilename;

		IOUtil.writeFile(config, this.path);
	}

	public String getDbPath() {

		return this.dbDirectory + "/" + this.dbFilename;
	}

	private String formSchemaPath() {

		return this.dbDirectory + "/" + "schema" + ".v" + Main.CURRENT_VERSION
				+ ".sql";
	}

	private void writeSchema() {

	}

	public void setDbDirectory(String dbDirectory) {
		this.dbDirectory = dbDirectory;
	}

	public void setDbFilename(DbFilename dbFilename) {
		this.dbFilename = dbFilename;
	}

	public DbFilename getDbFilename() {
		return dbFilename;
	}

	public String getDbDirectory() {
		return dbDirectory;
	}

	public String getSchemaPath() {
		return schemaPath;
	}
}
