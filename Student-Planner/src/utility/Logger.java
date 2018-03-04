package utility;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Logger {

	private String path;

	public static final String DEFAULT_PATH = "history.log";

	public Logger(String path) {

		this.path = path;

		if (!Files.exists(Paths.get(this.path))) {
			IOManager.writeFile("{ \"logs\":[", this.path);
		}
	}

	public void post(Log log) {

		IOManager.appendToFile(log.toString(), this.path);
	}
}
