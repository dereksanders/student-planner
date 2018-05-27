package views;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Observable;
import java.util.Observer;

import core.Main;
import core.Profile;
import core.Term;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import sqlite.SqliteWrapperException;
import utility.ColorUtil;
import utility.DateTimeUtil;

public class TermCalendarController implements Observer {

	private Profile observable;

	@FXML
	private Canvas canvas;

	private GraphicsContext gc;

	private static final int PADDING_LEFT = 20;
	private static final int PADDING_RIGHT = 20;
	private static final int PADDING_BOTTOM = 20;
	private static final int PADDING_TOP = 20;

	private static final int MONTH_WIDTH = 280;
	private static final int MONTH_HEIGHT = 250;

	private static final int MONTH_HEADER_HEIGHT = 25;
	private static final int DAY_LABELS_AREA_HEIGHT = 20;

	private static final int HORIZONTAL_SPACING = 20;
	private static final int VERTICAL_SPACING = 20;

	private static final int MONTHS_PER_ROW = 2;

	private static String CALENDAR_HEADER = "";
	private static String CALENDAR_HEADER_TEXT = "";

	private static final String CALENDAR_BACKGROUND = "#f4f4f4";
	private static final String DAY_LABELS_AREA = "#cccccc";
	private static final String CALENDAR_TEXT = "#000000";
	private static final String EMPTY_DATE_COLOR = "#bbbbbb";
	private static final String NUMBERED_DATE_COLOR = "#ffffff";

	private int numMonths;

	@FXML
	public void initialize() throws SqliteWrapperException, SQLException {

		this.observable = Main.active;
		this.observable.addObserver(this);

		this.gc = this.canvas.getGraphicsContext2D();
		drawCalendar();
	}

	private void drawCalendar() throws SqliteWrapperException, SQLException {

		String selectedTermColor = Term.getColor(Main.active.getSelectedTerm());

		CALENDAR_HEADER = selectedTermColor;
		if (ColorUtil.isDark(Color.web(selectedTermColor))) {

			CALENDAR_HEADER_TEXT = Main.TEXT_LIGHT_COLOR;

		} else {

			CALENDAR_HEADER_TEXT = Main.TEXT_DARK_COLOR;
		}

		this.numMonths = DateTimeUtil.getMonthsBetween(
				Main.active.getSelectedTerm().getStart(),
				Main.active.getSelectedTerm().getEnd()) + 1;

		calcCanvasSize();

		drawMonths();
	}

	private void calcCanvasSize() {

		calcCanvasWidth();
		calcCanvasHeight();
	}

	private void calcCanvasWidth() {

		if (this.numMonths < MONTHS_PER_ROW) {

			this.canvas
					.setWidth(
							PADDING_LEFT + MONTH_WIDTH
									+ ((HORIZONTAL_SPACING + MONTH_WIDTH)
											* (this.numMonths - 1))
									+ PADDING_RIGHT);
		} else {

			this.canvas
					.setWidth(
							PADDING_LEFT + MONTH_WIDTH
									+ ((HORIZONTAL_SPACING + MONTH_WIDTH)
											* (MONTHS_PER_ROW - 1))
									+ PADDING_RIGHT);
		}
	}

	private void calcCanvasHeight() {

		int rows;

		if (this.numMonths % MONTHS_PER_ROW == 0) {

			rows = this.numMonths / MONTHS_PER_ROW;

		} else {

			rows = (this.numMonths / MONTHS_PER_ROW) + 1;
		}

		this.canvas.setHeight(PADDING_TOP + MONTH_HEIGHT
				+ ((VERTICAL_SPACING + MONTH_HEIGHT) * (rows - 1))
				+ PADDING_BOTTOM);
	}

	private void drawMonths() {

		for (int i = 0; i < this.numMonths; i++) {

			int row = i / MONTHS_PER_ROW;
			int col = i % MONTHS_PER_ROW;

			int monthXPos = PADDING_LEFT
					+ ((MONTH_WIDTH + HORIZONTAL_SPACING) * col);
			int monthYPos = PADDING_TOP
					+ ((MONTH_HEIGHT + VERTICAL_SPACING) * row);

			LocalDate startOfTerm = Main.active.getSelectedTerm().getStart();

			String month = DateTimeUtil
					.getMonth(startOfTerm.plusMonths(i).getMonthValue());

			LocalDate firstOfMonth = LocalDate.of(startOfTerm.getYear(),
					startOfTerm.plusMonths(i).getMonthValue(), 1);

			drawCalendarBackground(monthXPos, monthYPos);
			drawCalendarHeader(month, monthXPos, monthYPos);
			drawDayLabels(monthXPos, monthYPos);
			drawDates(firstOfMonth, monthXPos, monthYPos);
		}
	}

