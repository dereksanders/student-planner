package core;

import java.time.LocalDate;

/**
 * The Class TermDescription.
 */
public class TermDescription {

	private String name;
	public LocalDate start;

	/**
	 * Instantiates a new term description.
	 *
	 * @param name
	 *            the name
	 * @param start
	 *            the start
	 */
	public TermDescription(String name, LocalDate start) {

		this.name = name;
		this.start = start;
	}

	/**
	 * Gets the start day.
	 *
	 * @return the start day
	 */
	public long getStartDay() {

		return start.toEpochDay();
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
