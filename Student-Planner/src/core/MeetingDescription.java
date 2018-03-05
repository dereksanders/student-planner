package core;

import java.sql.SQLException;
import java.time.LocalDate;

import sqlite.SqliteWrapperException;

public class MeetingDescription {

	// MeetingDate attributes
	private int setID;
	public LocalDate date;

	public MeetingSetDescription set;

	public MeetingDescription(int setID, LocalDate date)
			throws SQLException, SqliteWrapperException {

		this.setID = setID;
		this.date = date;

		this.set = MeetingSet.findMeetingSet(setID);
	}
}
