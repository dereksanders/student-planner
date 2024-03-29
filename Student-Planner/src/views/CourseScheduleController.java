package views;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import core.Main;
import core.Meeting;
import core.MeetingBlock;
import core.MeetingDescription;
import core.MeetingSet;
import core.Profile;
import core.Term;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import sqlite.SqliteWrapperException;
import utility.ColorUtil;
import utility.DateTimeUtil;

/**
 * The Class CourseScheduleController.
 */
public class CourseScheduleController implements Observer {

	private Profile observable;

	@FXML
	private DatePicker selectWeek;
	@FXML
	private CheckBox selectCurrentWeek;
	@FXML
	private Canvas canvas;

	private GraphicsContext gc;

	private int maxDay = DEFAULT_MAX_DAY;
	private LocalTime scheduleStart = DEFAULT_SCHEDULE_START;
	private LocalTime scheduleEnd = DEFAULT_SCHEDULE_END;
	private int dayHeight = (int) (DateTimeUtil.getMinutesBetween(scheduleStart,
			scheduleEnd) * PIXELS_PER_MINUTE);

	private ArrayList<MeetingBlock> meetingBlocks;

	// Art constants
	private static final int DEFAULT_MAX_DAY = 5;
	private static final LocalTime DEFAULT_SCHEDULE_START = LocalTime.of(9, 0);
	private static final LocalTime DEFAULT_SCHEDULE_END = LocalTime.of(17, 0);

	private static final int DAY_WIDTH = 100;
	private static final int TIME_LABEL_WIDTH = 45;
	private static final int TIME_LABEL_Y_OFFSET = 6;
	private static final int DAY_LABEL_HEIGHT = 20;
	private static final int NEWLINE_HEIGHT = 16;
	private static final double PIXELS_PER_MINUTE = 1.2;

	private static final int PADDING_LEFT = 10;
	private static final int PADDING_RIGHT = 0;
	private static final int PADDING_BOTTOM = 20;
	private static final int PADDING_TOP = 20;

	private static final String BORDER_COLOR = "#cccccc";
	private static final String EMPTY_SLOT_COLOR_PRESENT_OR_FUTURE = "#eeeeee";
	private static final String EMPTY_SLOT_COLOR_PAST = "#dddddd";

	private static final int MAX_RECENT_COLORS = 5;

	// Times for forms related to CourseSchedule.
	public static String[] times = DateTimeUtil.generateTimesAsStrings(2);

	// Recent colors used for non-course meetings.
	private static ArrayList<Color> recentColors = new ArrayList<>();

	/**
	 * Initialize.
	 *
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	@FXML
	public void initialize() throws SqliteWrapperException, SQLException {

		this.observable = Main.active;
		this.observable.addObserver(this);

		this.gc = this.canvas.getGraphicsContext2D();
		this.meetingBlocks = new ArrayList<>();
		drawSchedule();

		this.selectWeek.valueProperty()
				.addListener(new ChangeListener<LocalDate>() {
					@Override
					public void changed(
							ObservableValue<? extends LocalDate> observable,
							LocalDate oldDate, LocalDate newDate) {

						try {
							Main.active.setSelectedDate(newDate);

							if (!DateTimeUtil.isSameWeek(LocalDate.now(),
									newDate)) {

								updateSelectCurrentWeek(false);
							}
						} catch (SqliteWrapperException | SQLException e) {
							e.printStackTrace();
						}
					}
				});

		this.selectCurrentWeek.selectedProperty()
				.addListener(new ChangeListener<Boolean>() {
					@Override
					public void changed(
							ObservableValue<? extends Boolean> observable,
							Boolean oldVal, Boolean newVal) {

						if (newVal) {
							updateSelectWeek(LocalDate.now());
						}
					}
				});

		if (Main.active.getTermInProgress() != null) {
			this.selectCurrentWeek.setSelected(true);
		}

		this.canvas.addEventHandler(MouseEvent.MOUSE_CLICKED,
				new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {

						try {
							canvasMouseClicked(event.getX(), event.getY());
						} catch (SqliteWrapperException | SQLException
								| IOException e) {
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * Update the 'select current week' checkbox.
	 *
	 * @param selectCurrentWeek
	 *            whether or not the current week is selected
	 */
	private void updateSelectCurrentWeek(boolean selectCurrentWeek) {

		this.selectCurrentWeek.setSelected(selectCurrentWeek);
	}

