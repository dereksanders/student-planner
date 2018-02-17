package core;

import java.util.Observable;

import sqlite.SqliteWrapper;

public class Profile extends Observable {

	public String name;
	public SqliteWrapper sqlite;

	public Profile(String name, SqliteWrapper sqlite) {

		this.name = name;
		this.sqlite = sqlite;
	}
}
