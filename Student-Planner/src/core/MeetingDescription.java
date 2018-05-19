package core;

import java.sql.SQLException;
import java.time.LocalDate;

import sqlite.SqliteWrapperException;

/**
 * The Class MeetingDescription.
 */
public class MeetingDescription {

	public int setID;
	public LocalDate date;

	public MeetingSetDescription set;

	/**
	 * Instantiates a new meeting description.
	 *
	 * @param setID
	 *            the set ID
	 * @param date
	 *            the date
	 * @throws SQLException
	 *             the SQL exception
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 */
	public MeetingDescription(int setID, LocalDate date)
			throws SQLException, SqliteWrapperException {

		this.setID = setID;
		this.date = date;

		this.set = MeetingSet.findMeetingSet(setID);
	}

	@Override
	public boolean equals(Object o) {

		if (o instanceof MeetingDescription) {

			if (((MeetingDescription) o).setID == this.setID
					&& ((MeetingDescription) o).date.equals(this.date)) {

				return true;
			}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		String description = "";

		if (this.set.isCourseMeeting()) {

			description = this.set.getCourse() + " " + this.set.getType() + ": "
					+ this.set.getStart() + " - " + this.set.getEnd() + " ("
					+ this.date + ")";
		} else {

			description = this.set.getName() + " " + this.set.getType() + ": "
					+ this.set.getStart() + " - " + this.set.getEnd() + " ("
					+ this.date + ")";
		}

		return description;
	}
}