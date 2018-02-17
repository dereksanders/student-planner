package utility;

import java.time.LocalDateTime;

public class Log {

	public enum Severity {
		INFO, WARNING, ERROR;
	}

	private LocalDateTime time;
	private String function;
	private String action;
	private Severity severity;

	public Log(String function, String action, Severity severity) {
		this.time = LocalDateTime.now();
		this.function = function;
		this.action = action;
		this.severity = severity;
	}

	public String toString() {
		String desc = LocalDateTime.now() + "\n" + function + "\n" + action + "\n\n";
		return desc;
	}
}
