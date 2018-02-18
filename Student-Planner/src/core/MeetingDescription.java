package core;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class MeetingDescription {

	private String name;
	private LocalDateTime start;
	private LocalDateTime end;

	public MeetingDescription(String name, LocalDateTime start,
			LocalDateTime end) {

		this.name = name;
		this.start = start;
		this.end = end;
	}

	public long getTimestamp() {

		return start.toEpochSecond(ZoneOffset.UTC);
	}

	public String toString() {

		return this.name + " (" + start.toLocalTime() + " - "
				+ end.toLocalTime() + ")";
	}
}
