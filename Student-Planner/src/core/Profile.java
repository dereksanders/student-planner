package core;

import java.time.LocalDate;
import java.util.Observable;

import sqlite.SqliteWrapper;

public class Profile extends Observable {

	public String name;
	public SqliteWrapper db;

	// Date selected in the CourseSchedule tab.
	public LocalDate selectedDate;

	public Profile(String name, SqliteWrapper db) {

		this.name = name;
		this.db = db;
	}

	public void setSelectedDate(LocalDate selected) {

		this.selectedDate = selected;
	}

	public void update() {

		setChanged();
		notifyObservers();
	}
}
