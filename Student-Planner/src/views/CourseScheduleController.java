package views;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import core.Course;
import core.Main;
import core.Meeting;
import core.MeetingBlock;
import core.MeetingDescription;
import core.MeetingSet;
import core.Profile;
import core.Term;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import sqlite.SqliteWrapperException;
import utility.ColorUtil;
import utility.DateTimeUtil;

public class CourseScheduleController implements Observer {

	private Profile observable;

	// Drawing
	private Canvas canvas;
	private GraphicsContext gc;

	private int maxDay = DEFAULT_MAX_DAY;
	private LocalTime scheduleStart = DEFAULT_SCHEDULE_START;
	private LocalTime scheduleEnd = DEFAULT_SCHEDULE_END;
	private int dayHeight = DateTimeUtil.getMinutesBetween(scheduleStart,
			scheduleEnd);

	private ArrayList<MeetingBlock> meetingBlocks;

	private static final int DEFAULT_MAX_DAY = 5;
	private static final LocalTime DEFAULT_SCHEDULE_START = LocalTime.of(9, 0);
	private static final LocalTime DEFAULT_SCHEDULE_END = LocalTime.of(17, 0);

	private static final int DAY_WIDTH = 100;
	private static final int TIME_LABEL_WIDTH = 45;
	private static final int TIME_LABEL_Y_OFFSET = 6;
	private static final int DAY_LABEL_HEIGHT = 8;
	private static final double PIXELS_PER_MINUTE = 1.2;

	private static final int PADDING_LEFT = 10;
	private static final int PADDING_RIGHT = 0;
	private static final int PADDING_BOTTOM = 20;
	private static final int PADDING_TOP = 20;

	private static final String BORDER_COLOR = "#cccccc";
	private static final String EMPTY_DAY_COLOR = "#eeeeee";

	@FXML
	public void initialize() throws SqliteWrapperException, SQLException {

		this.observable = Main.active;
		this.observable.addObserver(this);

		this.canvas = new Canvas();
		this.gc = this.canvas.getGraphicsContext2D();
		this.meetingBlocks = new ArrayList<>();
		drawSchedule();
	}

