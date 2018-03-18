package utility;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Logger {

	private String path;

	public static final String DEFAULT_PATH = "history.log";

	public Logger() {

		this.path = DEFAULT_PATH;

		if (!Files.exists(Paths.get(this.path))) {
			IOUtil.writeFile("{ \"logs\":[", this.path);
		}
	}

	public void post(Log log) {

		IOUtil.appendToFile(log.toString(), this.path);
	}
}
