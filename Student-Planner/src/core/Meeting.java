package core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import sqlite.SqliteWrapperException;
import utility.DateTimeUtil;

public class Meeting {

	public enum Lookup {

		SET_ID(1), DATE(2);

		public int index;

		private Lookup(int index) {
			this.index = index;
		}
	};

	public static ArrayList<MeetingDescription> getMeetingsWeekOf(
			LocalDate date) throws SqliteWrapperException, SQLException {

		ArrayList<MeetingDescription> meetingsThisWeek = new ArrayList<>();

		LocalDate startOfWeek = DateTimeUtil.getStartOfWeek(date);

		long startOfWeekJulian = startOfWeek.toEpochDay();

		ResultSet findMeetings = Main.active.db
				.query("select * from meeting_date where date_of >= "
						+ startOfWeekJulian + " and date_of < "
						+ startOfWeekJulian + 7);

		int numMeetings = 0;

		while (findMeetings.next()) {

			numMeetings++;
			int set = findMeetings.getInt(Meeting.Lookup.SET_ID.index);
			LocalDate dateOf = LocalDate.ofEpochDay(
					findMeetings.getLong(Meeting.Lookup.DATE.index));

			meetingsThisWeek.add(new MeetingDescription(set, dateOf));
		}

		System.out.println("Found " + numMeetings + " the week of " + date);

		return meetingsThisWeek;
	}

	public static MeetingDescription getMeetingDuring(LocalDateTime dateTime) {
		return null;
	}
}
