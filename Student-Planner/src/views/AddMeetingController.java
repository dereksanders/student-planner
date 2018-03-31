package views;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import core.Course;
import core.CourseDescription;
import core.Main;
import core.Meeting;
import core.MeetingSet;
import core.TermDescription;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sqlite.SqliteWrapperException;

/**
 * The Class AddMeetingController.
 */
public class AddMeetingController {

	private LocalDateTime selected;
	private Stage window;

	// Course Meetings
	@FXML
	private ChoiceBox<CourseDescription> chooseCourse;
	@FXML
	private ChoiceBox<String> chooseTypeOfCourseMeeting;
	@FXML
	private TextField enterTypeOfCourseMeeting;

	// Non-Course Meetings
	@FXML
	private ChoiceBox<String> chooseTypeOfNonCourseMeeting;
	@FXML
	private TextField enterTypeOfNonCourseMeeting;
	@FXML
	private TextField enterMeetingName;
	@FXML
	private ColorPicker chooseMeetingColor;
	@FXML
	private HBox recentColors;

	@FXML
	private DatePicker chooseStartDate;
	@FXML
	private DatePicker chooseEndDate;
	@FXML
	private CheckBox toEndOfTerm;
	@FXML
	private ComboBox<LocalTime> chooseStartTime;
	@FXML
	private ComboBox<LocalTime> chooseEndTime;
	@FXML
	private TextField enterLocation;
	@FXML
	private ChoiceBox<String> chooseRepeat;

