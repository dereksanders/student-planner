package core;

import java.util.Observable;

import sqlite.SqliteWrapper;

public class Profile extends Observable {

	public String name;
	public SqliteWrapper db;

	public Profile(String name, SqliteWrapper db) {

		this.name = name;
		this.db = db;
	}
}
