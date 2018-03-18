package test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import core.InitializationException;
import core.Main;
import sqlite.SqliteWrapperException;
import utility.IOUtil;

public class MainTest {

	private static String testDbDirectory = "testDbDirectory/";

	@BeforeClass
	public static void init() throws IOException {

		IOUtil.createDirectory(testDbDirectory);
	}

	@Test(expected = InitializationException.class)
	public void loadNonExistantDb() throws SqliteWrapperException,
			InitializationException, IOException {

		String testName = "loadNonExistantDb";

		IOUtil.writeFile("dbDirectory," + testDbDirectory + testName + "/"
				+ "\n" + "dbName,nonExistantDb", "planner.cfg");

		Main.main(null);
	}

	@Test
	public void createNewDb() throws SqliteWrapperException,
			InitializationException, IOException {

		String testName = "createNewDb";

		IOUtil.writeFile("dbDirectory," + testDbDirectory + testName + "/",
				"planner.cfg");

		Main.main(null);

		assertTrue(IOUtil.fileExists(testDbDirectory + testName));
	}

	@AfterClass
	public static void cleanup() {

		IOUtil.deleteFile("planner.cfg");
	}
}
