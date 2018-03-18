package core;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

import sqlite.SqliteWrapperException;

public class MeetingSetDescription {

	private int id;
	private TermDescription term;
	public CourseDescription course;
	public boolean isCourseMeeting;
	public String name;
	public String type;
	public LocalTime start;
	public LocalTime end;

	public MeetingSetDescription(int id, LocalDate termStart,
			LocalDate courseStartTermStartDate,
			LocalDate courseEndTermStartDate, String courseDept, int courseCode,
			String name, String type, LocalTime start, LocalTime end,
			boolean isCourseMeeting)
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
	}

	@Override
	public String toString() {

		return "" + this.id + this.term + this.course + this.start + this.end;
	}
}
