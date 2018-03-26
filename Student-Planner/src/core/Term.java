package core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import javafx.scene.control.Alert.AlertType;
import sqlite.SqliteWrapperException;

/**
 * The Class Term.
 */
public class Term {

	/**
	 * The Enum Lookup.
	 */
	public enum Lookup {

		START_DATE(1), END_DATE(2), NAME(3), GRADE(4), GRADE_IS_AUTOMATIC(
				5), COLOR(6);

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
	 * Adds the term.
	 *
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date
	 * @param name
	 *            the name
	 * @param color
	 *            the color
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 */
	public static void addTerm(long startDate, long endDate, String name,
			String color) throws SqliteWrapperException {

		if (termIsValid(startDate, endDate, name, color)) {

			Main.active.db.execute(
					"insert into term(start_date, end_date, name, grade, grade_is_automatic, color) "
							+ "values(" + startDate + ", " + endDate + ", "
							+ "\'" + name + "\'" + ", 0, 1, " + "\'" + color
							+ "\'" + ");");
		}
	}

	/**
	 * Gets the terms.
	 *
	 * @return the terms
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 */
	public static ResultSet getTerms() throws SqliteWrapperException {

		ResultSet terms = Main.active.db
				.query("select * from term order by start_date");

		return terms;
	}

	/**
	 * Populate terms.
	 *
	 * @return the array list
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static ArrayList<TermDescription> populateTerms()
			throws SqliteWrapperException, SQLException {

		ResultSet terms = getTerms();

		ArrayList<TermDescription> termDescriptions = new ArrayList<>();

		while (terms.next()) {

			long startDay = terms.getLong(Term.Lookup.START_DATE.index);

			LocalDate start = LocalDate.ofEpochDay(startDay);

			TermDescription currentTerm = new TermDescription(
					terms.getString(Term.Lookup.NAME.index), start);

			termDescriptions.add(currentTerm);
		}

		return termDescriptions;
	}

	/**
	 * Gets the num terms.
	 *
	 * @return the num terms
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static int getNumTerms()
			throws SqliteWrapperException, SQLException {

		ResultSet countTermsQuery = Main.active.db
				.query("select count(*) from term");

		int countTerms = 0;

		// Note that at least one Term should always exist.
		if (countTermsQuery.next()) {
			countTerms = countTermsQuery.getInt(1);
		}

		return countTerms;
	}

	/**
	 * Validates the term.
	 *
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date
	 * @param name
	 *            the name
	 * @param color
	 *            the color
	 * @return true, if valid
	 */
	public static boolean termIsValid(long startDate, long endDate, String name,
			String color) {

		boolean termIsValid = false;
		boolean startIsBeforeEnd = false;
		boolean nameIsNonEmpty = false;

		if (startDate < endDate) {

			startIsBeforeEnd = true;
		}

		if (name.length() > 0) {

			nameIsNonEmpty = true;
		}

		if (startIsBeforeEnd && nameIsNonEmpty) {

			termIsValid = true;

		} else {

			termIsValid = false;

			ArrayList<String> errorMessages = new ArrayList<>();

			if (!startIsBeforeEnd) {

				errorMessages.add("Start date must be before end date");
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

			Main.showAlert(AlertType.ERROR, "Cannot add term", errorText);
		}

		return termIsValid;
	}

	/**
	 * Finds the term taking place during the specified date.
	 *
	 * @param date
	 *            the date
	 * @return the term description or null if no term is taking place
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static TermDescription findTerm(LocalDate date)
			throws SqliteWrapperException, SQLException {

		TermDescription found = null;

		long julianDate = date.toEpochDay();

		ResultSet findTermQuery = Main.active.db
				.query("select * from term where start_date <= " + julianDate
						+ " and end_date >= " + julianDate);

		if (findTermQuery.next()) {

			found = new TermDescription(
					findTermQuery.getString(Term.Lookup.NAME.index),
					LocalDate.ofEpochDay(findTermQuery
							.getLong(Term.Lookup.START_DATE.index)));
		}

		return found;
	}

	/**
	 * Gets the term in progress.
	 *
	 * @return the term in progress or null if no term is in progress
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static TermDescription getTermInProgress()
			throws SqliteWrapperException, SQLException {

		long currentDate = LocalDate.now().toEpochDay();

		TermDescription inProgress = null;

		ResultSet termInProgressQuery = Main.active.db
				.query("select * from term where start_date <= " + currentDate
						+ " and end_date >= " + currentDate);

		if (termInProgressQuery.next()) {

			inProgress = new TermDescription(
					termInProgressQuery.getString(Term.Lookup.NAME.index),
					LocalDate.ofEpochDay(termInProgressQuery
							.getLong(Term.Lookup.START_DATE.index)));
		}

		return inProgress;
	}

	/**
	 * Gets the latest day of the week that a meeting is taking place during a
	 * term.
	 * 
	 * FIXME: Needs to be term specific
	 *
	 * @return the latest day of week
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static int getLastDayOfWeek(TermDescription term)
			throws SqliteWrapperException, SQLException {

		// Days are numbered from 1 (Monday) to 7 (Sunday)
		int maxDay = 1;

		ResultSet meetingSets = Main.active.db
				.query("select * from meeting_set");

		while (meetingSets.next()) {

			int setID = meetingSets.getInt(MeetingSet.Lookup.ID.index);

			// Get a meeting from this set (all meetings from a set would occur
			// on the same weekday)
			//
			// We can assume that all MeetingSets will have at least on Meeting.
			ResultSet meeting = Main.active.db.query(
					"select * from meeting_date where set_id = " + setID);

			meeting.next();

			long julianDay = meeting.getLong(Meeting.Lookup.DATE.index);

			int meetingDayOfWeek = LocalDate.ofEpochDay(julianDay)
					.getDayOfWeek().getValue();

			if (meetingDayOfWeek > maxDay) {
				maxDay = meetingDayOfWeek;
			}
		}

		return maxDay;
	}

	/**
	 * Gets the earliest time that a meeting starts throughout a term.
	 *
	 * @param term
	 *            the term
	 * @return the earliest meeting start time
	 * @throws SQLException
	 *             the SQL exception
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 */
	public static LocalTime getEarliestMeetingStartTime(TermDescription term)
			throws SQLException, SqliteWrapperException {

		LocalTime earliest = LocalTime.of(23, 59);
		ResultSet meetingSets = Main.active.db
				.query("select * from meeting_set");

		while (meetingSets.next()) {

			long startSecond = meetingSets
					.getLong(MeetingSet.Lookup.START_TIME.index);

			LocalTime start = LocalTime.ofSecondOfDay(startSecond);

			if (start.isBefore(earliest)) {

				earliest = start;
			}
		}

		return earliest;
	}

	/**
	 * Gets the latest time that a meeting ends throughout a term.
	 *
	 * @param term
	 *            the term
	 * @return the latest meeting end time
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static LocalTime getLatestMeetingEndTime(TermDescription term)
			throws SqliteWrapperException, SQLException {

		LocalTime latest = LocalTime.of(0, 0);
		ResultSet meetingSets = Main.active.db
				.query("select * from meeting_set");

		while (meetingSets.next()) {

			long endSecond = meetingSets
					.getLong(MeetingSet.Lookup.END_TIME.index);

			LocalTime end = LocalTime.ofSecondOfDay(endSecond);

			if (end.isAfter(latest)) {

				latest = end;
			}
		}

		return latest;
	}

	/**
	 * Checks if the specified term contains any meetings.
	 *
	 * @param term
	 *            the term
	 * @return true, if successful
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static boolean hasMeetings(TermDescription term)
			throws SqliteWrapperException, SQLException {

		boolean hasMeetings = false;

		if (term != null) {

			ResultSet countMeetings = Main.active.db.query(
					"select count(*) from meeting_set where term_start_date = "
							+ term.getStartDay());

			if (countMeetings.next()) {
				hasMeetings = true;
			}
		}

		return hasMeetings;
	}

	/**
	 * Gets the color.
	 *
	 * @param term
	 *            the term
	 * @return the color
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static String getColor(TermDescription term)
			throws SqliteWrapperException, SQLException {

		String color = "";

		ResultSet getTerm = Main.active.db.query(
				"select * from term where start_date = " + term.getStartDay());

		if (getTerm.next()) {

			color = getTerm.getString(Term.Lookup.COLOR.index);
		}

		return color;
	}
}
