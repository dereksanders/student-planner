package core;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class CourseMeetingDescription extends MeetingDescription {

	private String name;
	private LocalDateTime start;
	private LocalDateTime end;
	private CourseDescription course;

	public CourseMeetingDescription(String name, LocalDateTime start,
			LocalDateTime end) {

		super(name, start, end);
	}

	public long getTimestamp() {

		return this.start.toEpochSecond(ZoneOffset.UTC);
	}

	public String toString() {

		return this.name + " (" + start.toLocalTime() + " - "
				+ end.toLocalTime() + ")";
	}
}
