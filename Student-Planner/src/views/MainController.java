package views;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import core.Main;
import core.Profile;
import core.Term;
import core.TermDescription;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import sqlite.SqliteWrapperException;

public class MainController implements Observer {

	private Observable profile;

	private TermDescription termInProgress;
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

	@FXML
	public void initialize() throws SqliteWrapperException, SQLException {

		// Whenever we get to this view, we want to open the current profile
		// when we next start the planner as getting here indicates that we have
		// either loaded a profile or successfully created a new one.
		Main.writeCurrentConfig();

		this.profile = Main.active;
		this.profile.addObserver(this);

		ArrayList<TermDescription> terms = Term.populateTerms();
		selectTerm.setItems(FXCollections.observableArrayList(terms));

		// By default, select the term in progress if it exists, otherwise the
		// most recent term (which should be the last term in the list).
		termInProgress = Term.getTermInProgress();
		if (termInProgress != null) {

			inProgressBox.setStyle(
					"-fx-background-color: " + Term.getColor(termInProgress));

			updateSelectedTerm(termInProgress);
			updateSelectedDate(LocalDate.now());
			showCurrentWeek.setSelected(true);

		} else {

			// If there is no Term containing the current date, then disable
			// 'Show Current Week'.
			showCurrentWeek.setVisible(false);
			showCurrentWeek.setManaged(false);

			selectedTerm = terms.get(terms.size() - 1);

			updateSelectedTerm(selectedTerm);

			scheduleSelectDate.setValue(selectedTerm.start);
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
							updateSelectedTerm(newTerm);
						} catch (SqliteWrapperException | SQLException e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void updateSelectedDate(LocalDate selected) {

		Main.active.setSelectedDate(selected);
		scheduleSelectDate.setValue(selected);
	}

	@Override
	public void update(Observable o, Object arg) {

		if (o instanceof Profile) {
			try {
				updateTerms();
			} catch (SqliteWrapperException | SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void updateTerms() throws SqliteWrapperException, SQLException {

		// By default, select the term in progress if it exists, otherwise the
		// most recent term (which should be the last term in the list).
		termInProgress = Main.active.termInProgress;
		if (termInProgress != null) {

			inProgressBox.setStyle(
					"-fx-background-color: " + Term.getColor(termInProgress));
		}
	}

	private void updateSelectedTerm(TermDescription term)
			throws SqliteWrapperException, SQLException {

		selectTerm.setValue(term);
		selectedTerm = term;
		selectedBox.setStyle("-fx-background-color: " + Term.getColor(term));

		scheduleSelectDate.setValue(term.start);
	}
}
