package views;

import core.MeetingSetDescription;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EditMeetingOptionsController {

	private MeetingSetDescription set;

	public EditMeetingOptionsController(MeetingSetDescription set) {

		this.set = set;

		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Edit Meeting");

		window.showAndWait();
	}
}
