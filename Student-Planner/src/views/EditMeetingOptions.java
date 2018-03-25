package views;

import core.MeetingSetDescription;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EditMeetingOptions {

	private MeetingSetDescription set;

	public EditMeetingOptions(MeetingSetDescription set) {

		this.set = set;

		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Edit Meeting");

		window.showAndWait();
	}
}
