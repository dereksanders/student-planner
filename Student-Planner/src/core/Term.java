package core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import javafx.scene.control.Alert;
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

		ResultSet terms = Main.sqlite.query("select * from term");

		return terms;
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

			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Cannot add term");

			String errorText = "";

			for (int i = 0; i < errorMessages.size(); i++) {

				errorText += i + ". " + errorMessages.get(i);

				if (i < errorMessages.size() - 1) {

					errorText += "\n";
				}
			}

			alert.setContentText(errorText);
			alert.showAndWait();
		}

		return termIsValid;
	}

	public TermDescription getTermInProgress()
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
}
