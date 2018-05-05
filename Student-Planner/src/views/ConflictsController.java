package views;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import core.Main;
import core.Meeting;
import core.MeetingDescription;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sqlite.SqliteWrapperException;

public class ConflictsController {

	private Stage window;
	private ArrayList<MeetingDescription> conflicts;
	private ArrayList<LocalDate> dates;
	private String beingAdded;

	@FXML
	private Text currentConflict;
	@FXML
	private Text meetingBeingAdded;
	@FXML
	private CheckBox repeat;

	public ConflictsController(ArrayList<MeetingDescription> conflicts,
			ArrayList<LocalDate> dates, String beingAdded) throws IOException {

		this.conflicts = conflicts;
		this.dates = dates;
		this.beingAdded = beingAdded;

		window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Handle Conflicts");

		// Because this controller is instantiated, it must be added to the
		// .FXML afterwards, i.e. it cannot be defined as the controller a
		// priori in the .FXML.
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(ViewController.conflictsPath));
		loader.setController(this);

		Scene scene = new Scene(loader.load());

		// If the window is closed before all conflicts are handled, then delete
		// all of the new meetings being added and preserve the conflicts.
		window.setOnCloseRequest(e -> {

			repeat.setSelected(true);
			deleteNew();
		});

		window.setScene(scene);
		window.getIcons()
				.add(new Image(Main.class.getResourceAsStream("/icon.png")));
		window.showAndWait();
	}

	@FXML
	public void initialize() {

		currentConflict.setText(conflicts.get(0).toString());
		meetingBeingAdded.setText(beingAdded);

		if (conflicts.size() > 1) {
			repeat.setText("Repeat for next " + (conflicts.size() - 1)
					+ " conflicts.");
		} else {
			repeat.setVisible(false);
		}
	}

	@FXML
	public void deleteOld() throws SQLException, SqliteWrapperException {

		Meeting.deleteMeeting(conflicts.get(0));
		conflicts.remove(0);

		System.out
				.println("Number of conflicts remaining: " + conflicts.size());

		if (conflicts.size() == 0) {
			window.close();
		} else {
			currentConflict.setText(conflicts.get(0).toString());
		}

		if (repeat.isSelected() && conflicts.size() > 0) {

			deleteOld();

		} else {

			if (conflicts.size() > 1) {
				repeat.setText("Repeat for next " + (conflicts.size() - 1)
						+ " conflicts.");
			} else {
				repeat.setVisible(false);
			}
		}
	}

	@FXML
	public void deleteNew() {

		dates.remove(conflicts.get(0).date);
		conflicts.remove(0);

		System.out
				.println("Number of conflicts remaining: " + conflicts.size());

		if (conflicts.size() == 0) {
			window.close();
		} else {
			currentConflict.setText(conflicts.get(0).toString());
		}

		if (repeat.isSelected() && conflicts.size() > 0) {

			deleteNew();

		} else {

			if (conflicts.size() > 1) {
				repeat.setText("Repeat for next " + (conflicts.size() - 1)
						+ " conflicts.");
			} else {
				repeat.setVisible(false);
			}
		}
	}

	public ArrayList<LocalDate> getRemainingDates() {

		return this.dates;
	}

}
