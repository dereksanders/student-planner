package views;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import core.Course;
import core.CourseDescription;
import core.Main;
import core.Meeting;
import core.MeetingDescription;
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
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sqlite.SqliteWrapperException;
import utility.DateTimeUtil;

/**
 * The Class AddMeetingController.
 */
public class AddMeetingController {

	private LocalDateTime selected;
	private Stage window;

	@FXML
	private Tab courseMeetingTab;
	@FXML
	private Tab nonCourseMeetingTab;

	// Course Meetings
	@FXML
	private ChoiceBox<CourseDescription> chooseCourse;
	@FXML
	private ChoiceBox<String> chooseCourseMeetingType;
	@FXML
	private TextField enterOtherCourseMeetingType;

	// Non-Course Meetings
	@FXML
	private ChoiceBox<String> chooseNonCourseMeetingType;
	@FXML
	private TextField enterOtherNonCourseMeetingType;
	@FXML
	private TextField enterMeetingName;
	@FXML
	private ColorPicker chooseColor;
	@FXML
	private HBox recentColors;

	@FXML
	private DatePicker chooseStartDate;
	@FXML
	private DatePicker chooseEndDate;
	@FXML
	private CheckBox toEndOfTerm;
	@FXML
	private ComboBox<String> chooseStartTime;
	@FXML
	private ComboBox<String> chooseEndTime;
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

		chooseCourseMeetingType.setItems(
				FXCollections.observableArrayList(Meeting.COURSE_TYPES));
		chooseCourseMeetingType.setValue(Meeting.COURSE_TYPES[0]);

		chooseCourseMeetingType.valueProperty()
				.addListener(new ChangeListener<String>() {
					@Override
					public void changed(
							ObservableValue<? extends String> observable,
							String oldType, String newType) {

						if (newType != null) {

							if (newType.equals("Other")) {
								enterOtherCourseMeetingType.setVisible(true);
							} else {
								enterOtherCourseMeetingType.setVisible(false);
							}
						}
					}
				});

		chooseNonCourseMeetingType.setItems(
				FXCollections.observableArrayList(Meeting.NON_COURSE_TYPES));
		chooseNonCourseMeetingType.setValue(Meeting.NON_COURSE_TYPES[0]);

		chooseNonCourseMeetingType.valueProperty()
				.addListener(new ChangeListener<String>() {
					@Override
					public void changed(
							ObservableValue<? extends String> observable,
							String oldType, String newType) {

						if (newType != null) {

							if (newType.equals("Other")) {
								enterOtherNonCourseMeetingType.setVisible(true);
							} else {
								enterOtherNonCourseMeetingType
										.setVisible(false);
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
								|| DateTimeUtil
										.parseLocalTime(
												chooseEndTime.getValue())
										.isBefore(DateTimeUtil.parseLocalTime(
												chooseStartTime.getValue()))) {

							Main.showAlert(AlertType.ERROR,
									"Cannot select time",
									"End time cannot be equal to or before start time.");
						}
					}
				});

		chooseStartTime.setValue(
				DateTimeUtil.localTimeAsString(selected.toLocalTime()));

		if (DateTimeUtil.parseLocalTime(chooseStartTime.getValue())
				.getHour() < 23) {

			chooseEndTime.setValue(DateTimeUtil.localTimeAsString(DateTimeUtil
					.parseLocalTime(chooseStartTime.getValue()).plusHours(1)));

		} else if (DateTimeUtil.parseLocalTime(chooseStartTime.getValue())
				.getMinute() < 30) {

			chooseEndTime.setValue(DateTimeUtil.localTimeAsString(
					DateTimeUtil.parseLocalTime(chooseStartTime.getValue())
							.plusMinutes(30)));

		} else {

			chooseEndTime.setValue(DateTimeUtil.localTimeAsString(
					DateTimeUtil.parseLocalTime(chooseStartTime.getValue())
							.plusMinutes(15)));
		}

		chooseRepeat.setItems(
				FXCollections.observableArrayList(MeetingSet.REPEAT_OPTIONS));
		chooseRepeat.setValue(MeetingSet.REPEAT_OPTIONS[0]);
	}

	@FXML
	private void confirm()
			throws SqliteWrapperException, SQLException, IOException {

		// Construct list of dates based on repeat choice
		ArrayList<LocalDate> meetingDates = new ArrayList<>();

		if (chooseRepeat.getValue().equals("Never")) {

			meetingDates.add(chooseStartDate.getValue());

		} else {

			int weeksBetweenMeetings = 1;

			if (chooseRepeat.getValue().equals("Bi-Weekly")) {
				weeksBetweenMeetings = 2;
			} else if (chooseRepeat.getValue().equals("Monthly")) {
				weeksBetweenMeetings = 4;
			}

			LocalDate current = chooseStartDate.getValue();

			while (current.isBefore(chooseEndDate.getValue())
					|| current.equals(chooseEndDate.getValue())) {

				meetingDates.add(current);
				current = current.plusDays(7 * weeksBetweenMeetings);
			}
		}

		if (DateTimeUtil.isValidLocalTime(chooseStartTime.getValue())
				&& DateTimeUtil.isValidLocalTime(chooseEndTime.getValue())) {

			ArrayList<MeetingDescription> conflicts = Meeting.getConflicts(
					meetingDates,
					DateTimeUtil.parseLocalTime(chooseStartTime.getValue()),
					DateTimeUtil.parseLocalTime(chooseEndTime.getValue()));

			if (conflicts.size() > 0) {

				ConflictsController cc = new ConflictsController(conflicts,
						meetingDates);

				meetingDates = cc.getRemainingDates();
			}

			if (meetingDates.size() > 0) {

				if (courseMeetingTab.isSelected()) {

					MeetingSet.addCourseMeetingSet(
							Main.active.getSelectedTerm(),
							chooseCourse.getValue(),
							chooseCourseMeetingType.getValue(),
							DateTimeUtil
									.parseLocalTime(chooseStartTime.getValue()),
							DateTimeUtil
									.parseLocalTime(chooseEndTime.getValue()),
							enterLocation.getText(), meetingDates);

				} else if (nonCourseMeetingTab.isSelected()) {

					MeetingSet.addNonCourseMeetingSet(
							Main.active.getSelectedTerm(),
							enterMeetingName.getText(),
							chooseNonCourseMeetingType.getValue(),
							DateTimeUtil
									.parseLocalTime(chooseStartTime.getValue()),
							DateTimeUtil
									.parseLocalTime(chooseEndTime.getValue()),
							enterLocation.getText(), meetingDates,
							chooseColor.getValue());
				}

				window.close();
			}
		} else {

			ArrayList<String> errorMessages = new ArrayList<>();

			if (!DateTimeUtil.isValidLocalTime(chooseStartTime.getValue())) {
				errorMessages.add(
						"Start time is invalid. It must be in the format hh:mm");
			}

			if (!DateTimeUtil.isValidLocalTime(chooseEndTime.getValue())) {
				errorMessages.add(
						"End time is invalid. It must be in the format hh:mm");
			}

			String errorText = "";

			for (int i = 0; i < errorMessages.size(); i++) {

				errorText += (i + 1) + ". " + errorMessages.get(i);

				if (i < errorMessages.size() - 1) {

					errorText += "\n";
				}
			}

			Main.showAlert(AlertType.ERROR, "Cannot add meeting", errorText);
		}
	}

	@FXML
	private void cancel() {
		window.close();
	}
}
