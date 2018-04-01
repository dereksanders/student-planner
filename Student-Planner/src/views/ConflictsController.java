package views;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import core.Main;
import core.MeetingDescription;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConflictsController {

	private Stage window;
	ArrayList<MeetingDescription> conflicts;
	ArrayList<LocalDate> dates;

	public ConflictsController(ArrayList<MeetingDescription> conflicts,
			ArrayList<LocalDate> dates) throws IOException {

		this.conflicts = conflicts;
		this.dates = dates;

		window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Add Meeting");

		// Because this controller is instantiated, it must be added to the
		// .FXML afterwards, i.e. it cannot be defined as the controller a
		// priori in the .FXML.
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(ViewController.conflictsPath));
		loader.setController(this);

		Scene scene = new Scene(loader.load());

		window.setScene(scene);
		window.getIcons()
				.add(new Image(Main.class.getResourceAsStream("/icon.png")));
		window.showAndWait();
	}

	public ArrayList<LocalDate> getRemainingDates() {

		return this.dates;
	}

}