	private void drawSchedule() throws SqliteWrapperException, SQLException {

		this.gc.clearRect(0, 0, this.canvas.getWidth(),
				this.canvas.getHeight());

		calcCanvasSize();

		drawDayLabels();

		drawDays();

		drawTimeLabels();

		drawMeetings();

		this.canvas.addEventHandler(MouseEvent.MOUSE_CLICKED,
				new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {

						handleMouseClickOnCanvas(event.getSceneX(),
								event.getSceneY());
					}

					private void handleMouseClickOnCanvas(double sceneX,
							double sceneY) {

					}
				});
	}

	private void drawMeetings() throws SqliteWrapperException, SQLException {

		this.meetingBlocks.clear();

		ArrayList<MeetingDescription> meetingsThisWeek = Meeting
				.getMeetingsWeekOf(Main.active.getSelectedDate());

		for (MeetingDescription meeting : meetingsThisWeek) {

			this.meetingBlocks.add(createMeetingBlockFrom(meeting));
		}

		for (MeetingBlock mb : this.meetingBlocks) {

			drawMeeting(mb);
		}
	}

	private MeetingBlock createMeetingBlockFrom(MeetingDescription meeting)
			throws SqliteWrapperException, SQLException {

		int dayOfWeek = meeting.date.getDayOfWeek().getValue();
		System.out.println(meeting);
		System.out.println(meeting.set);
		LocalTime start = meeting.set.start;

		double xOffset = getDayXPosition(dayOfWeek);
		double yOffset = (DateTimeUtil.getMinutesBetween(scheduleStart, start)
				* PIXELS_PER_MINUTE) + PADDING_TOP + DAY_LABEL_HEIGHT;
		double height = DateTimeUtil.getMinutesBetween(start, meeting.set.end)
				* PIXELS_PER_MINUTE;

		System.out.println("X-offset: " + xOffset + ", Y-offset: " + yOffset);

		Rectangle rect = new Rectangle(xOffset, yOffset, DAY_WIDTH, height);
		rect.setFill(Paint.valueOf(Course.getColor(meeting.set.course)));

		return new MeetingBlock(meeting, rect);
	}

	private void drawMeeting(MeetingBlock mb)
			throws SqliteWrapperException, SQLException {

		setFill(mb.rect.getFill());

		this.gc.fillRect(mb.rect.getX(), mb.rect.getY(), mb.rect.getWidth(),
				mb.rect.getHeight());

		Color meetingColor = Color.web(MeetingSet.getColor(mb.meeting.setID));

		if (ColorUtil.isDark(meetingColor)) {
			setFill(Main.TEXT_LIGHT_COLOR);
		} else {
			setFill(Main.TEXT_DARK_COLOR);
		}

		String meetingText = "";

		if (mb.meeting.set.isCourseMeeting) {

			meetingText = mb.meeting.set.course.toString() + "\n"
					+ mb.meeting.set.type;

		} else {

			meetingText = mb.meeting.set.name + "\n" + mb.meeting.set.type;
		}

		this.gc.fillText(meetingText, PADDING_LEFT + mb.rect.getX(),
				mb.rect.getY() + 20, mb.rect.getWidth());
	}

	private void drawDayLabels() {

		setFill(Main.TEXT_DARK_COLOR);

		for (int i = 1; i <= maxDay; i++) {

			this.gc.fillText(DateTimeUtil.intToDay(i),
					getDayXPosition(i) + getDayLabelOffset(i), PADDING_TOP);
		}
	}

	private int getDayLabelOffset(int day) {

		int offset = 0;

		final int mondayOffset = 20;
		final int tuesdayOffset = 20;
		final int wednesdayOffset = 12;
		final int thursdayOffset = 20;
		final int fridayOffset = 30;
		final int saturdayOffset = 20;
		final int sundayOffset = 26;

		switch (day) {

		case 1:
			offset = mondayOffset;
			break;
		case 2:
			offset = tuesdayOffset;
			break;
		case 3:
			offset = wednesdayOffset;
			break;
		case 4:
			offset = thursdayOffset;
			break;
		case 5:
			offset = fridayOffset;
			break;
		case 6:
			offset = saturdayOffset;
			break;
		case 7:
			offset = sundayOffset;
			break;
		}

		return offset;
	}

	private int getDayXPosition(int day) {

		return ((day - 1) * DAY_WIDTH) + PADDING_LEFT + TIME_LABEL_WIDTH;
	}

	private void drawDays() {

		for (int i = 1; i <= maxDay; i++) {

			for (int j = 0; j < DateTimeUtil.getMinutesBetween(
					this.scheduleStart, this.scheduleEnd); j += 30) {

				double slotYPosition = PADDING_TOP + DAY_LABEL_HEIGHT
						+ (j * PIXELS_PER_MINUTE);

				setFill(BORDER_COLOR);

				this.gc.fillRect(getDayXPosition(i), slotYPosition, DAY_WIDTH,
						(30 * PIXELS_PER_MINUTE));

				setFill(EMPTY_DAY_COLOR);

				if (i == 1 && j == 0) {

					// First day, first slot
					this.gc.fillRect(getDayXPosition(i) + 1, slotYPosition + 1,
							DAY_WIDTH - 2, (30 * PIXELS_PER_MINUTE) - 2);

				} else if (i == 1) {

					// First day, not the first slot
					this.gc.fillRect(getDayXPosition(i) + 1, slotYPosition,
							DAY_WIDTH - 2, (30 * PIXELS_PER_MINUTE) - 1);

				} else if (i != 1 && j == 0) {

					// Not the first day, first slot
					this.gc.fillRect(getDayXPosition(i), slotYPosition + 1,
							DAY_WIDTH - 1, (30 * PIXELS_PER_MINUTE) - 2);

				} else if (i != 1) {

					// Not the first day, not the first slot
					this.gc.fillRect(getDayXPosition(i), slotYPosition,
							DAY_WIDTH - 1, (30 * PIXELS_PER_MINUTE) - 1);
				}
			}
		}
	}

	private void setFill(String color) {

		this.gc.setFill(Paint.valueOf(color));
	}

	private void setFill(Paint paint) {

		this.gc.setFill(paint);
	}

	private void drawTimeLabels() {

		setFill(Main.TEXT_DARK_COLOR);

		for (int i = 0; i < this.dayHeight; i += 30) {

			if (i == 0) {
				this.gc.fillText(this.scheduleStart.toString(), PADDING_LEFT,
						PADDING_TOP + DAY_LABEL_HEIGHT + TIME_LABEL_Y_OFFSET);
			} else {
				this.gc.fillText(this.scheduleStart.plusMinutes(i).toString(),
						PADDING_LEFT,
						PADDING_TOP + DAY_LABEL_HEIGHT + TIME_LABEL_Y_OFFSET
								+ (i * PIXELS_PER_MINUTE));
			}
		}
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

		if (Term.meetingsExistIn(this.observable.getSelectedTerm())) {

			LocalTime earliestStart = Term
					.getEarliestMeetingStart(this.observable.getSelectedTerm());
			LocalTime latestEnd = Term
					.getLatestMeetingEnd(this.observable.getSelectedTerm());

			LocalTime earliestStartRounded = DateTimeUtil
					.roundToPrevHalfHour(earliestStart);

			LocalTime latestEndRounded = DateTimeUtil
					.roundToNextHalfHour(latestEnd);

			this.scheduleStart = earliestStartRounded;
			this.scheduleEnd = latestEndRounded;
		}

		this.dayHeight = (int) (DateTimeUtil.getMinutesBetween(
				this.scheduleStart, this.scheduleEnd) * PIXELS_PER_MINUTE);

		// The height of the canvas will be the height of the day labels + 1px
		// for each minute displayed.
		this.canvas.setHeight(PADDING_TOP + DAY_LABEL_HEIGHT + this.dayHeight
				+ PADDING_BOTTOM);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof Profile) {
			try {
				drawSchedule();
			} catch (SqliteWrapperException | SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
