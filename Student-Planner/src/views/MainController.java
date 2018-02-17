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
import javafx.scene.control.ScrollPane;
import sqlite.SqliteWrapperException;

public class MainController {

	@FXML
	private ChoiceBox<TermDescription> selectTerm;
	@FXML
	private ScrollPane scheduleScroll;
	@FXML
	private CheckBox selectCurrentWeek;
	@FXML
	private DatePicker scheduleSelectDate;

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
		TermDescription inProgress = Term.getTermInProgress();
		if (inProgress != null) {
			selectTerm.setValue(inProgress);
		} else {
			selectTerm.setValue(terms.get(terms.size() - 1));
		}

		CourseSchedule cs = new CourseSchedule(selectTerm.getValue());

		scheduleScroll.setContent(cs.getCanvas());
	}
}
