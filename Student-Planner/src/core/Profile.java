package core;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Observable;

import sqlite.SqliteWrapper;
import sqlite.SqliteWrapperException;

public class Profile extends Observable {

	public String name;
	public SqliteWrapper db;

	public TermDescription termInProgress;

	// Date selected in the CourseSchedule tab.
	public LocalDate selectedDate;

	public Profile(String name, SqliteWrapper db) {

		this.name = name;
		this.db = db;
	}

	public void setSelectedDate(LocalDate selected) {

		this.selectedDate = selected;
	}

	public void update() throws SqliteWrapperException, SQLException {

		termInProgress = Term.getTermInProgress();

		setChanged();
		notifyObservers();
	}
}
