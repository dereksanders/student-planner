package views;

import java.sql.SQLException;
import java.util.ArrayList;

import core.Main;
import core.Term;
import core.TermDescription;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import sqlite.SqliteWrapperException;

public class MainController {

	private TermDescription termInProgress;
	private TermDescription selectedTerm;

	@FXML
	private ChoiceBox<TermDescription> selectTerm;
	@FXML
	private ScrollPane scheduleScroll;
	@FXML
	private CheckBox selectCurrentWeek;
	@FXML
	private DatePicker scheduleSelectDate;
	@FXML
	private HBox inProgressBox;
	@FXML
	private HBox selectedBox;
	@FXML
	private Label inProgress;

	@FXML
	public void initialize() throws SqliteWrapperException, SQLException {

		// Whenever we get to this view, we want to open the current profile
		// when we next start the planner as getting here indicates that we have
		// either loaded a profile or successfully created a new one.
		Main.writeCurrentConfig();

		ArrayList<TermDescription> terms = Term.populateTerms();
		selectTerm.setItems(FXCollections.observableArrayList(terms));

		// By default, select the term in progress if it exists, otherwise the
		// most recent term (which should be the last term in the list).
		termInProgress = Term.getTermInProgress();
		if (termInProgress != null) {

			inProgressBox.setStyle(
					"-fx-background-color: " + Term.getColor(termInProgress));

			updateSelectedTerm(termInProgress);

		} else {

			updateSelectedTerm(terms.get(terms.size() - 1));
		}

		CourseSchedule cs = new CourseSchedule(selectTerm.getValue());

		scheduleScroll.setContent(cs.getCanvas());
	}

	private void updateSelectedTerm(TermDescription term)
			throws SqliteWrapperException, SQLException {

		selectTerm.setValue(term);
		selectedTerm = term;
		selectedBox.setStyle("-fx-background-color: " + Term.getColor(term));
	}
}
