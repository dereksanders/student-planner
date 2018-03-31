package core;

import java.time.LocalDate;

/**
 * The Class TermDescription.
 */
public class TermDescription {

	private String name;
	private LocalDate start;
	private LocalDate end;

	/**
	 * Instantiates a new term description.
	 *
	 * @param name
	 *            the name
	 * @param start
	 *            the start
	 */
	public TermDescription(String name, LocalDate start, LocalDate end) {

		this.name = name;
		this.start = start;
		this.end = end;
	}

	/**
	 * Gets the start day.
	 *
	 * @return the start day
	 */
	public long getStartDay() {

		return start.toEpochDay();
	}

	public LocalDate getStart() {
		return this.start;
	}

	public LocalDate getEnd() {
		return this.end;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {

		return this.name + " (" + start.getYear() + ")";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {

		if (o instanceof TermDescription) {

			if (name.equals(((TermDescription) o).name)
					&& start.equals(((TermDescription) o).start)) {

				return true;
			}
		}

		return false;
	}
}
