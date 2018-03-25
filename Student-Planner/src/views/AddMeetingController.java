package views;

import java.io.IOException;
import java.time.LocalDateTime;

import core.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The Class AddMeetingController.
 */
public class AddMeetingController {

	private LocalDateTime selected;

	@FXML
	private Text selectedText;

	/**
	 * Instantiates a controller for an AddMeeting window and launches the
	 * window.
	 *
	 * @param selected
	 *            the selected date time
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public AddMeetingController(LocalDateTime selected) throws IOException {

		this.selected = selected;

		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Add Meeting");

		// Because this controller is instantiated, it must be added to the
		// .FXML afterwards, i.e. it cannot be defined as the controller a
		// priori in the .FXML.
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(ViewController.addMeetingPath));
		loader.setController(this);

		Scene scene = new Scene(loader.load());

		window.setScene(scene);
		window.getIcons()
				.add(new Image(Main.class.getResourceAsStream("/icon.png")));
		window.showAndWait();
	}

	/**
	 * Initialize.
	 */
	@FXML
	public void initialize() {

		this.selectedText.setText(selected.toString());
	}
}
