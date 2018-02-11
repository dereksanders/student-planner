package core;

import java.time.LocalDate;

public class TermDescription {

	private String name;
	private LocalDate start;

	public TermDescription(String name, LocalDate start) {

		this.name = name;
		this.start = start;
	}

	public long getStartDay() {

		return start.toEpochDay();
	}

	public String toString() {

		return this.name + " (" + start.getYear() + ")";
	}
}
