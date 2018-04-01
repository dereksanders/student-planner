package core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import javafx.scene.control.Alert.AlertType;
import sqlite.SqliteWrapperException;

/**
 * The Class Course.
 */
public class Course {

	/**
	 * The Enum Lookup.
	 */
	public enum Lookup {

		START_TERM_START_DATE(1), END_TERM_START_DATE(2), DEPT_ID(3), CODE(
				4), NAME(5), GRADE(6), GRADE_IS_AUTOMATIC(7), COLOR(8);

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

	/**
	 * Adds the course.
	 *
	 * @param startTermStartDate
	 *            the start term start date
	 * @param endTermStartDate
	 *            the end term start date
	 * @param deptID
	 *            the dept ID
	 * @param code
	 *            the code
	 * @param name
	 *            the name
	 * @param color
	 *            the color
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static void addCourse(long startTermStartDate, long endTermStartDate,
			String deptID, int code, String name, String color)
			throws SqliteWrapperException, SQLException {

		if (courseIsValid(startTermStartDate, endTermStartDate, deptID, code,
				name, color)) {

			Statement sql = Main.active.db.getConnection().createStatement();

			sql.execute(
					"insert into course(start_term_start_date, end_term_start_date, "
							+ "dept_id, code, name, grade, grade_is_automatic, color) "
							+ "values(" + startTermStartDate + ", "
							+ endTermStartDate + ", " + "\'" + deptID + "\'"
							+ ", " + code + ", " + "\'" + name + "\'"
							+ ", 0, 1, " + "\'" + color + "\'" + ");");
			sql.close();
		}
	}

	/**
	 * Gets all courses in a specified term.
	 *
	 * @return the courses
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 */
	public static ArrayList<CourseDescription> populateCourses(
			TermDescription term) throws SqliteWrapperException, SQLException {

		ArrayList<CourseDescription> courses = new ArrayList<>();

		Statement sql = Main.active.db.getConnection().createStatement();
		ResultSet findCourses = sql.executeQuery("select * from course");

		while (findCourses.next()) {

			courses.add(new CourseDescription(
					findCourses.getString(Course.Lookup.DEPT_ID.index),
					findCourses.getInt(Course.Lookup.CODE.index),
					LocalDate.ofEpochDay(findCourses.getLong(
							Course.Lookup.START_TERM_START_DATE.index)),
					LocalDate.ofEpochDay(findCourses.getLong(
							Course.Lookup.END_TERM_START_DATE.index))));
		}
		findCourses.close();
		sql.close();

		return courses;
	}

	/**
	 * Validates the course info.
	 *
	 * @param startTermStartDate
	 *            the start term start date
	 * @param endTermStartDate
	 *            the end term start date
	 * @param deptID
	 *            the dept ID
	 * @param code
	 *            the code
	 * @param name
	 *            the name
	 * @param color
	 *            the color
	 * @return true, if valid
	 */
	public static boolean courseIsValid(long startTermStartDate,
			long endTermStartDate, String deptID, int code, String name,
			String color) {

		boolean courseIsValid = false;
		boolean startTermBeforeOrEqualToEndTerm = false;
		boolean deptIDIsNonEmpty = false;
		boolean nameIsNonEmpty = false;

		if (startTermStartDate <= endTermStartDate) {

			startTermBeforeOrEqualToEndTerm = true;
		}

		if (deptID.length() > 0) {

			deptIDIsNonEmpty = true;
		}

		if (name.length() > 0) {

			nameIsNonEmpty = true;
		}

		if (startTermBeforeOrEqualToEndTerm && deptIDIsNonEmpty
				&& nameIsNonEmpty) {

			courseIsValid = true;

		} else {

			courseIsValid = false;

			ArrayList<String> errorMessages = new ArrayList<>();

			if (!startTermBeforeOrEqualToEndTerm) {

				errorMessages.add("Start term cannot be after end term");
			}

			if (!deptIDIsNonEmpty) {

				errorMessages.add("Department cannot be empty");
			}

			if (!nameIsNonEmpty) {

				errorMessages.add("Name cannot be empty");
			}

			String errorText = "";

			for (int i = 0; i < errorMessages.size(); i++) {

				errorText += (i + 1) + ". " + errorMessages.get(i);

				if (i < errorMessages.size() - 1) {

					errorText += "\n";
				}
			}

			Main.showAlert(AlertType.ERROR, "Cannot add course", errorText);
		}

		return courseIsValid;
	}

	/**
	 * Gets the color.
	 *
	 * @param course
	 *            the course
	 * @return the color
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static String getColor(CourseDescription course)
			throws SqliteWrapperException, SQLException {

		String color = "";

		Statement sql = Main.active.db.getConnection().createStatement();

		ResultSet getCourse = sql.executeQuery(
				"select * from course where start_term_start_date = "
						+ course.startTerm.getStartDay()
						+ " and end_term_start_date = "
						+ course.endTerm.getStartDay() + " and dept_id = "
						+ "\'" + course.dept + "\'" + " and code = "
						+ course.code);

		if (getCourse.next()) {

			color = getCourse.getString(Course.Lookup.COLOR.index);
		}

		getCourse.close();
		sql.close();

		return color;
	}
}
