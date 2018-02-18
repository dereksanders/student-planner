package views;

import java.time.LocalTime;
import java.util.Observable;
import java.util.Observer;

import core.Main;
import core.Profile;
import core.Term;
import core.TermDescription;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class CourseSchedule implements Observer {

	private Observable profile;
	private TermDescription term;
	private Canvas canvas;
	private GraphicsContext gc;

	public CourseSchedule(TermDescription term) {

		this.profile = Main.active;
		this.profile.addObserver(this);

		this.term = term;
		this.canvas = new Canvas();
		this.gc = this.canvas.getGraphicsContext2D();
		drawSchedule(this.gc);
	}

	private void drawSchedule(GraphicsContext gc) {

		int paddingTop = 10;
		int paddingLeft = 10;

		// Determine the width of the schedule by determining if Saturday or
		// Sunday have any meetings during this term. Otherwise, we will show 5
		// days, Monday-Friday.
		int days = Term.getLastDayOfWeek();

		if (days < 5) {
			days = 5;
		}

		// The width of the canvas will be the width of the time labels + 100px
		// for each day
		this.canvas.setWidth(75 + (100 * days) + (2 * paddingLeft));

		LocalTime earliestStart = Term.getEarliestMeetingStart(term);
		LocalTime latestEnd = Term.getLatestEnd(term);

		// The height of the canvas will be the height of the day labels + 1px
		// for each minute displayed.
		this.canvas.setHeight(50 + getMinutesBetween(earliestStart, latestEnd)
				+ (2 * paddingTop));

	}

	private int getMinutesBetween(LocalTime earliestStart,
			LocalTime latestEnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Canvas getCanvas() {

		return this.canvas;
	}

	public void setTerm(TermDescription term) {

		this.term = term;
		drawSchedule(this.gc);
	}

	@Override
	public void update(Observable arg0, Object arg1) {

		if (arg0 instanceof Profile) {
			drawSchedule(this.gc);
		}
	}
}
