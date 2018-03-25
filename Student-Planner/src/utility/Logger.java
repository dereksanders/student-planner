package utility;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The Class Logger.
 */
public class Logger {

	private String path;

	public static final String DEFAULT_PATH = "history.log";

	/**
	 * Instantiates a new logger.
	 */
	public Logger() {

		this.path = DEFAULT_PATH;

		if (!Files.exists(Paths.get(this.path))) {
			// Writes the beginning of a JSON array of logs.
			IOUtil.writeFile("{ \"logs\":[", this.path);
		}
	}

	/**
	 * Instantiates a new logger.
	 *
	 * @param path
	 *            the log file path
	 */
	public Logger(String path) {

		this.path = path;

		if (!Files.exists(Paths.get(this.path))) {
			// Writes the beginning of a JSON array of logs.
			IOUtil.writeFile("{ \"logs\":[", this.path);
		}
	}

	/**
	 * Write a new log to the logger path.
	 *
	 * @param log
	 *            the log
	 */
	public void post(Log log) {

		IOUtil.appendToFile(log.toString(), this.path);
	}
}
