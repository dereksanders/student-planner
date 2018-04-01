package core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import sqlite.SqliteWrapperException;
import utility.DateTimeUtil;

/**
 * The Class Meeting.
 */
public class Meeting {

	/**
	 * The Enum Lookup.
	 */
	public enum Lookup {

		SET_ID(1), DATE(2);

		public int index;

		/**
		 * Instantiates a new lookup.
		 *
		 * @param index
		 *            the index
		 */
		private Lookup(int index) {
			this.index = index;
		}
	};

	public static final String[] COURSE_TYPES = { "Lecture", "Tutorial", "Lab",
			"Seminar", "Other" };

	public static final String[] NON_COURSE_TYPES = { "Club", "Work", "Sports",
			"Other" };

	/**
	 * Gets all meetings taking place during the week of the specified date.
	 *
	 * @param date
	 *            the date
	 * @return the meetings taking place that week
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static ArrayList<MeetingDescription> getMeetingsWeekOf(
			LocalDate date) throws SqliteWrapperException, SQLException {

		ArrayList<MeetingDescription> meetingsThisWeek = new ArrayList<>();

		LocalDate startOfWeek = DateTimeUtil.getStartOfWeek(date);
		long startOfWeekJulian = startOfWeek.toEpochDay();
		long startOfNextWeekJulian = startOfWeekJulian + 7;

		Statement sql = Main.active.db.getConnection().createStatement();
		ResultSet findMeetings = sql
				.executeQuery("select * from meeting_date where date_of >= "
						+ startOfWeekJulian + " and date_of < "
						+ startOfNextWeekJulian);

		while (findMeetings.next()) {

			int set = findMeetings.getInt(Meeting.Lookup.SET_ID.index);
			LocalDate dateOf = LocalDate.ofEpochDay(
					findMeetings.getLong(Meeting.Lookup.DATE.index));

			meetingsThisWeek.add(new MeetingDescription(set, dateOf));
			System.out.println(set + ", " + dateOf.toEpochDay());
		}
		findMeetings.close();
		sql.close();

		return meetingsThisWeek;
	}

	/**
	 * Gets the meeting taking place during the specified date time.
	 * 
	 * Returns null if no meeting is taking place.
	 *
	 * @param dateTime
	 *            the date time
	 * @return the meeting during
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static MeetingSetDescription getMeetingDuring(LocalDateTime dateTime)
			throws SqliteWrapperException, SQLException {

		MeetingSetDescription setWithMeetingDuringTime = null;

		Statement sql = Main.active.db.getConnection().createStatement();
		ResultSet meetingsThatDay = sql
				.executeQuery("select * from meeting_date where date_of = "
						+ dateTime.toLocalDate().toEpochDay());

		ArrayList<MeetingDescription> meetings = new ArrayList<>();

		while (meetingsThatDay.next()) {

			meetings.add(new MeetingDescription(
					meetingsThatDay.getInt(Meeting.Lookup.SET_ID.index),
					LocalDate.ofEpochDay(meetingsThatDay
							.getLong(Meeting.Lookup.DATE.index))));
		}
		meetingsThatDay.close();

		for (MeetingDescription meeting : meetings) {

			ResultSet meetingSet = sql.executeQuery(
					"select * from meeting_set where id = " + meeting.setID);

			LocalTime meetingStart = null;
			LocalTime meetingEnd = null;

			// There should only be one MeetingSet per ID.
			while (meetingSet.next()) {

				meetingStart = LocalTime.ofSecondOfDay(
						meetingSet.getLong(MeetingSet.Lookup.START_TIME.index));

				meetingEnd = LocalTime.ofSecondOfDay(
						meetingSet.getLong(MeetingSet.Lookup.END_TIME.index));

				LocalTime time = dateTime.toLocalTime();

				if ((time.equals(meetingStart) || time.isAfter(meetingStart))
						&& (time.equals(meetingEnd)
								|| time.isBefore(meetingEnd))) {

					setWithMeetingDuringTime = new MeetingSetDescription(
							meetingSet.getInt(MeetingSet.Lookup.ID.index),
							LocalDate.ofEpochDay(meetingSet.getLong(
									MeetingSet.Lookup.TERM_START_DATE.index)),
							LocalDate.ofEpochDay(meetingSet.getLong(
									MeetingSet.Lookup.COURSE_START_TERM_START_DATE.index)),
							LocalDate.ofEpochDay(meetingSet.getLong(
									MeetingSet.Lookup.COURSE_END_TERM_START_DATE.index)),
							meetingSet.getString(
									MeetingSet.Lookup.COURSE_DEPT_ID.index),
							meetingSet.getInt(
									MeetingSet.Lookup.COURSE_CODE.index),
							meetingSet.getString(MeetingSet.Lookup.NAME.index),
							meetingSet.getString(
									MeetingSet.Lookup.MEETING_TYPE.index),
							meetingStart, meetingEnd, meetingSet.getBoolean(
									MeetingSet.Lookup.IS_COURSE_MEETING.index));

					break;
				}
			}
			meetingSet.close();
		}

		return setWithMeetingDuringTime;
	}

	public static ArrayList<MeetingDescription> getConflicts(
			ArrayList<LocalDate> meetingDates, LocalTime start, LocalTime end) {

		ArrayList<MeetingDescription> conflicts = new ArrayList<>();

		return conflicts;
	}
}
