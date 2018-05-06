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
	public static MeetingSetDescription getMeetingSetDuring(
			LocalDateTime dateTime)
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
		sql.close();

		return setWithMeetingDuringTime;
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
	public static MeetingDescription getMeetingDuring(LocalDateTime dateTime)
			throws SqliteWrapperException, SQLException {

		MeetingDescription meetingDuringTime = null;

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

			LocalTime time = dateTime.toLocalTime();

			LocalTime meetingStart = null;
			LocalTime meetingEnd = null;

			// There should only be one MeetingSet per ID.
			while (meetingSet.next()) {

				meetingStart = LocalTime.ofSecondOfDay(
						meetingSet.getLong(MeetingSet.Lookup.START_TIME.index));

				meetingEnd = LocalTime.ofSecondOfDay(
						meetingSet.getLong(MeetingSet.Lookup.END_TIME.index));
			}
			meetingSet.close();

			if ((time.equals(meetingStart) || time.isAfter(meetingStart))
					&& (time.equals(meetingEnd) || time.isBefore(meetingEnd))) {

				meetingDuringTime = meeting;
				break;
			}
		}
		sql.close();

		return meetingDuringTime;
	}

	/**
	 * Gets the conflicts.
	 *
	 * @param meetingDates
	 *            the meeting dates
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @return the conflicts
	 * @throws SQLException
	 * @throws SqliteWrapperException
	 */
	public static ArrayList<MeetingDescription> getConflicts(
			ArrayList<LocalDate> meetingDates, LocalTime start, LocalTime end)
			throws SQLException, SqliteWrapperException {

		ArrayList<MeetingDescription> conflicts = new ArrayList<>();

		Statement sql = Main.active.db.getConnection().createStatement();

		for (LocalDate date : meetingDates) {

			ResultSet meetingsThatDay = sql
					.executeQuery("select * from meeting_date where date_of = "
							+ date.toEpochDay());

			ArrayList<MeetingDescription> meetings = new ArrayList<>();

			while (meetingsThatDay.next()) {

				meetings.add(new MeetingDescription(
						meetingsThatDay.getInt(Meeting.Lookup.SET_ID.index),
						LocalDate.ofEpochDay(meetingsThatDay
								.getLong(Meeting.Lookup.DATE.index))));
			}
			meetingsThatDay.close();

			for (MeetingDescription meeting : meetings) {

				ResultSet meetingSet = sql
						.executeQuery("select * from meeting_set where id = "
								+ meeting.setID);

				LocalTime meetingStart = null;
				LocalTime meetingEnd = null;

				// There should only be one MeetingSet per ID.
				while (meetingSet.next()) {

					meetingStart = LocalTime.ofSecondOfDay(meetingSet
							.getLong(MeetingSet.Lookup.START_TIME.index));

					meetingEnd = LocalTime.ofSecondOfDay(meetingSet
							.getLong(MeetingSet.Lookup.END_TIME.index));

					if (!(start.isAfter(meetingEnd) || start.equals(meetingEnd)
							|| end.isBefore(meetingStart)
							|| end.equals(meetingStart))) {

						conflicts.add(meeting);
					}
				}
				meetingSet.close();
			}
		}
		sql.close();

		return conflicts;
	}

	public static void deleteMeeting(MeetingDescription meeting)
			throws SQLException, SqliteWrapperException {

		Statement sql = Main.active.db.getConnection().createStatement();

		sql.execute("delete from meeting_date where set_id = " + meeting.setID
				+ " and date_of = " + meeting.date.toEpochDay());

		ResultSet countMeetingsInSet = sql.executeQuery(
				"select count(*) from meeting_date where set_id = "
						+ meeting.setID);

		int numMeetings = 0;

		if (countMeetingsInSet.next()) {
			numMeetings = countMeetingsInSet.getInt(1);
		}

		// If the set no longer has any meetings, delete it.
		if (numMeetings == 0) {

			MeetingSet.deleteMeetingSet(meeting.setID);
		}

		countMeetingsInSet.close();
		sql.close();

		Main.active.update();
	}
}