	/**
	 * Instantiates a controller for an AddMeeting window and launches the
	 * window.
	 *
	 * @param selected
	 *            the selected date time
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public AddMeetingController(LocalDateTime selected) throws IOException {

		this.selected = selected;

		window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Add Meeting");

		// Because this controller is instantiated, it must be added to the
		// .FXML afterwards, i.e. it cannot be defined as the controller a
		// priori in the .FXML.
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(ViewController.addMeetingPath));
		loader.setController(this);

		Scene scene = new Scene(loader.load());

		window.setScene(scene);
		window.getIcons()
				.add(new Image(Main.class.getResourceAsStream("/icon.png")));
		window.showAndWait();
	}

	/**
	 * Initialize.
	 * 
	 * @throws SQLException
	 * @throws SqliteWrapperException
	 */
	@FXML
	public void initialize() throws SqliteWrapperException, SQLException {

		TermDescription selectedTerm = Main.active.getSelectedTerm();

		ArrayList<CourseDescription> coursesInSelectedTerm = Course
				.populateCourses(selectedTerm);

		chooseCourse.setItems(
				FXCollections.observableArrayList(coursesInSelectedTerm));

		// FIXME: If no courses exist, then the user should only be able to
		// access the "Non-Course Meeting" tab
		if (coursesInSelectedTerm.size() == 0) {

		} else {

			chooseCourse.setValue(coursesInSelectedTerm.get(0));
		}

		chooseTypeOfCourseMeeting.setItems(
				FXCollections.observableArrayList(Meeting.COURSE_TYPES));
		chooseTypeOfCourseMeeting.setValue(Meeting.COURSE_TYPES[0]);

		chooseTypeOfCourseMeeting.valueProperty()
				.addListener(new ChangeListener<String>() {
					@Override
					public void changed(
							ObservableValue<? extends String> observable,
							String oldType, String newType) {

						if (newType != null) {

							if (newType.equals("Other")) {
								enterTypeOfCourseMeeting.setVisible(true);
							} else {
								enterTypeOfCourseMeeting.setVisible(false);
							}
						}
					}
				});

		chooseTypeOfNonCourseMeeting.setItems(
				FXCollections.observableArrayList(Meeting.NON_COURSE_TYPES));
		chooseTypeOfNonCourseMeeting.setValue(Meeting.NON_COURSE_TYPES[0]);

		chooseTypeOfNonCourseMeeting.valueProperty()
				.addListener(new ChangeListener<String>() {
					@Override
					public void changed(
							ObservableValue<? extends String> observable,
							String oldType, String newType) {

						if (newType != null) {

							if (newType.equals("Other")) {
								enterTypeOfNonCourseMeeting.setVisible(true);
							} else {
								enterTypeOfNonCourseMeeting.setVisible(false);
							}
						}
					}
				});

		// Enable "To end of term" by default, setting the end date of the
		// Meeting to the end date of the term.
		toEndOfTerm.selectedProperty()
				.addListener(new ChangeListener<Boolean>() {
					@Override
					public void changed(
							ObservableValue<? extends Boolean> observable,
							Boolean oldVal, Boolean newVal) {

						if (newVal) {
							chooseEndDate.setValue(selectedTerm.getEnd());
							chooseEndDate.setDisable(true);
						} else {
							chooseEndDate.setDisable(false);
						}
					}
				});
		toEndOfTerm.setSelected(true);

		// Check each time the end date is changed that it is after the start
		// date and the end of the term.
		chooseEndDate.valueProperty()
				.addListener(new ChangeListener<LocalDate>() {
					@Override
					public void changed(
							ObservableValue<? extends LocalDate> observable,
							LocalDate oldEndDate, LocalDate newEndDate) {

						if (newEndDate.isBefore(chooseStartDate.getValue())) {

							Main.showAlert(AlertType.ERROR,
									"Cannot select date",
									"End date cannot be prior to start date.");

						} else if (newEndDate.isAfter(selectedTerm.getEnd())) {

							Main.showAlert(AlertType.ERROR,
									"Cannot select date",
									"End date cannot be after the end of the term.");
						}
					}
				});

		// Check each time the start date is changed that it is before the end
		// date and not before the start of the term.
		chooseStartDate.valueProperty()
				.addListener(new ChangeListener<LocalDate>() {
					@Override
					public void changed(
							ObservableValue<? extends LocalDate> observable,
							LocalDate oldStartDate, LocalDate newStartDate) {

						if (newStartDate.isAfter(chooseEndDate.getValue())) {

							Main.showAlert(AlertType.ERROR,
									"Cannot select date",
									"Start date cannot be after end date.");

						} else if (newStartDate
								.isBefore(selectedTerm.getStart())) {

							Main.showAlert(AlertType.ERROR,
									"Cannot select date",
									"Start date cannot be before the start of the term.");
						}
					}
				});

		// Set the start date to the date that the user clicked on, on the
		// schedule.
		//
		// FIXME: It should be safe to assume that this is within the selected
		// term - validation should be done on the schedule's end.
		chooseStartDate.setValue(selected.toLocalDate());

		chooseStartTime.setItems(FXCollections
				.observableArrayList(CourseScheduleController.times));
		chooseEndTime.setItems(FXCollections
				.observableArrayList(CourseScheduleController.times));

		chooseEndTime.getSelectionModel().selectedIndexProperty()
				.addListener(new ChangeListener<Number>() {
					@Override
					public void changed(
							ObservableValue<? extends Number> observable,
							Number old, Number current) {

						if (chooseEndTime.getValue()
								.equals(chooseStartTime.getValue())
								|| chooseEndTime.getValue()
										.isBefore(chooseStartTime.getValue())) {

							Main.showAlert(AlertType.ERROR,
									"Cannot select time",
									"End time cannot be equal to or before start time.");
						}
					}
				});

		chooseStartTime.setValue(selected.toLocalTime());

		if (chooseStartTime.getValue().getHour() < 23) {

			chooseEndTime.setValue(chooseStartTime.getValue().plusHours(1));

		} else if (chooseStartTime.getValue().getMinute() < 30) {

			chooseEndTime.setValue(chooseStartTime.getValue().plusMinutes(30));

		} else {

			chooseEndTime.setValue(chooseStartTime.getValue().plusMinutes(15));
		}

		chooseRepeat.setItems(
				FXCollections.observableArrayList(MeetingSet.REPEAT_OPTIONS));
		chooseRepeat.setValue(MeetingSet.REPEAT_OPTIONS[0]);
	}

	@FXML
	private void confirm() throws SqliteWrapperException, SQLException {
		MeetingSet.addMeetingSet(0, null, null, null, null, null);
	}

	@FXML
	private void cancel() {
		window.close();
	}
}