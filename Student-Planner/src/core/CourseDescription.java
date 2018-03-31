package core;

import java.sql.SQLException;
import java.time.LocalDate;

import sqlite.SqliteWrapperException;

/**
 * The Class CourseDescription.
 */
public class CourseDescription {

	public String dept;
	public int code;
	public TermDescription startTerm;
	public TermDescription endTerm;

	/**
	 * Instantiates a new course description.
	 *
	 * @param dept
	 *            the dept
	 * @param code
	 *            the code
	 * @param startTerm
	 *            the start term
	 * @param endTerm
	 *            the end term
	 */
	public CourseDescription(String dept, int code, TermDescription startTerm,
			TermDescription endTerm) {

		this.dept = dept;
		this.code = code;
		this.startTerm = startTerm;
		this.endTerm = endTerm;
	}

	public CourseDescription(String dept, int code, LocalDate startTermStartDay,
			LocalDate endTermStartDay)
			throws SqliteWrapperException, SQLException {

		this.dept = dept;
		this.code = code;
		this.startTerm = Term.findTerm(startTermStartDay);
		this.endTerm = Term.findTerm(endTermStartDay);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		String desc = "";

		desc += dept + " " + code;

		return desc;
	}
}
