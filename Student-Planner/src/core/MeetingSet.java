package core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import sqlite.SqliteWrapperException;

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
								9), MEETING_TYPE(10), LOCATION(
										11), IS_COURSE_MEETING(12), COLOR(13);

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

	public static final String[] REPEAT_OPTIONS = { "Weekly", "Bi-Weekly",
			"Monthly", "Never" };

	/**
	 * Adds the meeting set.
	 *
	 * @param id
	 *            the id
	 * @param term
	 *            the term
	 * @param course
	 *            the course
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param dates
	 *            the dates
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static void addMeetingSet(int id, TermDescription term,
			CourseDescription course, LocalTime start, LocalTime end,
			ArrayList<LocalDate> dates)
			throws SqliteWrapperException, SQLException {

		if (course != null) {

			Main.active.db.execute(
					"insert into meeting_set(id, term_start_date, start_time, end_time, "
							+ "course_start_term_start_date, course_end_term_start_date, "
							+ "course_dept_id, course_code, name, meeting_type, location, "
							+ "is_course_meeting_set, color) " + "values(" + id
							+ "," + term.getStartDay() + ","
							+ start.toSecondOfDay() + "," + end.toSecondOfDay()
							+ "," + course.startTerm.getStartDay() + ","
							+ course.endTerm.getStartDay() + ",\'" + course.dept
							+ "\'," + course.code + "," + "\'nullname\'" + ","
							+ "\'nulltype\'" + "," + "\'nulloc\'" + ",1," + "\'"
							+ Course.getColor(course) + "\'" + ");");

		} else {

			// FIXME: Add handling for non-Course MeetingSets
		}

		for (LocalDate d : dates) {

			Main.active.db.execute("insert into meeting_date(set_id, date_of) "
					+ "values(" + id + ", " + d.toEpochDay() + ");");
		}
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

		ResultSet findMeetingSet = Main.active.db
				.query("select * from meeting_set where id = " + setID);

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
					startTime, endTime, findMeetingSet.getBoolean(
							MeetingSet.Lookup.IS_COURSE_MEETING.index));
		}

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

		ResultSet getMeetingSet = Main.active.db
				.query("select * from meeting_set where id = " + id);

		if (getMeetingSet.next()) {

			color = getMeetingSet.getString(MeetingSet.Lookup.COLOR.index);
		}

		return color;
	}
}
