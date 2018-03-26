package core;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Observable;

import sqlite.SqliteWrapper;
import sqlite.SqliteWrapperException;

/**
 * The Class Profile.
 */
public class Profile extends Observable {

	public String name;

	// Database access
	public SqliteWrapper db;

	private TermDescription termInProgress;
	private TermDescription selectedTerm;
	private LocalDate selectedDate;

	/**
	 * Instantiates a new profile.
	 *
	 * @param name
	 *            the name
	 * @param db
	 *            the db
	 */
	public Profile(String name, SqliteWrapper db) {

		this.name = name;
		this.db = db;
	}

	/**
	 * Sets the selected date.
	 *
	 * @param selected
	 *            the new selected date
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void setSelectedDate(LocalDate selected)
			throws SqliteWrapperException, SQLException {

		this.selectedDate = selected;
		this.selectedTerm = Term.findTerm(selected);
		update();
	}

	/**
	 * Sets the term in progress.
	 *
	 * @param termInProgress
	 *            the new term in progress
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void setTermInProgress(TermDescription termInProgress)
			throws SqliteWrapperException, SQLException {

		this.termInProgress = termInProgress;
	}

	/**
	 * Gets the term in progress.
	 *
	 * @return the term in progress
	 */
	public TermDescription getTermInProgress() {

		return this.termInProgress;
	}

	/**
	 * Gets the selected term.
	 *
	 * @return the selected term
	 */
	public TermDescription getSelectedTerm() {

		return this.selectedTerm;
	}

	/**
	 * Gets the selected date.
	 *
	 * @return the selected date
	 */
	public LocalDate getSelectedDate() {

		return this.selectedDate;
	}

	/**
	 * Update.
	 *
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void update() throws SqliteWrapperException, SQLException {

		setTermInProgress(Term.getTermInProgress());

		System.out.println("Selected date = " + this.selectedDate);

		setChanged();
		notifyObservers();
	}
}