	/**
	 * Update select week.
	 *
	 * @param date
	 *            the date
	 */
	private void updateSelectWeek(LocalDate date) {

		this.selectWeek.setValue(date);
	}

	/**
	 * Draw schedule.
	 *
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	private void drawSchedule() throws SqliteWrapperException, SQLException {

		this.gc.clearRect(0, 0, this.canvas.getWidth(),
				this.canvas.getHeight());

		calcCanvasSize();

		drawDayLabels();

		drawDays();

		drawTimeLabels();

		drawMeetings();
	}

	/**
	 * Handle mouse clicks on the schedule. Max position is at the bottom right
	 * of the canvas.
	 *
	 * @param x
	 *            the x position relative to the canvas
	 * @param y
	 *            the y position relative to the canvas
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void canvasMouseClicked(double x, double y)
			throws SqliteWrapperException, SQLException, IOException {

		int dayClicked = getDayClicked(x);

		if (dayClicked == 0) {
			return;
		}

		LocalTime timeClicked = getTimeClicked(y);

		if (timeClicked == null) {
			return;
		}

		// First check if the user has clicked on an existing meeting. If so,
		// prompt them to edit the meeting set to which that meeting belongs.
		// Otherwise, round the time they clicked on and let them add a Meeting
		// to the CourseSchedule with the time already filled in.
		LocalDate selectedDate = getDateFromDayOfWeek(dayClicked);
		LocalDateTime selectedDateTime = LocalDateTime.of(selectedDate,
				timeClicked);

		MeetingDescription existing = Meeting
				.getMeetingDuring(selectedDateTime);

		if (existing != null) {

			new EditMeetingOptionsController(existing);

		} else {

			LocalTime adjustedTime = DateTimeUtil
					.roundToPrevHalfHour(timeClicked);
			LocalDateTime adjustedDateTime = LocalDateTime.of(selectedDate,
					adjustedTime);

			System.out.println(
					"Add meeting on " + DateTimeUtil.intToDay(dayClicked)
							+ " at " + adjustedTime);

			new AddMeetingController(adjustedDateTime);
		}
	}

	/**
	 * Gets the day clicked.
	 *
	 * @param x
	 *            the x
	 * @return the day clicked
	 */
	private int getDayClicked(double x) {

		int dayClicked = 0;

		if (x >= getDayXPosition(1)
				&& x <= (getDayXPosition(this.maxDay) + DAY_WIDTH)) {

			int i = 2;

			while (i <= this.maxDay) {

				if (x < getDayXPosition(i)) {

					dayClicked = i - 1;
					break;
				}

				i++;
			}

			if (dayClicked == 0) {

				dayClicked = this.maxDay;
			}
		}

		return dayClicked;
	}

	/**
	 * Gets the time clicked.
	 *
	 * @param y
	 *            the y
	 * @return the time clicked
	 */
	private LocalTime getTimeClicked(double y) {

		LocalTime clicked = null;

		double minY = PADDING_TOP + DAY_LABEL_HEIGHT;
		double maxY = this.canvas.getHeight() - PADDING_BOTTOM;

		if (y >= minY && y <= maxY) {

			clicked = this.scheduleStart
					.plusMinutes((long) ((y - minY) / PIXELS_PER_MINUTE));
		}

		return clicked;
	}

