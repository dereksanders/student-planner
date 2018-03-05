package core;

import java.time.LocalTime;

public class MeetingSetDescription {

	private int id;
	private TermDescription term;
	public CourseDescription course;
	public LocalTime start;
	public LocalTime end;

	public MeetingSetDescription(int id, TermDescription term,
			CourseDescription course, LocalTime start, LocalTime end) {

		this.id = id;
		this.term = term;
		this.course = course;
		this.start = start;
		this.end = end;
	}
}
