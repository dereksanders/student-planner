package core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javafx.scene.control.Alert.AlertType;
import sqlite.SqliteWrapperException;

public class Course {

	public enum Lookup {

		START_TERM_START_DATE(1), END_TERM_START_DATE(2), DEPT_ID(3), CODE(
				4), NAME(5), GRADE(6), GRADE_IS_AUTOMATIC(7), COLOR(8);

		public int index;

		private Lookup(int index) {
			this.index = index;
		}
	}

	public static void addCourse(long startTermStartDate, long endTermStartDate,
			String deptID, int code, String name, String color)
			throws SqliteWrapperException {

		if (courseIsValid(startTermStartDate, endTermStartDate, deptID, code,
				name, color)) {

			Main.active.db.execute(
					"insert into course(start_term_start_date, end_term_start_date, "
							+ "dept_id, code, name, grade, grade_is_automatic, color) "
							+ "values(" + startTermStartDate + ", "
							+ endTermStartDate + ", " + "\'" + deptID + "\'"
							+ ", " + code + ", " + "\'" + name + "\'"
							+ ", 0, 1, " + "\'" + color + "\'" + ");");
		}
	}

	public static ResultSet getCourses() throws SqliteWrapperException {

		ResultSet courses = Main.active.db.query("select * from course");

		return courses;
	}

	public static CourseDescription getCourse(TermDescription term)
			throws SqliteWrapperException, SQLException {

		CourseDescription found = null;

		ResultSet courses = Main.active.db
				.query("select * from course where start_term_start_date = "
						+ term.getStartDay());

		if (courses.next()) {
			found = new CourseDescription(
					courses.getString(Course.Lookup.DEPT_ID.index),
					courses.getInt(Course.Lookup.CODE.index), term, term);
		}

		return found;
	}

	public static int getNumCourses()
			throws SqliteWrapperException, SQLException {

		ResultSet countCoursesQuery = Main.active.db
				.query("select count(*) from course");

		int countCourses = countCoursesQuery.getInt(1);

		return countCourses;
	}

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

	public static String getColor(CourseDescription course)
			throws SqliteWrapperException, SQLException {

		String color = "";

		ResultSet getCourse = Main.active.db
				.query("select * from course where start_term_start_date = "
						+ course.startTerm.start.toEpochDay()
						+ " and end_term_start_date = "
						+ course.endTerm.start.toEpochDay() + " and dept_id = "
						+ course.dept + " and code = " + course.code);

		if (getCourse.next()) {

			color = getCourse.getString(Course.Lookup.COLOR.index);
		}

		return color;
	}
}
