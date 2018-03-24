package views;

import java.io.IOException;
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
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import sqlite.SqliteWrapperException;
import utility.ColorUtil;

public class MainController implements Observer {

	private Observable profile;

	// Tabbed views
	@FXML
	private AnchorPane schedulePane;
	@FXML
	private AnchorPane calendarPane;
	@FXML
	private AnchorPane gradesPane;
	@FXML
	private AnchorPane plotsPane;

	// Local elements
	@FXML
	private ChoiceBox<TermDescription> selectTerm;
	@FXML
	private HBox inProgressBox;
	@FXML
	private HBox selectedBox;
	@FXML
	private Label inProgress;
	@FXML
	private Text inProgressTitle;
	@FXML
	private Text selectedHeader;

	private static final String EMPTY_TERM_COLOR = "#eeeeee";

	@FXML
	public void initialize()
			throws SqliteWrapperException, SQLException, IOException {

		// Whenever we get to this view, we want to open the current profile
		// when we next start the planner as getting here indicates that we have
		// either loaded a profile or successfully created a new one.
		Main.config.write();
		Main.window.setTitle(Main.active.name + " - " + Main.title);

		this.profile = Main.active;
		this.profile.addObserver(this);

		ArrayList<TermDescription> terms = Term.populateTerms();
		selectTerm.setItems(FXCollections.observableArrayList(terms));

		// By default, select the term in progress if it exists, otherwise the
		// most recent term (which should be the last term in the list).
		TermDescription termInProgress = Term.getTermInProgress();
		Main.active.setTermInProgress(termInProgress);

		updateTermInProgressBox();

		if (termInProgress != null) {
			Main.active.setSelectedDate(LocalDate.now());
			updateSelectedTermBox(termInProgress);
		} else {

			TermDescription selected = terms.get(terms.size() - 1);

			// FIXME: If there is no term in progress, then the end of the most
			// recent term should be selected if it is BEFORE the current date.
			// If there are Terms in the future, select the start of the soonest
			// one.
			Main.active.setSelectedDate(selected.start);
			updateSelectedTermBox(selected);
		}

		loadTabs();

		// Add arbitrary MeetingSet for testing purposes.
		ArrayList<LocalDate> dates = new ArrayList<>();
		dates.add(LocalDate.now().minusDays(5));

		MeetingSet.addMeetingSet(1, Main.active.getTermInProgress(),
				Course.getCourse(Main.active.getTermInProgress()),
				LocalTime.of(9, 0), LocalTime.of(10, 0), dates);
	}

	private void loadTabs() throws IOException {

		AnchorPane cs = FXMLLoader
				.load(getClass().getResource("CourseSchedule.fxml"));

		schedulePane.getChildren().setAll(cs.getChildren());

		calendarPane = FXMLLoader
				.load(getClass().getResource("TermCalendar.fxml"));
		gradesPane = FXMLLoader.load(getClass().getResource("Grades.fxml"));
		plotsPane = FXMLLoader.load(getClass().getResource("Plots.fxml"));
	}

	@Override
	public void update(Observable o, Object arg) {

		if (o instanceof Profile) {
			try {
				updateTermInProgressBox();
			} catch (SqliteWrapperException | SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void updateTermInProgressBox()
			throws SqliteWrapperException, SQLException {

		if (Main.active.getTermInProgress() != null) {

			String termInProgressColor = Term
					.getColor(Main.active.getTermInProgress());

			inProgressBox.setStyle(
					"-fx-background-color: " + termInProgressColor + ";");

			if (ColorUtil.isDark(Color.web(termInProgressColor))) {
				inProgressTitle.setFill(Color.web(Main.TEXT_LIGHT_COLOR));
				inProgress.setStyle(
						"-fx-text-fill: " + Main.TEXT_LIGHT_COLOR + ";");
			} else {
				inProgressTitle.setFill(Color.web(Main.TEXT_DARK_COLOR));
				inProgress.setStyle(
						"-fx-text-fill: " + Main.TEXT_LIGHT_COLOR + ";");
			}

			inProgress.setText(Main.active.getTermInProgress().toString());

		} else {

			inProgressBox.setStyle(
					"-fx-background-color: " + EMPTY_TERM_COLOR + ";");

			if (ColorUtil.isDark(Color.web(EMPTY_TERM_COLOR))) {
				inProgressTitle.setFill(Color.web(Main.TEXT_LIGHT_COLOR));
			} else {
				inProgressTitle.setFill(Color.web(Main.TEXT_DARK_COLOR));
			}

			inProgress.setText("");
		}
	}

	private void updateSelectedTermBox(TermDescription term)
			throws SqliteWrapperException, SQLException {

		selectTerm.setValue(term);

		String termColor = Term.getColor(term);

		selectedBox.setStyle("-fx-background-color: " + termColor + ";");

		if (ColorUtil.isDark(Color.web(termColor))) {

			selectedHeader.setFill(Color.web(Main.TEXT_LIGHT_COLOR));

		} else {

			selectedHeader.setFill(Color.web(Main.TEXT_DARK_COLOR));
		}
	}

}
