package sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import utility.IOManager;

/**
 * The Class SqliteWrapper.
 */
public class SqliteWrapper {

	public Connection connection;
	private Statement sql;
	private String dbDirectory;

	private static String jdbcUrl = "jdbc:sqlite:";

	/**
	 * Instantiates a new sqlite wrapper.
	 *
	 * @param dbDirectory
	 *            the database directory
	 */
	public SqliteWrapper(String dbDirectory) {
		this.dbDirectory = dbDirectory;
	}

	/**
	 * Db exists.
	 *
	 * @param dbName
	 *            the db name
	 * @return true, if successful
	 */
	public boolean dbExists(String dbName) {

		return IOManager.fileExists(this.dbDirectory + "/" + dbName);
	}

	/**
	 * Create a database.
	 *
	 * @param dbName
	 *            the name for the new database
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 */
	public void createDb(String dbName) throws SqliteWrapperException {

		assertDbDoesNotExist(dbName);

		String url = dbUrl(this.dbDirectory, dbName);

		try {

			this.connection = DriverManager.getConnection(url);
			this.sql = this.connection.createStatement();

		} catch (SQLException e) {

			System.out.println(e.getMessage());
		}
	}

	/**
	 * Create a database or connect to it if it already exists.
	 * 
	 * The purpose of this function is to explicitly tolerate the scenario where
	 * we try to create a database that already exists by connecting to the
	 * existing database.
	 *
	 * @param dbName
	 *            the name for the new database
	 * @param connectIfExists
	 *            connects to the database if it already exists
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 */
	public void createDb(String dbName, boolean connectIfExists)
			throws SqliteWrapperException {

		if (!connectIfExists) {

			createDb(dbName);

		} else {

			String url = dbUrl(this.dbDirectory, dbName);

			try {

				this.connection = DriverManager.getConnection(url);
				this.sql = this.connection.createStatement();

			} catch (SQLException e) {

				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * Connect to a database.
	 *
	 * @param dbName
	 *            the name of the database to connect to
	 * @throws SqliteWrapperException
	 *             if the database does not exist
	 */
	public void connectToDb(String dbName) throws SqliteWrapperException {

		assertDbExists(dbName);

		String url = dbUrl(this.dbDirectory, dbName);

		try {

			this.connection = DriverManager.getConnection(url);
			this.sql = this.connection.createStatement();

		} catch (SQLException e) {

			System.out.println(e.getMessage());
		}
	}

	public void disconnectFromDb(String dbName) throws SqliteWrapperException {

		if (connectionExists()) {
			try {
				this.connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Db url.
	 *
	 * @param dbDirectory
	 *            the db directory
	 * @param dbName
	 *            the db name
	 * @return the string
	 */
	private static String dbUrl(String dbDirectory, String dbName) {

		return jdbcUrl + dbDirectory + "/" + dbName;
	}

	/**
	 * Executes an SQL statement on the database currently connected to.
	 *
	 * @param statement
	 *            the statement
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 */
	public void execute(String statement) throws SqliteWrapperException {

		assertConnectionExists();

		try {

			if (!this.sql.execute(statement)) {
				System.out.println("Create table success: " + statement);
			}

		} catch (SQLException e) {

			new SqliteWrapperException("SQL error " + e.getErrorCode()
					+ " + occurred:\n" + e.getMessage());
		}
	}

	public void executeFromFile(String path) throws SqliteWrapperException {

		// Execute each command in the db schema one at a time.
		String[] lines = IOManager.loadFile(path);
		String schema = "";
		for (String s : lines) {
			schema += s;
		}

		String[] statements = schema.split(";");

		for (String s : statements) {
			execute(s);
		}
	}

	/**
	 * Query.
	 *
	 * @param statement
	 *            the statement
	 * @return the result set
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 */
	public ResultSet query(String statement) throws SqliteWrapperException {

		ResultSet results = null;

		assertConnectionExists();

		try {

			results = this.sql.executeQuery(statement);

		} catch (SQLException e) {

			throw new SqliteWrapperException("SQL error " + e.getErrorCode()
					+ " occurred:\n" + e.getMessage());
		}

		System.out.println(results);

		return results;
	}

	/**
	 * Connection exists.
	 *
	 * @return true, if successful
	 */
	public boolean connectionExists() {

		return (this.connection != null);
	}

	/**
	 * Assert connection exists.
	 *
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 */
	private void assertConnectionExists() throws SqliteWrapperException {

		if (!connectionExists()) {

			System.out.println("NO DB CONNECTION EXISTS!!!");
			throw new SqliteWrapperException("No database connection exists.");
		}
	}

	/**
	 * Assert db does not exist.
	 *
	 * @param dbName
	 *            the db name
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 */
	private void assertDbDoesNotExist(String dbName)
			throws SqliteWrapperException {

		if (dbExists(dbName)) {

			throw new SqliteWrapperException("Database already exists.");
		}
	}

	/**
	 * Assert db exists.
	 *
	 * @param dbName
	 *            the db name
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 */
	private void assertDbExists(String dbName) throws SqliteWrapperException {

		if (!dbExists(dbName)) {

			throw new SqliteWrapperException("Database does not exist.");
		}
	}
}