	/**
	 * Gets the date of a day of the week from the currently selected week.
	 *
	 * @param dayOfWeek
	 *            the day of the week from the currently selected week
	 * @return the date
	 */
	private LocalDate getDateFromDayOfWeek(int dayOfWeek) {

		LocalDate startOfWeek = DateTimeUtil
				.getStartOfWeek(Main.active.getSelectedDate());

		return startOfWeek.plusDays(dayOfWeek - 1);
	}

	/**
	 * Draw meetings.
	 *
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
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

	/**
	 * Creates the meeting block from the meeting.
	 *
	 * @param meeting
	 *            the meeting
	 * @return the meeting block
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	private MeetingBlock createMeetingBlockFrom(MeetingDescription meeting)
			throws SqliteWrapperException, SQLException {

		int dayOfWeek = meeting.date.getDayOfWeek().getValue();
		LocalTime start = meeting.set.getStart();

		double xOffset = getDayXPosition(dayOfWeek);
		double yOffset = (DateTimeUtil.getMinutesBetween(scheduleStart, start)
				* PIXELS_PER_MINUTE) + PADDING_TOP + DAY_LABEL_HEIGHT;
		double height = DateTimeUtil.getMinutesBetween(start,
				meeting.set.getEnd()) * PIXELS_PER_MINUTE;

		Rectangle rect = new Rectangle(xOffset, yOffset, DAY_WIDTH, height);
		rect.setFill(Paint.valueOf(MeetingSet.getColor(meeting.setID)));

		return new MeetingBlock(meeting, rect);
	}

	/**
	 * Draw meeting.
	 *
	 * @param meetingBlock
	 *            the meeting block
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	private void drawMeeting(MeetingBlock meetingBlock)
			throws SqliteWrapperException, SQLException {

		setFill(meetingBlock.rect.getFill());

		this.gc.fillRect(meetingBlock.rect.getX(), meetingBlock.rect.getY(),
				meetingBlock.rect.getWidth(), meetingBlock.rect.getHeight());

		Color meetingColor = Color
				.web(MeetingSet.getColor(meetingBlock.meeting.setID));

		if (ColorUtil.isDark(meetingColor)) {
			setFill(Main.TEXT_LIGHT_COLOR);
		} else {
			setFill(Main.TEXT_DARK_COLOR);
		}

		String meetingText = "";

		if (meetingBlock.meeting.set.isCourseMeeting()) {

			meetingText = meetingBlock.meeting.set.getCourse().toString() + "\n"
					+ meetingBlock.meeting.set.getType();

		} else {

			meetingText = meetingBlock.meeting.set.getName() + "\n"
					+ meetingBlock.meeting.set.getType();
		}

		this.gc.fillText(meetingText, PADDING_LEFT + meetingBlock.rect.getX(),
				meetingBlock.rect.getY() + 20, meetingBlock.rect.getWidth());
	}

	/**
	 * Draw day labels.
	 */
	private void drawDayLabels() {

		setFill(Main.TEXT_DARK_COLOR);

		for (int i = 1; i <= maxDay; i++) {

			LocalDate startOfWeek = this.getDateFromDayOfWeek(i);

			// This will display the date in the format of:
			//
			// dayOfWeek (e.g. Monday)
			// month date (e.g. Jan. 31st)
			this.gc.fillText(DateTimeUtil.intToDay(i), getDayXPosition(i),
					PADDING_TOP);

			this.gc.fillText(DateTimeUtil.shortPrettyDate(startOfWeek),
					getDayXPosition(i), PADDING_TOP + NEWLINE_HEIGHT);
		}
	}

	/**
	 * Gets the day X position.
	 *
	 * @param day
	 *            the day
	 * @return the day X position
	 */
	private int getDayXPosition(int day) {

		int dayXPos = ((day - 1) * DAY_WIDTH) + PADDING_LEFT + TIME_LABEL_WIDTH;

		return dayXPos;
	}

