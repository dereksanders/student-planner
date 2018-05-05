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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		String description = "";

		if (this.set.isCourseMeeting) {

			description = this.set.course + " " + this.set.type + "\n"
					+ this.date + "\n" + this.set.start + " - " + this.set.end;
		} else {

			description = this.set.name + " " + this.set.type + "\n" + this.date
					+ "\n" + this.set.start + " - " + this.set.end;
		}

		return description;
	}
}
