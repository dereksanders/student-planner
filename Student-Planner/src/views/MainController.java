package views;

import core.Main;
import javafx.fxml.FXML;

public class MainController {

	@FXML
	public void initialize() {

		// Whenever we get to this view, we want to open the current profile
		// when we next start the planner as getting here indicates that we have
		// either loaded a profile or successfully created a new one.
		Main.writeCurrentConfig();
	}
}
