package views;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import core.Main;
import core.MeetingDescription;
import core.MeetingSet;
import core.MeetingSet.EditOption;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sqlite.SqliteWrapperException;

public class EditMeetingOptionsController {

	private Stage window;

	private MeetingDescription selected;
	@FXML
	private VBox meetings;

	public EditMeetingOptionsController(MeetingDescription selected)
			throws IOException {

		this.selected = selected;

		window = new Stage();
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

		ArrayList<MeetingDescription> meetingsInSet = MeetingSet
				.getMeetingsInSet(selected.setID);

		for (MeetingDescription m : meetingsInSet) {

			Label description = new Label();
			description.setText(m.toString());

			if (m.equals(this.selected)) {

				description.setStyle(
						"-fx-font-style: italic;" + "-fx-text-fill: #1636a0;");
			}

			meetings.getChildren().add(description);
		}
	}

	@FXML
	public void editThisInstance() throws IOException {

		new EditMeetingController(this.selected, EditOption.EDIT_THIS_INSTANCE);
		this.window.close();
	}

	@FXML
	public void editAllInstances() throws IOException {

		new EditMeetingController(this.selected, EditOption.EDIT_ALL_INSTANCES);
		this.window.close();
	}

	@FXML
	public void editThisAndFutureInstances() throws IOException {

		new EditMeetingController(this.selected,
				EditOption.EDIT_THIS_AND_FUTURE_INSTANCES);
		this.window.close();
	}
}
