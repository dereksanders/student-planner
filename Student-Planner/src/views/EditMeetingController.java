package views;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import core.Course;
import core.CourseDescription;
import core.Main;
import core.Meeting;
import core.MeetingDescription;
import core.MeetingSet;
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
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sqlite.SqliteWrapperException;
import utility.DateTimeUtil;

public class EditMeetingController {

	public enum Option {

		EDIT_THIS_INSTANCE, EDIT_ALL_INSTANCES, EDIT_THIS_AND_FUTURE_INSTANCES;
	};

	private Stage window;

	private MeetingDescription selected;
	private Option option;

	@FXML
	private TabPane meetingTabs;

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
	private Text chooseEndDateTitle;
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
	private Text chooseRepeatTitle;
	@FXML
	private ChoiceBox<String> chooseRepeat;

	public EditMeetingController(MeetingDescription selected, Option option)
			throws IOException {

		this.selected = selected;
		this.option = option;

		this.window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Edit Meeting");

		// Because this controller is instantiated, it must be added to the
		// .FXML afterwards, i.e. it cannot be defined as the controller a
		// priori in the .FXML.
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(ViewController.editMeetingPath));
		loader.setController(this);

		Scene scene = new Scene(loader.load());

		window.setScene(scene);
		window.getIcons()
				.add(new Image(Main.class.getResourceAsStream("/icon.png")));

		window.showAndWait();
	}

	@FXML
	public void initialize() throws SqliteWrapperException, SQLException {

		ArrayList<CourseDescription> courses = Course
				.populateCourses(Main.active.getSelectedTerm());

		chooseCourse.setItems(FXCollections.observableArrayList(courses));

		chooseCourseMeetingType.setItems(
				FXCollections.observableArrayList(MeetingSet.COURSE_TYPES));
		chooseNonCourseMeetingType.setItems(
				FXCollections.observableArrayList(MeetingSet.NON_COURSE_TYPES));

		chooseRepeat.setItems(
				FXCollections.observableArrayList(MeetingSet.REPEAT_OPTIONS));
		chooseRepeat.setValue(selected.set.repeat);

		if (selected.set.isCourseMeeting) {

			meetingTabs.getSelectionModel().select(courseMeetingTab);
			nonCourseMeetingTab.setDisable(true);

			chooseCourse.setValue(selected.set.course);

			if (chooseCourseMeetingType.getItems()
					.contains(selected.set.type)) {

				chooseCourseMeetingType.setValue(selected.set.type);

			} else {

				chooseCourseMeetingType.setValue("Other");
				enterOtherCourseMeetingType.setText(selected.set.type);
			}

		} else {

			meetingTabs.getSelectionModel().select(nonCourseMeetingTab);
			courseMeetingTab.setDisable(true);

			if (chooseNonCourseMeetingType.getItems()
					.contains(selected.set.type)) {

				chooseNonCourseMeetingType.setValue(selected.set.type);

			} else {

				chooseNonCourseMeetingType.setValue("Other");
				enterOtherNonCourseMeetingType.setText(selected.set.type);
			}
		}

		if (this.option.equals(Option.EDIT_THIS_INSTANCE)) {

			chooseStartDate.setValue(selected.date);

			chooseEndDateTitle.setVisible(false);
			chooseEndDate.setVisible(false);
			toEndOfTerm.setVisible(false);
			chooseRepeatTitle.setVisible(false);
			chooseRepeat.setVisible(false);

			chooseRepeat.setValue("Never");

		} else if (this.option.equals(Option.EDIT_THIS_AND_FUTURE_INSTANCES)) {

			chooseStartDate.setValue(selected.date);
			chooseEndDate
					.setValue(MeetingSet.getLastMeeting(selected.setID).date);

		} else if (this.option.equals(Option.EDIT_ALL_INSTANCES)) {

			chooseStartDate
					.setValue(MeetingSet.getFirstMeeting(selected.setID).date);
			chooseEndDate
					.setValue(MeetingSet.getLastMeeting(selected.setID).date);
		}

		chooseStartTime
				.setValue(DateTimeUtil.localTimeAsString(selected.set.start));
		chooseEndTime
				.setValue(DateTimeUtil.localTimeAsString(selected.set.end));

		enterLocation.setText(selected.set.location);
	}

	@FXML
	private void confirm()
			throws SQLException, SqliteWrapperException, IOException {

		// delete all edited meetings from existing set
		if (this.option.equals(Option.EDIT_THIS_INSTANCE)) {

			Meeting.deleteMeeting(selected);

		} else if (this.option.equals(Option.EDIT_THIS_AND_FUTURE_INSTANCES)) {

			Meeting.deleteMeetingsFromSet(selected.setID, selected.date);

		} else if (this.option.equals(Option.EDIT_ALL_INSTANCES)) {

			MeetingSet.deleteMeetingSet(selected.setID);
		}

		// create a new set containing these edited meetings
		String description = "";
		if (courseMeetingTab.isSelected()) {

			description = chooseCourse.getValue() + " "
					+ chooseCourseMeetingType.getValue();
		} else {

			description = enterMeetingName.getText();
		}

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
						meetingDates, description);

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
							enterLocation.getText(), chooseRepeat.getValue(),
							meetingDates);

				} else if (nonCourseMeetingTab.isSelected()) {

					MeetingSet.addNonCourseMeetingSet(
							Main.active.getSelectedTerm(),
							enterMeetingName.getText(),
							chooseNonCourseMeetingType.getValue(),
							DateTimeUtil
									.parseLocalTime(chooseStartTime.getValue()),
							DateTimeUtil
									.parseLocalTime(chooseEndTime.getValue()),
							enterLocation.getText(), chooseRepeat.getValue(),
							meetingDates, chooseColor.getValue());
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

		this.window.close();
	}

	@FXML
	private void delete() throws SQLException, SqliteWrapperException {

		if (this.option.equals(Option.EDIT_THIS_INSTANCE)) {

			Meeting.deleteMeeting(selected);

		} else if (this.option.equals(Option.EDIT_THIS_AND_FUTURE_INSTANCES)) {

			Meeting.deleteMeetingsFromSet(selected.setID, selected.date);

		} else if (this.option.equals(Option.EDIT_ALL_INSTANCES)) {

			MeetingSet.deleteMeetingSet(selected.setID);
		}

		this.window.close();
	}
}
