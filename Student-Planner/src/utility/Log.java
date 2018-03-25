package utility;

import java.time.LocalDateTime;

/**
 * The Class Log.
 */
public class Log {

	/**
	 * The Enum Severity.
	 */
	public enum Severity {
		INFO, WARNING, ERROR;
	}

	private LocalDateTime time;
	private String function;
	private String action;
	private Severity severity;

	/**
	 * Instantiates a new log.
	 *
	 * @param function
	 *            the function
	 * @param action
	 *            the action
	 * @param severity
	 *            the severity
	 */
	public Log(String function, String action, Severity severity) {
		this.time = LocalDateTime.now();
		this.function = function;
		this.action = action;
		this.severity = severity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {

		// Formatted as an element of a JSON array of logs.
		String desc = "\n\t{\n\t\t" + "\"time\":" + "\"" + this.time + "\""
				+ "\n\t\t" + "\"severity\":" + "\"" + severity + "\"" + "\n\t\t"
				+ "\"function\":" + "\"" + function + "\"" + "\n\t\t"
				+ "\"action\":" + "\"" + action + "\"" + "\n\t}";

		return desc;
	}
}