	/**
	 * Draw days.
	 */
	private void drawDays() {

		for (int i = 1; i <= maxDay; i++) {

			for (int j = 0; j < DateTimeUtil.getMinutesBetween(
					this.scheduleStart, this.scheduleEnd); j += 30) {

				// The start date/time represented by this slot.
				LocalDateTime slot = LocalDateTime.of(getDateFromDayOfWeek(i),
						this.scheduleStart.plusMinutes(j));

				LocalDateTime now = LocalDateTime.now();

				// First, draw the border.
				setFill(BORDER_COLOR);

				double slotYPosition = PADDING_TOP + DAY_LABEL_HEIGHT
						+ (j * PIXELS_PER_MINUTE);

				this.gc.fillRect(getDayXPosition(i), slotYPosition, DAY_WIDTH,
						(30 * PIXELS_PER_MINUTE));

				// Determine how much of the slot should be colored as in the
				// past.
				int minutesInPast = 0;

				boolean entireSlotIsInPast = slot.isBefore(LocalDateTime.of(
						now.toLocalDate(),
						DateTimeUtil.roundToPrevHalfHour(now.toLocalTime())));

				if (entireSlotIsInPast) {

					minutesInPast = 30;

				} else if (slot.isBefore(now)) {

					// Some of the slot is in the past.
					minutesInPast = now.getMinute() - slot.getMinute();
				}

				// For each slot, first draw the portion that is in the past;
				// then draw the portion that is in the present or future.
				if (i == 1 && j == 0) {

					// First day, first slot

					setFill(EMPTY_SLOT_COLOR_PAST);

					this.gc.fillRect(getDayXPosition(i) + 1, slotYPosition + 1,
							DAY_WIDTH - 2,
							(minutesInPast * PIXELS_PER_MINUTE) - 2);

					setFill(EMPTY_SLOT_COLOR_PRESENT_OR_FUTURE);

					this.gc.fillRect(getDayXPosition(i) + 1,
							slotYPosition + 1
									+ (minutesInPast * PIXELS_PER_MINUTE),
							DAY_WIDTH - 2,
							((30 - minutesInPast) * PIXELS_PER_MINUTE) - 2);

				} else if (i == 1) {

					// First day, not the first slot

					setFill(EMPTY_SLOT_COLOR_PAST);

					this.gc.fillRect(getDayXPosition(i) + 1, slotYPosition,
							DAY_WIDTH - 2,
							(minutesInPast * PIXELS_PER_MINUTE) - 1);

					setFill(EMPTY_SLOT_COLOR_PRESENT_OR_FUTURE);

					this.gc.fillRect(getDayXPosition(i) + 1,
							slotYPosition + (minutesInPast * PIXELS_PER_MINUTE),
							DAY_WIDTH - 2,
							((30 - minutesInPast) * PIXELS_PER_MINUTE) - 1);

				} else if (i != 1 && j == 0) {

					// Not the first day, first slot

					setFill(EMPTY_SLOT_COLOR_PAST);

					this.gc.fillRect(getDayXPosition(i), slotYPosition + 1,
							DAY_WIDTH - 1,
							(minutesInPast * PIXELS_PER_MINUTE) - 2);

					setFill(EMPTY_SLOT_COLOR_PRESENT_OR_FUTURE);

					this.gc.fillRect(getDayXPosition(i),
							slotYPosition + 1
									+ (minutesInPast * PIXELS_PER_MINUTE),
							DAY_WIDTH - 1,
							((30 - minutesInPast) * PIXELS_PER_MINUTE) - 2);

				} else if (i != 1) {

					// Not the first day, not the first slot

					setFill(EMPTY_SLOT_COLOR_PAST);

					this.gc.fillRect(getDayXPosition(i), slotYPosition,
							DAY_WIDTH - 1,
							(minutesInPast * PIXELS_PER_MINUTE) - 1);

					setFill(EMPTY_SLOT_COLOR_PRESENT_OR_FUTURE);

					this.gc.fillRect(getDayXPosition(i),
							slotYPosition + (minutesInPast * PIXELS_PER_MINUTE),
							DAY_WIDTH - 1,
							((30 - minutesInPast) * PIXELS_PER_MINUTE) - 1);
				}
			}
		}

	}

