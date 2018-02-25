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
import javafx.scene.paint.Paint;
import sqlite.SqliteWrapperException;
import utility.DateTimeUtil;

public class CourseSchedule implements Observer {

	private Observable profile;
	private TermDescription term;

	// Drawing
	private Canvas canvas;
	private GraphicsContext gc;

	private int maxDay = DEFAULT_MAX_DAY;
	private LocalTime scheduleStart = DEFAULT_SCHEDULE_START;
	private LocalTime scheduleEnd = DEFAULT_SCHEDULE_END;
	private int dayHeight = DateTimeUtil.getMinutesBetween(scheduleStart,
			scheduleEnd);

	private static final int DEFAULT_MAX_DAY = 7;
	private static final LocalTime DEFAULT_SCHEDULE_START = LocalTime.of(9, 0);
	private static final LocalTime DEFAULT_SCHEDULE_END = LocalTime.of(17, 0);

	private static final int DAY_WIDTH = 100;
	private static final int TIME_LABEL_WIDTH = 75;

	private static final int DAY_LABEL_HEIGHT = 20;

	private static final int PADDING_LEFT = 20;
	private static final int PADDING_RIGHT = 20;
	private static final int PADDING_BOTTOM = 20;
	private static final int PADDING_TOP = 20;

	private static final String BORDER_COLOR = "#cccccc";
	private static final String EMPTY_DAY_COLOR = "#eeeeee";

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

		calcCanvasSize();

		drawDayLabels();

		drawDays();

		drawTimeLabels();
	}

	private void drawDayLabels() {

		for (int i = 1; i <= maxDay; i++) {

			if (i == 1) {
				this.gc.fillText(DateTimeUtil.intToDay(i), PADDING_LEFT + 25,
						PADDING_TOP);
			} else {
				this.gc.fillText(DateTimeUtil.intToDay(i),
						(i - 1) * 100 + (PADDING_LEFT + 25), PADDING_TOP);
			}
		}
	}

	private void drawDays() {

		for (int i = 1; i <= maxDay; i++) {

			if (i == 1) {

				setBorderFill();

				this.gc.fillRect(PADDING_LEFT, PADDING_TOP + DAY_LABEL_HEIGHT,
						DAY_WIDTH, this.dayHeight);

				setEmptyDayFill();

				this.gc.fillRect(PADDING_LEFT + 1,
						PADDING_TOP + DAY_LABEL_HEIGHT + 1, DAY_WIDTH - 2,
						this.dayHeight - 2);

			} else {

				setBorderFill();

				this.gc.fillRect(PADDING_LEFT + (DAY_WIDTH * (i - 1)),
						PADDING_TOP + DAY_LABEL_HEIGHT, DAY_WIDTH,
						this.dayHeight);

				setEmptyDayFill();

				this.gc.fillRect(PADDING_LEFT + (DAY_WIDTH * (i - 1)),
						PADDING_TOP + DAY_LABEL_HEIGHT + 1, DAY_WIDTH - 1,
						this.dayHeight - 2);
			}
		}
	}

	private void setBorderFill() {

		this.gc.setFill(Paint.valueOf(BORDER_COLOR));
	}

	private void setEmptyDayFill() {

		this.gc.setFill(Paint.valueOf(EMPTY_DAY_COLOR));
	}

	private void drawTimeLabels() {

	}

	private void calcCanvasSize() throws SqliteWrapperException, SQLException {

		calcCanvasWidth();
		calcCanvasHeight();
	}

	private void calcCanvasWidth() throws SqliteWrapperException, SQLException {

		// Determine the width of the schedule by determining if Saturday or
		// Sunday have any meetings during this term. Otherwise, we will show 5
		// days, Monday-Friday, the default value of maxDay.
		int lastDayOfWeek = Term.getLastDayOfWeek();

		if (lastDayOfWeek <= DEFAULT_MAX_DAY) {
			this.maxDay = DEFAULT_MAX_DAY;
		} else {
			this.maxDay = lastDayOfWeek;
		}

		// The width of the canvas will be the width of the time labels + 100px
		// for each day
		this.canvas.setWidth(PADDING_LEFT + TIME_LABEL_WIDTH
				+ (DAY_WIDTH * this.maxDay) + PADDING_RIGHT);
	}

	private void calcCanvasHeight()
			throws SqliteWrapperException, SQLException {

		// If no Meetings exist, then display 9 am - 5 pm.
		this.scheduleStart = DEFAULT_SCHEDULE_START;
		this.scheduleEnd = DEFAULT_SCHEDULE_END;

		if (Term.getNumMeetings() > 0) {

			LocalTime earliestStart = Term.getEarliestMeetingStart(term);
			LocalTime latestEnd = Term.getLatestMeetingEnd(term);

			LocalTime earliestStartRounded = DateTimeUtil
					.roundToNextHalfHour(earliestStart);
			LocalTime latestEndRounded = DateTimeUtil
					.roundToNextHalfHour(latestEnd);

			this.scheduleStart = earliestStartRounded;
			this.scheduleEnd = latestEndRounded;
		}

		this.dayHeight = DateTimeUtil.getMinutesBetween(this.scheduleStart,
				this.scheduleEnd);

		// The height of the canvas will be the height of the day labels + 1px
		// for each minute displayed.
		this.canvas.setHeight(PADDING_TOP + DAY_LABEL_HEIGHT + this.dayHeight
				+ PADDING_BOTTOM);
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
