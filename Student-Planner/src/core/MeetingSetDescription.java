package core;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

import sqlite.SqliteWrapperException;

/**
 * The Class MeetingSetDescription.
 */
public class MeetingSetDescription {

	public int id;
	private TermDescription term;

	public CourseDescription course;
	public boolean isCourseMeeting;
	public String name;
	public String type;
	public String location;
	public String repeat;
	public LocalTime start;
	public LocalTime end;

	/**
	 * Instantiates a new meeting set description.
	 *
	 * @param id
	 *            the id
	 * @param termStart
	 *            the term start
	 * @param courseStartTermStartDate
	 *            the course start term start date
	 * @param courseEndTermStartDate
	 *            the course end term start date
	 * @param courseDept
	 *            the course dept
	 * @param courseCode
	 *            the course code
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param isCourseMeeting
	 *            the is course meeting
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	public MeetingSetDescription(int id, LocalDate termStart,
			LocalDate courseStartTermStartDate,
			LocalDate courseEndTermStartDate, String courseDept, int courseCode,
			String name, String type, String location, String repeat,
			LocalTime start, LocalTime end, boolean isCourseMeeting)
			throws SqliteWrapperException, SQLException {

		this.id = id;
		this.start = start;
		this.end = end;

		this.term = Term.findTerm(termStart);

		this.isCourseMeeting = isCourseMeeting;
		if (this.isCourseMeeting) {
			this.course = new CourseDescription(courseDept, courseCode,
					Term.findTerm(courseStartTermStartDate),
					Term.findTerm(courseEndTermStartDate));
		}

		this.name = name;
		this.type = type;
		this.location = location;
		this.repeat = repeat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "" + this.id + this.term + this.course + this.start + this.end;
	}
}
