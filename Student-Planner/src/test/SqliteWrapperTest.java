package test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import sqlite.SqliteWrapper;
import sqlite.SqliteWrapperException;
import utility.IOManager;

public class SqliteWrapperTest {

	private static SqliteWrapper sqlite;
	private static String testDbDirectory = "sqlliteTestDbDirectory/";
	private static String dbName = "test";

	@BeforeClass
	public static void init() throws IOException {

		IOManager.createDirectory(testDbDirectory);
		sqlite = new SqliteWrapper(testDbDirectory);

		IOManager.deleteFile(testDbDirectory + dbName);
	}

	@Test
	public void sanity() throws SqliteWrapperException, SQLException {

		sanitySetup();
		sanityWorkload();
		sanityWorkloadFromFile();
	}

	private void sanityWorkloadFromFile()
			throws SqliteWrapperException, SQLException {

		sqlite.executeFromFile("create.sql");

		ResultSet results = sqlite.query("select * from term");

		while (results.next()) {

			System.out.println("ID: " + results.getInt(1) + ", Name: "
					+ results.getString(2) + ", Price: "
					+ results.getDouble(3));
		}

		sqlite.execute("drop table term");
	}

	private void sanitySetup() throws SqliteWrapperException {

		boolean connToNonExistantDbFails = false;

		try {
			sqlite.connectToDb("nonExistantDb");
		} catch (SqliteWrapperException e) {
			connToNonExistantDbFails = true;
		}

		assertTrue(connToNonExistantDbFails);

		sqlite.createDb(dbName);

		boolean createDuplicateDbFails = false;

		try {
			sqlite.createDb(dbName);
		} catch (SqliteWrapperException e) {
			createDuplicateDbFails = true;
		}

		assertTrue(createDuplicateDbFails);
	}

	private void sanityWorkload() throws SqliteWrapperException, SQLException {

		sqlite.execute(
				"create table employees (id integer primary key, name text)");
		sqlite.execute(
				"insert into employees(id, name) values(1, \'Walter White\')");

		ResultSet results = sqlite.query("select * from employees");

		while (results.next()) {

			System.out.println("ID: " + results.getInt(1) + ", Name: "
					+ results.getString(2));
		}

		sqlite.execute("drop table employees");

		sqlite.execute(
				"create table employees (id integer primary key, name text)");
		sqlite.execute("insert into employees(id, name) values(2, \'Dingus\')");

		ResultSet results2 = sqlite.query("select * from employees");

		while (results2.next()) {

			System.out.println("ID: " + results2.getInt(1) + ", Name: "
					+ results2.getString(2));
		}

		sqlite.execute("drop table employees");
	}

	@AfterClass
	public static void cleanup() throws SqliteWrapperException {

		sqlite.disconnectFromDb(dbName);
		IOManager.deleteFile(testDbDirectory + dbName);
	}
}