	/**
	 * Sets the fill.
	 *
	 * @param color
	 *            the new fill
	 */
	private void setFill(String color) {

		this.gc.setFill(Paint.valueOf(color));
	}

	/**
	 * Sets the fill.
	 *
	 * @param paint
	 *            the new fill
	 */
	private void setFill(Paint paint) {

		this.gc.setFill(paint);
	}

	/**
	 * Draw time labels.
	 */
	private void drawTimeLabels() {

		setFill(Main.TEXT_DARK_COLOR);

		// FIXME: This works so long as PIXELS_PER_MINUTE > 1.0
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

	/**
	 * Calc canvas size.
	 *
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	private void calcCanvasSize() throws SqliteWrapperException, SQLException {

		calcCanvasWidth();
		calcCanvasHeight();
	}

	/**
	 * Calc canvas width.
	 *
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	private void calcCanvasWidth() throws SqliteWrapperException, SQLException {

		// Determine the width of the schedule by determining if Saturday or
		// Sunday have any meetings during this term. Otherwise, we will show 5
		// days, Monday-Friday, the default value of maxDay.
		int lastDayOfWeek = Term
				.getLastDayOfWeek(this.observable.getSelectedTerm());

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

	/**
	 * Calc canvas height.
	 *
	 * @throws SqliteWrapperException
	 *             the sqlite wrapper exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	private void calcCanvasHeight()
			throws SqliteWrapperException, SQLException {

		// If no Meetings exist, then display 9 am - 5 pm.
		this.scheduleStart = DEFAULT_SCHEDULE_START;
		this.scheduleEnd = DEFAULT_SCHEDULE_END;

		// Otherwise, reconfigure the schedule to display 30 minutes
		// prior to the first meeting to 30 minutes after the last meeting.
		if (Term.hasMeetings(this.observable.getSelectedTerm())) {

			LocalTime earliestStart = Term.getEarliestMeetingStartTime(
					this.observable.getSelectedTerm());
			LocalTime latestEnd = Term
					.getLatestMeetingEndTime(this.observable.getSelectedTerm());

			LocalTime earliestStartRounded = DateTimeUtil
					.roundToPrevHalfHourThatIsAtLeastHalfAnHourAgo(
							earliestStart);
			LocalTime latestEndRounded = DateTimeUtil
					.roundToNextHalfHourThatIsAtLeastHalfAnHourFrom(latestEnd);

			this.scheduleStart = earliestStartRounded;
			this.scheduleEnd = latestEndRounded;

			System.out.println("Earliest meeting start = " + earliestStart
					+ ", Latest meeting end = " + latestEnd);
			System.out.println("Schedule starts at " + this.scheduleStart
					+ " and ends at " + this.scheduleEnd);
		}

		// Height of each day of the schedule, determined by how much time the
		// schedule has been configured to span.
		this.dayHeight = (int) (DateTimeUtil.getMinutesBetween(
				this.scheduleStart, this.scheduleEnd) * PIXELS_PER_MINUTE);

		this.canvas.setHeight(PADDING_TOP + DAY_LABEL_HEIGHT + this.dayHeight
				+ PADDING_BOTTOM);
	}

	public static void addRecentColor(Color color) {

		if (recentColors.contains(color)) {

			recentColors.remove(color);
		}

		else if (recentColors.size() == MAX_RECENT_COLORS) {

			recentColors.remove(MAX_RECENT_COLORS - 1);
		}

		recentColors.add(0, color);
	}

	public static ArrayList<Color> getRecentColors() {

		return recentColors;
	}

	/**
	 * Update.
	 *
	 * @param arg0
	 *            the arg 0
	 * @param arg1
	 *            the arg 1
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
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
