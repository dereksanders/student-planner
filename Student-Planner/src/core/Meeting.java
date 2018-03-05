package core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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

		while (findMeetings.next()) {

			int set = findMeetings.getInt(1);
			LocalDate dateOf = LocalDate.ofEpochDay(findMeetings.getLong(2));

			meetingsThisWeek.add(new MeetingDescription(set, dateOf));
		}

		return meetingsThisWeek;
	}
}
