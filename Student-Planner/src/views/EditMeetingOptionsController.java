package views;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import core.Main;
import core.MeetingDescription;
import core.MeetingSet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sqlite.SqliteWrapperException;
import views.EditMeetingController.Option;

public class EditMeetingOptionsController {

	private MeetingDescription selected;
	@FXML
	private Text meetings;
	@FXML
	private Text selectedDescription;

	public EditMeetingOptionsController(MeetingDescription selected)
			throws IOException {

		this.selected = selected;

		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Edit Meeting");

		// Because this controller is instantiated, it must be added to the
		// .FXML afterwards, i.e. it cannot be defined as the controller a
		// priori in the .FXML.
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(ViewController.editMeetingOptionsPath));
		loader.setController(this);

		Scene scene = new Scene(loader.load());

		window.setScene(scene);
		window.getIcons()
				.add(new Image(Main.class.getResourceAsStream("/icon.png")));

		window.showAndWait();
	}

	@FXML
	public void initialize() throws SQLException, SqliteWrapperException {

		selectedDescription.setText(
				selectedDescription.getText() + " " + this.selected.toString());

		meetings.setText("");

		ArrayList<MeetingDescription> meetingsInSet = MeetingSet
				.getMeetingsInSet(selected.setID);

		for (MeetingDescription m : meetingsInSet) {

			if (m.equals(this.selected)) {

				// meetings.setFont(Font.ITALIC);
			}
			meetings.setText(meetings.getText() + m + "\n\n");
		}
	}

	@FXML
	public void editThisInstance() throws IOException {

		new EditMeetingController(this.selected, Option.EDIT_THIS_INSTANCE);
	}

	@FXML
	public void editAllInstances() throws IOException {

		new EditMeetingController(this.selected, Option.EDIT_ALL_INSTANCES);
	}

	@FXML
	public void editThisAndFutureInstances() throws IOException {

		new EditMeetingController(this.selected,
				Option.EDIT_THIS_AND_FUTURE_INSTANCES);
	}
}
