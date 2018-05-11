package core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import javafx.scene.paint.Color;
import sqlite.SqliteWrapperException;
import utility.ColorUtil;

/**
 * The Class MeetingSet.
 */
public class MeetingSet {

	/**
	 * The Enum Lookup.
	 */
	public enum Lookup {

		ID(1), TERM_START_DATE(2), START_TIME(3), END_TIME(
				4), COURSE_START_TERM_START_DATE(5), COURSE_END_TERM_START_DATE(
						6), COURSE_DEPT_ID(7), COURSE_CODE(8), NAME(
								9), MEETING_TYPE(10), LOCATION(11), REPEAT(
										12), IS_COURSE_MEETING(13), COLOR(14);

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
	}

	public static final String[] COURSE_TYPES = { "Lecture", "Tutorial", "Lab",
			"Seminar", "Other" };

	public static final String[] NON_COURSE_TYPES = { "Club", "Work", "Sports",
			"Other" };

	public static final String[] REPEAT_OPTIONS = { "Weekly", "Bi-Weekly",
			"Monthly", "Never" };

	/**
	 * Adds the meeting set.
	 *
	 * @param term
	 *            the term
	 * @param course
	 *            the course
	 * @param meetingType
	 *            the meeting type
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param location
	 *            the location
	 * @param dates
	 *            the dates
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static void addCourseMeetingSet(TermDescription term,
			CourseDescription course, String meetingType, LocalTime start,
			LocalTime end, String location, String repeat,
			ArrayList<LocalDate> dates)
			throws SqliteWrapperException, SQLException {

		int id = getNextMeetingID();
		Statement sql = Main.active.db.getConnection().createStatement();

		sql.execute(
				"insert into meeting_set(id, term_start_date, start_time, end_time, "
						+ "course_start_term_start_date, course_end_term_start_date, "
						+ "course_dept_id, course_code, name, meeting_type, location, repeat, "
						+ "is_course_meeting_set, color) " + "values(" + id
						+ "," + term.getStartDay() + "," + start.toSecondOfDay()
						+ "," + end.toSecondOfDay() + ","
						+ course.startTerm.getStartDay() + ","
						+ course.endTerm.getStartDay() + ",\'" + course.dept
						+ "\'," + course.code + "," + "\'nullname\'" + ","
						+ "\'" + meetingType + "\'" + "," + "\'" + location
						+ "\'" + "," + "\'" + repeat + "\'" + ",1," + "\'"
						+ Course.getColor(course) + "\'" + ");");

		for (LocalDate d : dates) {

			sql.execute("insert into meeting_date(set_id, date_of) " + "values("
					+ id + ", " + d.toEpochDay() + ");");
		}
		sql.close();
		Main.active.update();
	}

	/**
	 * Adds the meeting set.
	 *
	 * @param term
	 *            the term
	 * @param meetingName
	 *            the meeting name
	 * @param meetingType
	 *            the meeting type
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param location
	 *            the location
	 * @param dates
	 *            the dates
	 * @param color
	 *            the color
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static void addNonCourseMeetingSet(TermDescription term,
			String meetingName, String meetingType, LocalTime start,
			LocalTime end, String location, String repeat,
			ArrayList<LocalDate> dates, Color color)
			throws SqliteWrapperException, SQLException {

		int id = getNextMeetingID();

		Statement sql = Main.active.db.getConnection().createStatement();
		sql.execute(
				"insert into meeting_set(id, term_start_date, start_time, end_time, "
						+ "course_start_term_start_date, course_end_term_start_date, "
						+ "course_dept_id, course_code, name, meeting_type, location, repeat, "
						+ "is_course_meeting_set, color) " + "values(" + id
						+ "," + term.getStartDay() + "," + start.toSecondOfDay()
						+ "," + end.toSecondOfDay() + ",0,0" + ",\'null\',0,"
						+ "\'" + meetingName + "\'" + "," + "\'" + meetingType
						+ "\'" + "," + "\'" + location + "\'" + "," + "\'"
						+ repeat + "\'" + ",0," + "\'"
						+ ColorUtil.colorToHex(color) + "\'" + ");");

		for (LocalDate d : dates) {

			sql.execute("insert into meeting_date(set_id, date_of) " + "values("
					+ id + ", " + d.toEpochDay() + ");");
		}
		sql.close();
		Main.active.update();
	}

	/**
	 * Gets the next meeting ID.
	 *
	 * @return the next meeting ID
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	private static int getNextMeetingID()
			throws SqliteWrapperException, SQLException {

		int maxID = 0;

		Statement sql = Main.active.db.getConnection().createStatement();

		if (meetingSetsExist()) {

			ResultSet rs = sql.executeQuery("select max(id) from meeting_set");

			while (rs.next()) {
				maxID = rs.getInt(1);
			}

			rs.close();
		}
		sql.close();

		return maxID + 1;
	}

	/**
	 * Meeting sets exist.
	 *
	 * @return true, if successful
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	private static boolean meetingSetsExist()
			throws SqliteWrapperException, SQLException {

		int numMeetingSets = 0;

		Statement sql = Main.active.db.getConnection().createStatement();
		ResultSet countMeetingSets = sql
				.executeQuery("select count(*) from meeting_set");

		while (countMeetingSets.next()) {

			numMeetingSets = countMeetingSets.getInt(1);
		}
		countMeetingSets.close();
		sql.close();

		return (numMeetingSets > 0);
	}

	/**
	 * Find meeting set with the specified ID.
	 *
	 * @param setID
	 *            the set ID
	 * @return the meeting set description
	 * @throws SQLException
	 *             the SQL exception
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 */
	public static MeetingSetDescription findMeetingSet(int setID)
			throws SQLException, SqliteWrapperException {

		MeetingSetDescription found = null;

		Statement sql = Main.active.db.getConnection().createStatement();
		ResultSet findMeetingSet = sql
				.executeQuery("select * from meeting_set where id = " + setID);

		if (findMeetingSet.next()) {

			long secondStart = findMeetingSet
					.getLong(MeetingSet.Lookup.START_TIME.index);
			long secondEnd = findMeetingSet
					.getLong(MeetingSet.Lookup.END_TIME.index);

			LocalTime startTime = LocalTime.ofSecondOfDay(secondStart);
			LocalTime endTime = LocalTime.ofSecondOfDay(secondEnd);

			found = new MeetingSetDescription(
					findMeetingSet.getInt(MeetingSet.Lookup.ID.index),
					LocalDate.ofEpochDay(findMeetingSet
							.getLong(MeetingSet.Lookup.TERM_START_DATE.index)),
					LocalDate.ofEpochDay(findMeetingSet.getLong(
							MeetingSet.Lookup.COURSE_START_TERM_START_DATE.index)),
					LocalDate.ofEpochDay(findMeetingSet.getLong(
							MeetingSet.Lookup.COURSE_END_TERM_START_DATE.index)),
					findMeetingSet
							.getString(MeetingSet.Lookup.COURSE_DEPT_ID.index),
					findMeetingSet.getInt(MeetingSet.Lookup.COURSE_CODE.index),
					findMeetingSet.getString(MeetingSet.Lookup.NAME.index),
					findMeetingSet
							.getString(MeetingSet.Lookup.MEETING_TYPE.index),
					findMeetingSet.getString(MeetingSet.Lookup.LOCATION.index),
					findMeetingSet.getString(MeetingSet.Lookup.REPEAT.index),
					startTime, endTime, findMeetingSet.getBoolean(
							MeetingSet.Lookup.IS_COURSE_MEETING.index));
		}
		findMeetingSet.close();
		sql.close();

		return found;
	}

