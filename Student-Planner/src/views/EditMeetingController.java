package views;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import core.Course;
import core.CourseDescription;
import core.Main;
import core.MeetingDescription;
import core.MeetingSet;
import core.MeetingSet.EditOption;
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
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sqlite.SqliteWrapperException;
import utility.DateTimeUtil;

public class EditMeetingController {

	private Stage window;

	private MeetingDescription selected;
	private EditOption option;

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

	public EditMeetingController(MeetingDescription selected, EditOption option)
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

							chooseEndTime.setValue(chooseEndTime.getItems()
									.get(chooseEndTime.getItems().indexOf(
											chooseStartTime.getValue()) + 1));
						}
					}
				});

		toEndOfTerm.selectedProperty()
				.addListener(new ChangeListener<Boolean>() {
					@Override
					public void changed(
							ObservableValue<? extends Boolean> observable,
							Boolean oldVal, Boolean newVal) {

						if (newVal) {
							chooseEndDate.setValue(
									Main.active.getSelectedTerm().getEnd());
							chooseEndDate.setDisable(true);
						} else {
							chooseEndDate.setDisable(false);
						}
					}
				});

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

						} else if (newEndDate.isAfter(
								Main.active.getSelectedTerm().getEnd())) {

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

						} else if (newStartDate.isBefore(
								Main.active.getSelectedTerm().getStart())) {

							Main.showAlert(AlertType.ERROR,
									"Cannot select date",
									"Start date cannot be before the start of the term.");
						}
					}
				});

		chooseStartTime.setValue(
				DateTimeUtil.localTimeAsString(selected.set.getStart()));

		chooseEndTime.setValue(
				DateTimeUtil.localTimeAsString(selected.set.getEnd()));

		chooseRepeat.setItems(
				FXCollections.observableArrayList(MeetingSet.REPEAT_OPTIONS));
		chooseRepeat.setValue(selected.set.getRepeat());

		if (selected.set.isCourseMeeting()) {

			meetingTabs.getSelectionModel().select(courseMeetingTab);
			nonCourseMeetingTab.setDisable(true);

			chooseCourse.setValue(selected.set.getCourse());

			if (chooseCourseMeetingType.getItems()
					.contains(selected.set.getType())) {

				chooseCourseMeetingType.setValue(selected.set.getType());

			} else {

				chooseCourseMeetingType.setValue("Other");
				enterOtherCourseMeetingType.setText(selected.set.getType());
			}

		} else {

			meetingTabs.getSelectionModel().select(nonCourseMeetingTab);
			courseMeetingTab.setDisable(true);

			if (chooseNonCourseMeetingType.getItems()
					.contains(selected.set.getType())) {

				chooseNonCourseMeetingType.setValue(selected.set.getType());

			} else {

				chooseNonCourseMeetingType.setValue("Other");
				enterOtherNonCourseMeetingType.setText(selected.set.getType());
			}

			enterMeetingName.setText(selected.set.getName());

			chooseColor
					.setValue(Color.web(MeetingSet.getColor(selected.setID)));

			for (Color c : CourseScheduleController.getRecentColors()) {

				Rectangle r = new Rectangle(30, 30);
				r.setFill(c);

				r.setOnMouseClicked(e -> {
					chooseColor.setValue(c);
				});

				recentColors.getChildren().add(r);
			}
		}

		if (this.option.equals(EditOption.EDIT_THIS_INSTANCE)) {

			chooseStartDate.setValue(selected.date);

			chooseEndDateTitle.setVisible(false);
			chooseEndDate.setVisible(false);
			toEndOfTerm.setVisible(false);
			chooseRepeatTitle.setVisible(false);
			chooseRepeat.setVisible(false);

			chooseRepeat.setValue("Never");

		} else if (this.option
				.equals(EditOption.EDIT_THIS_AND_FUTURE_INSTANCES)) {

			chooseStartDate.setValue(selected.date);
			chooseEndDate
					.setValue(MeetingSet.getLastMeeting(selected.setID).date);

		} else if (this.option.equals(EditOption.EDIT_ALL_INSTANCES)) {

			chooseStartDate
					.setValue(MeetingSet.getFirstMeeting(selected.setID).date);
			chooseEndDate
					.setValue(MeetingSet.getLastMeeting(selected.setID).date);
		}

		chooseStartTime.setValue(
				DateTimeUtil.localTimeAsString(selected.set.getStart()));
		chooseEndTime.setValue(
				DateTimeUtil.localTimeAsString(selected.set.getEnd()));

		enterLocation.setText(selected.set.getLocation());
	}

	@FXML
	private void confirm()
			throws SQLException, SqliteWrapperException, IOException {

		if (timesAreValid(chooseStartTime.getValue(),
				chooseEndTime.getValue())) {

			if (courseMeetingTab.isSelected()) {

				MeetingSet.editCourseMeetingSet(this.selected, this.option,
						chooseCourse.getValue(),
						chooseCourseMeetingType.getValue(),
						chooseStartDate.getValue(), chooseEndDate.getValue(),
						DateTimeUtil.parseLocalTime(chooseStartTime.getValue()),
						DateTimeUtil.parseLocalTime(chooseEndTime.getValue()),
						enterLocation.getText(), chooseRepeat.getValue());

				window.close();

			} else {

				MeetingSet.editNonCourseMeetingSet(this.selected, this.option,
						enterMeetingName.getText(),
						chooseNonCourseMeetingType.getValue(),
						chooseStartDate.getValue(), chooseEndDate.getValue(),
						DateTimeUtil.parseLocalTime(chooseStartTime.getValue()),
						DateTimeUtil.parseLocalTime(chooseEndTime.getValue()),
						enterLocation.getText(), chooseRepeat.getValue(),
						chooseColor.getValue());

				window.close();
			}
		}
	}

	private boolean timesAreValid(String startTime, String endTime) {

		boolean timesAreValid = true;

		LocalTime start = DateTimeUtil
				.parseLocalTime(chooseStartTime.getValue());

		LocalTime end = DateTimeUtil.parseLocalTime(chooseEndTime.getValue());

		ArrayList<String> errorMessages = new ArrayList<>();

		if (start == null) {
			errorMessages.add(
					"Start time is invalid. It must be in the format hh:mm");

			timesAreValid = false;
		}

		if (end == null) {
			errorMessages
					.add("End time is invalid. It must be in the format hh:mm");

			timesAreValid = false;
		}

		if (timesAreValid) {

			if (!start.isBefore(end)) {

				errorMessages.add("Start time must be before end time.");
			}
		}

		if (!timesAreValid) {

			String errorText = "";

			for (int i = 0; i < errorMessages.size(); i++) {

				errorText += (i + 1) + ". " + errorMessages.get(i);

				if (i < errorMessages.size() - 1) {

					errorText += "\n";
				}
			}

			Main.showAlert(AlertType.ERROR, "Cannot edit meeting", errorText);
		}

		return timesAreValid;
	}

	@FXML
	private void cancel() {

		this.window.close();
	}

	@FXML
	private void delete() throws SQLException, SqliteWrapperException {

		MeetingSet.deleteMeetings(this.selected, this.option);

		this.window.close();
	}
}
