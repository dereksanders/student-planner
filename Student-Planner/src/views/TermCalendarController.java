package views;

import java.util.Observable;
import java.util.Observer;

import core.Main;
import core.Profile;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
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

	private static final int MONTH_WIDTH = 250;
	private static final int MONTH_HEIGHT = 250;

	private static final int MONTH_HEADER_HEIGHT = 25;

	private static final int HORIZONTAL_SPACING = 20;
	private static final int VERTICAL_SPACING = 20;

	private static final int MONTHS_PER_ROW = 2;

	private static final String CALENDAR_BACKGROUND = "#dddddd";
	private static final String CALENDAR_HEADER = "#0dbe0d";
	private static final String CALENDAR_HEADER_TEXT = "#ffffff";

	private int numMonths;

	@FXML
	public void initialize() {

		this.observable = Main.active;
		this.observable.addObserver(this);

		this.gc = this.canvas.getGraphicsContext2D();
		drawCalendar();
	}

	private void drawCalendar() {

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

			this.gc.setFill(Paint.valueOf(CALENDAR_BACKGROUND));
			this.gc.fillRect(monthXPos, monthYPos, MONTH_WIDTH, MONTH_HEIGHT);

			this.gc.setFill(Paint.valueOf(CALENDAR_HEADER));
			this.gc.fillRect(monthXPos, monthYPos, MONTH_WIDTH,
					MONTH_HEADER_HEIGHT);

			this.gc.setFill(Paint.valueOf(CALENDAR_HEADER_TEXT));
			this.gc.fillText(
					DateTimeUtil.getMonth(Main.active.getSelectedTerm()
							.getStart().plusMonths(i).getMonthValue()),
					monthXPos + 5, monthYPos + MONTH_HEADER_HEIGHT - 5);
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof Profile) {
			drawCalendar();
		}
	}
}