	/**
	 * Gets the color.
	 *
	 * @param id
	 *            the id
	 * @return the color
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static String getColor(int id)
			throws SqliteWrapperException, SQLException {

		String color = "";

		Statement sql = Main.active.db.getConnection().createStatement();
		ResultSet getMeetingSet = sql
				.executeQuery("select * from meeting_set where id = " + id);

		if (getMeetingSet.next()) {

			color = getMeetingSet.getString(MeetingSet.Lookup.COLOR.index);
		}
		getMeetingSet.close();
		sql.close();

		return color;
	}

	/**
	 * Delete meeting set.
	 *
	 * @param setID
	 *            the set ID
	 * @throws SQLException
	 *             the SQL exception
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 */
	public static void deleteMeetingSet(int setID)
			throws SQLException, SqliteWrapperException {

		Statement sql = Main.active.db.getConnection().createStatement();

		sql.execute("delete from meeting_set where id = " + setID);
		sql.close();

		Main.active.update();
	}

	/**
	 * Gets all of the meetings in the set, ordered by date (ascending).
	 *
	 * @param id
	 *            the id
	 * @return the meetings in set
	 * @throws SQLException
	 *             the SQL exception
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 */
	public static ArrayList<MeetingDescription> getMeetingsInSet(int id)
			throws SQLException, SqliteWrapperException {

		ArrayList<MeetingDescription> meetings = new ArrayList<>();

		Statement sql = Main.active.db.getConnection().createStatement();

		ResultSet meetingsInSet = sql
				.executeQuery("select * from meeting_date where set_id = " + id
						+ " order by date_of asc");

		while (meetingsInSet.next()) {

			meetings.add(new MeetingDescription(id, LocalDate.ofEpochDay(
					meetingsInSet.getLong(Meeting.Lookup.DATE.index))));
		}
		meetingsInSet.close();
		sql.close();

		return meetings;
	}

	public static MeetingDescription getFirstMeeting(int id)
			throws SQLException, SqliteWrapperException {

		return getMeetingsInSet(id).get(0);

		/*
		 * MeetingDescription firstMeeting = null;
		 * 
		 * Statement sql = Main.active.db.getConnection().createStatement();
		 * 
		 * ResultSet meetingsInSet = sql
		 * .executeQuery("select * from meeting_date where set_id = " + id +
		 * " and date_of = (select MIN(date_of) from meeting_date)");
		 * 
		 * while (meetingsInSet.next()) {
		 * 
		 * System.out.println("First meeting found.");
		 * 
		 * firstMeeting = new MeetingDescription(id, LocalDate.ofEpochDay(
		 * meetingsInSet.getLong(Meeting.Lookup.DATE.index))); }
		 * meetingsInSet.close(); sql.close();
		 * 
		 * return firstMeeting;
		 */
	}

	public static MeetingDescription getLastMeeting(int id)
			throws SQLException, SqliteWrapperException {

		ArrayList<MeetingDescription> meetings = getMeetingsInSet(id);

		return meetings.get(meetings.size() - 1);

		/*
		 * MeetingDescription lastMeeting = null;
		 * 
		 * Statement sql = Main.active.db.getConnection().createStatement();
		 * 
		 * ResultSet meetingsInSet = sql
		 * .executeQuery("select * from meeting_date where set_id = " + id +
		 * " and date_of = (select MAX(date_of) from meeting_date)");
		 * 
		 * while (meetingsInSet.next()) {
		 * 
		 * System.out.println("Last meeting found.");
		 * 
		 * lastMeeting = new MeetingDescription(id, LocalDate.ofEpochDay(
		 * meetingsInSet.getLong(Meeting.Lookup.DATE.index))); }
		 * meetingsInSet.close(); sql.close();
		 * 
		 * return lastMeeting;
		 */
	}
}