	private void drawCalendarBackground(int monthXPos, int monthYPos) {

		this.gc.setFill(Paint.valueOf(CALENDAR_BACKGROUND));
		this.gc.fillRect(monthXPos, monthYPos, MONTH_WIDTH, MONTH_HEIGHT);
	}

	private void drawCalendarHeader(String month, int monthXPos,
			int monthYPos) {

		this.gc.setFill(Paint.valueOf(CALENDAR_HEADER));
		this.gc.fillRect(monthXPos, monthYPos, MONTH_WIDTH,
				MONTH_HEADER_HEIGHT);

		this.gc.setFill(Paint.valueOf(CALENDAR_HEADER_TEXT));
		this.gc.fillText(month, monthXPos + 10,
				monthYPos + MONTH_HEADER_HEIGHT - 7);
	}

	private void drawDayLabels(int monthXPos, int monthYPos) {

		this.gc.setFill(Paint.valueOf(DAY_LABELS_AREA));
		this.gc.fillRect(monthXPos, monthYPos + MONTH_HEADER_HEIGHT,
				MONTH_WIDTH, DAY_LABELS_AREA_HEIGHT);

		for (int j = 1; j <= 7; j++) {

			this.gc.setFill(Paint.valueOf(CALENDAR_TEXT));
			this.gc.fillText(DateTimeUtil.intToDayShort(j),
					monthXPos + ((MONTH_WIDTH / 7) * (j - 1)) + 10, monthYPos
							+ MONTH_HEADER_HEIGHT + DAY_LABELS_AREA_HEIGHT - 5);
		}
	}

	private void drawDates(LocalDate firstOfMonth, int monthXPos,
			int monthYPos) {

		int dateWidth = MONTH_WIDTH / 7;
		int dateHeight = (MONTH_HEIGHT - MONTH_HEADER_HEIGHT
				- DAY_LABELS_AREA_HEIGHT) / 6;

		int firstDayOfWeek = firstOfMonth.getDayOfWeek().getValue();
		int datesAdded = 0;

		for (int i = 1; i <= 6; i++) {

			for (int j = 1; j <= 7; j++) {

				if (i == 1 && j < firstDayOfWeek) {

					this.gc.setFill(Paint.valueOf(EMPTY_DATE_COLOR));
					this.gc.fillRect(monthXPos + (dateWidth * (j - 1)),
							monthYPos + MONTH_HEADER_HEIGHT
									+ DAY_LABELS_AREA_HEIGHT
									+ (dateHeight * (i - 1)),
							dateWidth, dateHeight);

				} else if (datesAdded < firstOfMonth.lengthOfMonth()) {

					this.gc.setFill(Paint.valueOf(NUMBERED_DATE_COLOR));
					this.gc.fillRect(monthXPos + (dateWidth * (j - 1)),
							monthYPos + MONTH_HEADER_HEIGHT
									+ DAY_LABELS_AREA_HEIGHT
									+ (dateHeight * (i - 1)),
							dateWidth, dateHeight);

					this.gc.setFill(Paint.valueOf(Main.TEXT_DARK_COLOR));
					this.gc.fillText(
							"" + firstOfMonth.plusDays(datesAdded)
									.getDayOfMonth(),
							monthXPos + (dateWidth * (j - 1)) + 15,
							monthYPos + MONTH_HEADER_HEIGHT
									+ DAY_LABELS_AREA_HEIGHT
									+ (dateHeight * (i - 1)) + 23);

					datesAdded++;

				} else {

					this.gc.setFill(Paint.valueOf(CALENDAR_BACKGROUND));
					this.gc.fillRect(monthXPos + (dateWidth * (j - 1)),
							monthYPos + MONTH_HEADER_HEIGHT
									+ DAY_LABELS_AREA_HEIGHT
									+ (dateHeight * (i - 1)),
							dateWidth, dateHeight);
				}
			}
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof Profile) {
			try {
				System.out.println("redrawing..");
				drawCalendar();
			} catch (SqliteWrapperException | SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
