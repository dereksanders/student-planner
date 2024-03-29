package test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import sqlite.SqliteWrapper;
import sqlite.SqliteWrapperException;
import utility.IOUtil;

public class SqliteWrapperTest {

	private static SqliteWrapper sqlite;
	private static String testDbDirectory = "sqlliteTestDbDirectory/";
	private static String dbName = "test";

	@BeforeClass
	public static void init() throws IOException {

		IOUtil.createDirectory(testDbDirectory);
		sqlite = new SqliteWrapper(testDbDirectory);

		IOUtil.deleteFile(testDbDirectory + dbName);
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

		Statement sql = sqlite.getConnection().createStatement();

		ResultSet results = sql.executeQuery("select * from term");

		while (results.next()) {

			System.out.println("ID: " + results.getInt(1) + ", Name: "
					+ results.getString(2) + ", Price: "
					+ results.getDouble(3));
		}

		sql.execute("drop table term");
		results.close();
		sql.close();
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

		Statement sql = sqlite.getConnection().createStatement();

		sql.execute(
				"create table employees (id integer primary key, name text)");
		sql.execute(
				"insert into employees(id, name) values(1, \'Walter White\')");

		ResultSet results = sql.executeQuery("select * from employees");

		while (results.next()) {

			System.out.println("ID: " + results.getInt(1) + ", Name: "
					+ results.getString(2));
		}

		results.close();

		sql.execute("drop table employees");
		sql.execute(
				"create table employees (id integer primary key, name text)");
		sql.execute("insert into employees(id, name) values(2, \'Dingus\')");

		ResultSet results2 = sql.executeQuery("select * from employees");

		while (results2.next()) {

			System.out.println("ID: " + results2.getInt(1) + ", Name: "
					+ results2.getString(2));
		}

		results2.close();
		sql.execute("drop table employees");
		sql.close();
	}

	@AfterClass
	public static void cleanup() throws SqliteWrapperException {

		sqlite.disconnectFromDb(dbName);
		IOUtil.deleteFile(testDbDirectory + dbName);
	}
}
