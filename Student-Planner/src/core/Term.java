package core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

import javafx.scene.control.Alert.AlertType;
import sqlite.SqliteWrapperException;

public class Term {

	public enum Lookup {

		START_DATE(1), END_DATE(2), NAME(3), GRADE(4), GRADE_IS_AUTOMATIC(
				5), COLOR(6);

		public int index;

		private Lookup(int index) {
			this.index = index;
		}
	}

	public static void addTerm(long startDate, long endDate, String name,
			String color) throws SqliteWrapperException {

		if (termIsValid(startDate, endDate, name, color)) {

			Main.sqlite.execute(
					"insert into term(start_date, end_date, name, grade, grade_is_automatic, color) "
							+ "values(" + startDate + ", " + endDate + ", "
							+ "\'" + name + "\'" + ", 0, 1, " + "\'" + color
							+ "\'" + ");");
		}
	}

	public static ResultSet getTerms() throws SqliteWrapperException {

		ResultSet terms = Main.sqlite
				.query("select * from term order by start_date");

		return terms;
	}

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

	public static int getNumTerms()
			throws SqliteWrapperException, SQLException {

		ResultSet countTermsQuery = Main.sqlite
				.query("select count(*) from term");

		int countTerms = countTermsQuery.getInt(1);

		return countTerms;
	}

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

	public static TermDescription getTermInProgress()
			throws SqliteWrapperException, SQLException {

		long currentDate = LocalDate.now().toEpochDay();

		TermDescription inProgress = null;

		ResultSet termInProgressQuery = Main.sqlite
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

	public static int getLastDayOfWeek()
			throws SqliteWrapperException, SQLException {

		// Days are numbered from 1 (Monday) to 7 (Sunday)
		int maxDay = 1;

		ResultSet meetingSets = Main.sqlite.query("select * from meeting_set");

		while (meetingSets.next()) {

			int setID = meetingSets.getInt(MeetingSet.Lookup.ID.index);

			// Get a meeting from this set (all meetings from a set would occur
			// on the same weekday)
			//
			// We can assume that all MeetingSets will have at least on Meeting.
			ResultSet meeting = Main.sqlite.query(
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

	public static LocalTime getEarliestMeetingStart(TermDescription term)
			throws SQLException, SqliteWrapperException {

		LocalTime earliest = LocalTime.of(23, 59);
		ResultSet meetingSets = Main.sqlite.query("select * from meeting_set");

		while (meetingSets.next()) {

			long epochSecond = meetingSets
					.getLong(MeetingSet.Lookup.START_TIME.index);

			LocalTime start = LocalDateTime
					.ofEpochSecond(epochSecond, 0, ZoneOffset.UTC)
					.toLocalTime();

			if (start.isBefore(earliest)) {

				earliest = start;
			}
		}

		return earliest;
	}

	public static LocalTime getLatestMeetingEnd(TermDescription term)
			throws SqliteWrapperException, SQLException {

		LocalTime latest = LocalTime.of(0, 0);
		ResultSet meetingSets = Main.sqlite.query("select * from meeting_set");

		while (meetingSets.next()) {

			long epochSecond = meetingSets
					.getLong(MeetingSet.Lookup.END_TIME.index);

			LocalTime end = LocalDateTime
					.ofEpochSecond(epochSecond, 0, ZoneOffset.UTC)
					.toLocalTime();

			if (end.isAfter(latest)) {

				latest = end;
			}
		}

		return latest;
	}

	public static int getNumMeetings()
			throws SqliteWrapperException, SQLException {

		ResultSet countMeetings = Main.sqlite
				.query("select count(*) from meeting_date");

		int numMeetings = countMeetings.getInt(1);

		return numMeetings;
	}
}
