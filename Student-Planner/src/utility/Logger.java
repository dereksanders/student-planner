package utility;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Logger {

	private static String logFile;

	public static void initialize(String path) {

		logFile = path;

		if (!Files.exists(Paths.get(logFile))) {
			IOManager.writeFile("{ \"logs\":[", logFile);
		}
	}

	public static void post(Log log) {

		IOManager.appendToFile(log.toString(), logFile);
	}
}
