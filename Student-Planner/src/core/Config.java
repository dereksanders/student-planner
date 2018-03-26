package core;

import java.io.IOException;

import utility.IOUtil;

/**
 * The Class Config.
 */
public class Config {

	private String path;

	private DbFilename dbFilename;
	private String dbDirectory = "dbs";
	private String schemaPath = formSchemaPath();

	public static final String DEFAULT_PATH = "planner.cfg";

	/**
	 * Instantiates a new config.
	 *
	 * @throws InitializationException
	 *             the initialization exception
	 */
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

	/**
	 * Load config file and populate fields from it.
	 */
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

	/**
	 * Write config file.
	 */
	public void write() {

		String config = "dbDirectory," + this.dbDirectory + "\n" + "dbName,"
				+ this.dbFilename;

		IOUtil.writeFile(config, this.path);
	}

	/**
	 * Gets the db path.
	 *
	 * @return the db path
	 */
	public String getDbPath() {

		return this.dbDirectory + "/" + this.dbFilename;
	}

	/**
	 * Form schema path.
	 *
	 * @return the string
	 */
	private String formSchemaPath() {

		return this.dbDirectory + "/" + "schema" + ".v" + Main.CURRENT_VERSION
				+ ".sql";
	}

	/**
	 * Write schema.
	 */
	private void writeSchema() {

	}

	/**
	 * Sets the db directory.
	 *
	 * @param dbDirectory
	 *            the new db directory
	 */
	public void setDbDirectory(String dbDirectory) {
		this.dbDirectory = dbDirectory;
	}

	/**
	 * Sets the db filename.
	 *
	 * @param dbFilename
	 *            the new db filename
	 */
	public void setDbFilename(DbFilename dbFilename) {
		this.dbFilename = dbFilename;
	}

	/**
	 * Gets the db filename.
	 *
	 * @return the db filename
	 */
	public DbFilename getDbFilename() {
		return dbFilename;
	}

	/**
	 * Gets the db directory.
	 *
	 * @return the db directory
	 */
	public String getDbDirectory() {
		return dbDirectory;
	}

	/**
	 * Gets the schema path.
	 *
	 * @return the schema path
	 */
	public String getSchemaPath() {
		return schemaPath;
	}
}
