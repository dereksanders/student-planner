package views;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import core.Course;
import core.Main;
import core.MeetingSet;
import core.Profile;
import core.Term;
import core.TermDescription;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import sqlite.SqliteWrapperException;
import utility.DateTimeUtil;

public class MainController implements Observer {

	private Observable profile;

	private TermDescription selectedTerm;

	@FXML
	private AnchorPane schedulePane;
	@FXML
	private ChoiceBox<TermDescription> selectTerm;
	@FXML
	private ScrollPane scheduleScroll;
	@FXML
	private CheckBox showCurrentWeek;
	@FXML
	private DatePicker scheduleSelectDate;
	@FXML
	private HBox inProgressBox;
	@FXML
	private HBox selectedBox;
	@FXML
	private Label inProgress;

	private static final String EMPTY_TERM_COLOR = "#eeeeee";

	@FXML
	public void initialize() throws SqliteWrapperException, SQLException {

		// Whenever we get to this view, we want to open the current profile
		// when we next start the planner as getting here indicates that we have
		// either loaded a profile or successfully created a new one.
		Main.config.write();

		this.profile = Main.active;
		this.profile.addObserver(this);

		ArrayList<TermDescription> terms = Term.populateTerms();
		selectTerm.setItems(FXCollections.observableArrayList(terms));

		// By default, select the term in progress if it exists, otherwise the
		// most recent term (which should be the last term in the list).
		Main.active.termInProgress = Term.getTermInProgress();
		if (Main.active.termInProgress != null) {

			inProgressBox.setStyle("-fx-background-color: "
					+ Term.getColor(Main.active.termInProgress));

			updateSelectedDate(LocalDate.now());

		} else {

			// If there is no Term containing the current date, then disable
			// 'Show Current Week'.
			showCurrentWeek.setVisible(false);
			showCurrentWeek.setManaged(false);

			selectedTerm = terms.get(terms.size() - 1);

			updateSelectedDate(selectedTerm.start);

			showCurrentWeek.setSelected(false);
		}

		CourseSchedule cs = new CourseSchedule(selectTerm.getValue());

		scheduleScroll.setContent(cs.getCanvas());

		selectTerm.valueProperty()
				.addListener(new ChangeListener<TermDescription>() {

					@Override
					public void changed(
							ObservableValue<? extends TermDescription> observable,
							TermDescription oldTerm, TermDescription newTerm) {

						try {
							if (newTerm.equals(Main.active.termInProgress)) {
								updateSelectedDate(LocalDate.now());
							} else {
								// FIXME: Perhaps if a Term from the past is
								// selected, the end date should be selected.
								updateSelectedDate(newTerm.start);
							}

						} catch (SqliteWrapperException | SQLException e) {
							e.printStackTrace();
						}
					}
				});

		showCurrentWeek.selectedProperty()
				.addListener(new ChangeListener<Boolean>() {
					@Override
					public void changed(
							ObservableValue<? extends Boolean> observable,
							Boolean oldVal, Boolean newVal) {

						try {
							updateShowCurrentWeek(newVal);
						} catch (SqliteWrapperException | SQLException e) {
							e.printStackTrace();
						}
					}
				});

		// Add arbitrary MeetingSet
		ArrayList<LocalDate> dates = new ArrayList<>();
		dates.add(LocalDate.now().minusDays(5));

		System.out.println(Main.active.termInProgress);
		System.out.println("Printing out all courses..");
		Course.printCourses();

		MeetingSet.addMeetingSet(1, Main.active.termInProgress,
				Course.getCourse(Main.active.termInProgress),
				LocalTime.of(9, 0), LocalTime.of(10, 0), dates);

		Main.active.db.showSchema("meeting_set");
	}

	private void updateShowCurrentWeek(boolean showCurrentWeek)
			throws SqliteWrapperException, SQLException {

		if (showCurrentWeek) {

			if (Main.active.termInProgress != null) {
				updateSelectedDate(LocalDate.now());
			}
		}
	}

	private void updateSelectedDate(LocalDate selected)
			throws SqliteWrapperException, SQLException {

		TermDescription containsDate = Term.findTerm(selected);

		if (containsDate != null) {

			Main.active.setSelectedDate(selected);
			scheduleSelectDate.setValue(selected);

			if (DateTimeUtil.isSameWeek(selected, LocalDate.now())) {
				showCurrentWeek.setSelected(true);
			} else {
				showCurrentWeek.setSelected(false);
			}

			updateSelectedTerm(containsDate);

		} else {

			Main.showAlert(AlertType.ERROR, "Cannot select date",
					"This date does not belong to any Term.");
		}
	}

	@Override
	public void update(Observable o, Object arg) {

		if (o instanceof Profile) {
			try {
				updateTermInProgress();
			} catch (SqliteWrapperException | SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void updateTermInProgress()
			throws SqliteWrapperException, SQLException {

		if (Main.active.termInProgress != null) {

			inProgressBox.setStyle("-fx-background-color: "
					+ Term.getColor(Main.active.termInProgress));

			// If there is now a Term in progress when there wasn't before, then
			// we need to enable the showCurrentWeek checkbox which should be
			// disabled when there is no Term in progress.
			if (!showCurrentWeek.isVisible()) {

				showCurrentWeek.setVisible(true);
				showCurrentWeek.setManaged(true);
			}

		} else {

			inProgressBox.setStyle("-fx-background-color: " + EMPTY_TERM_COLOR);
		}
	}

	private void updateSelectedTerm(TermDescription term)
			throws SqliteWrapperException, SQLException {

		selectTerm.setValue(term);
		selectedTerm = term;
		selectedBox.setStyle("-fx-background-color: " + Term.getColor(term));
	}
}
