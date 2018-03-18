package core;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Observable;

import sqlite.SqliteWrapper;
import sqlite.SqliteWrapperException;

public class Profile extends Observable {

	public String name;

	// Database access
	public SqliteWrapper db;

	private TermDescription termInProgress;
	private TermDescription selectedTerm;
	private LocalDate selectedDate;

	public Profile(String name, SqliteWrapper db) {

		this.name = name;
		this.db = db;
	}

	public void setSelectedDate(LocalDate selected)
			throws SqliteWrapperException, SQLException {

		this.selectedDate = selected;
		this.selectedTerm = Term.findTerm(selected);
		update();
	}

	public void setTermInProgress(TermDescription termInProgress)
			throws SqliteWrapperException, SQLException {

		this.termInProgress = termInProgress;
		update();
	}

	public TermDescription getTermInProgress() {

		return this.termInProgress;
	}

	public TermDescription getSelectedTerm() {

		return this.selectedTerm;
	}

	public LocalDate getSelectedDate() {

		return this.selectedDate;
	}

	public void update() throws SqliteWrapperException, SQLException {

		setTermInProgress(Term.getTermInProgress());

		setChanged();
		notifyObservers();
	}
}
