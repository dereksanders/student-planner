package views;

import java.time.LocalDateTime;

import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddMeetingOnSchedule {

	private LocalDateTime selected;

	public AddMeetingOnSchedule(LocalDateTime selected) {

		this.selected = selected;

		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Add Meeting");

		window.showAndWait();
	}

}
