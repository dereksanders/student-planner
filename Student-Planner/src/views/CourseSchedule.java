package views;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.Observable;
import java.util.Observer;

import core.Main;
import core.Profile;
import core.Term;
import core.TermDescription;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import sqlite.SqliteWrapperException;

public class CourseSchedule implements Observer {

	private Observable profile;
	private TermDescription term;
	private Canvas canvas;
	private GraphicsContext gc;

	public CourseSchedule(TermDescription term)
			throws SqliteWrapperException, SQLException {

		this.profile = Main.active;
		this.profile.addObserver(this);

		this.term = term;
		this.canvas = new Canvas();
		this.gc = this.canvas.getGraphicsContext2D();
		drawSchedule(this.gc);
	}

	private void drawSchedule(GraphicsContext gc)
			throws SqliteWrapperException, SQLException {

		int paddingTop = 20;
		int paddingLeft = 10;

		// Determine the width of the schedule by determining if Saturday or
		// Sunday have any meetings during this term. Otherwise, we will show 5
		// days, Monday-Friday.
		int maxDay = Term.getLastDayOfWeek();

		if (maxDay < 5) {
			maxDay = 5;
		}

		// The width of the canvas will be the width of the time labels + 100px
		// for each day
		this.canvas.setWidth(75 + (100 * maxDay) + (2 * paddingLeft));

		// If no Meetings exist, then display 9 am - 5 pm.
		LocalTime scheduleStart = LocalTime.of(9, 0);
		LocalTime scheduleEnd = LocalTime.of(17, 0);

		if (Term.getNumMeetings() > 0) {

			LocalTime earliestStart = Term.getEarliestMeetingStart(term);
			LocalTime latestEnd = Term.getLatestMeetingEnd(term);

			LocalTime earliestStartRounded = roundToNearestHalfHour(
					earliestStart);
			LocalTime latestEndRounded = roundToNearestHalfHour(latestEnd);

			scheduleStart = earliestStartRounded;
			scheduleEnd = latestEndRounded;
		}

		// The height of the canvas will be the height of the day labels + 1px
		// for each minute displayed.
		this.canvas.setHeight(50 + getMinutesBetween(scheduleStart, scheduleEnd)
				+ (2 * paddingTop));

		for (int i = 1; i <= maxDay; i++) {

			if (i == 1) {
				this.gc.fillText(intToDay(i), paddingLeft + 50, paddingTop);
			} else {
				this.gc.fillText(intToDay(i),
						(i - 1) * 100 + (paddingLeft + 50), paddingTop);
			}
		}
	}

	private String intToDay(int dayAsInt) {

		String day = "";

		switch (dayAsInt) {

		case 1:
			day = "Monday";
			break;
		case 2:
			day = "Tuesday";
			break;
		case 3:
			day = "Wednesday";
			break;
		case 4:
			day = "Thursday";
			break;
		case 5:
			day = "Friday";
			break;
		case 6:
			day = "Saturday";
			break;
		case 7:
			day = "Sunday";
			break;
		}

		return day;
	}

	private LocalTime roundToNearestHalfHour(LocalTime time) {

		LocalTime rounded = LocalTime.of(time.getHour(), time.getMinute());

		if (rounded.getMinute() <= 30) {

			rounded = LocalTime.of(time.getHour(), 30);

		} else {

			if (time.getHour() < 23) {

				rounded = LocalTime.of(time.getHour() + 1, 0);
			} else {

				rounded = LocalTime.of(time.getHour(), 59);
			}
		}

		return rounded;
	}

	private int getMinutesBetween(LocalTime earliestStart,
			LocalTime latestEnd) {

		int start = earliestStart.toSecondOfDay() / 60;
		int end = latestEnd.toSecondOfDay() / 60;

		return end - start;
	}

	public Canvas getCanvas() {
		return this.canvas;
	}

	public void setTerm(TermDescription term)
			throws SqliteWrapperException, SQLException {

		this.term = term;
		drawSchedule(this.gc);
	}

	@Override
	public void update(Observable arg0, Object arg1) {

		if (arg0 instanceof Profile) {
			try {
				drawSchedule(this.gc);
			} catch (SqliteWrapperException | SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
