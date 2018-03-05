package core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import sqlite.SqliteWrapperException;

public class MeetingSet {

	public enum Lookup {

		ID(1), TERM_START_DATE(2), START_TIME(3), END_TIME(
				4), COURSE_START_TERM_START_DATE(5), COURSE_END_TERM_START_DATE(
						6), COURSE_DEPT(7), COURSE_CODE(
								8), NAME(9), TYPE(10), LOCATION(11);

		public int index;

		private Lookup(int index) {
			this.index = index;
		}
	}

	public static void addMeetingSet(int id, TermDescription term,
			CourseDescription course, LocalTime start, LocalTime end,
			ArrayList<LocalDate> dates) throws SqliteWrapperException {

		Main.active.db.execute(
				"insert into meeting_set(id, term_start_date, start_time, end_time, course_start_term_start_date, course_end_term_start_date, course_dept_id, course_code, name, type, location) "
						+ "values(" + id + ", " + term.start.toEpochDay() + ", "
						+ start.toSecondOfDay() + ", " + end.toSecondOfDay()
						+ ", " + course.startTerm.getStartDay() + ", "
						+ course.endTerm.getStartDay() + ", \'" + course.dept
						+ "\', " + course.code + ", \'" + "nullname" + "\', \'"
						+ "nullloc" + "\');");

		for (LocalDate d : dates) {

			Main.active.db.execute("insert into meeting_date(set_id, date_of) "
					+ "values(" + id + ", " + d.toEpochDay() + ");");
		}
	}

	public static MeetingSetDescription findMeetingSet(int setID)
			throws SQLException, SqliteWrapperException {

		MeetingSetDescription found = null;

		ResultSet findMeetingSet = Main.active.db
				.query("select * from meeting_set where id = " + setID);

		if (findMeetingSet.next()) {

			LocalDate termStart = LocalDate.ofEpochDay(findMeetingSet
					.getLong(MeetingSet.Lookup.TERM_START_DATE.index));
			TermDescription term = Term.findTerm(termStart);
			CourseDescription course = new CourseDescription(
					findMeetingSet
							.getString(MeetingSet.Lookup.COURSE_DEPT.index),
					findMeetingSet.getInt(MeetingSet.Lookup.COURSE_CODE.index),
					Term.findTerm(LocalDate.ofEpochDay(findMeetingSet.getLong(
							MeetingSet.Lookup.COURSE_START_TERM_START_DATE.index))),
					Term.findTerm(LocalDate.ofEpochDay(findMeetingSet.getLong(
							MeetingSet.Lookup.COURSE_END_TERM_START_DATE.index))));

			long secondStart = findMeetingSet
					.getLong(MeetingSet.Lookup.START_TIME.index);
			long secondEnd = findMeetingSet
					.getLong(MeetingSet.Lookup.END_TIME.index);

			LocalTime startTime = LocalTime.ofSecondOfDay(secondStart);
			LocalTime endTime = LocalTime.ofSecondOfDay(secondEnd);

			found = new MeetingSetDescription(
					findMeetingSet.getInt(MeetingSet.Lookup.ID.index), term,
					course, startTime, endTime);
		}

		return found;
	}
}
