package views;

import java.io.IOException;
import java.time.LocalDateTime;

import core.CourseDescription;
import core.Main;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The Class AddMeetingController.
 */
public class AddMeetingController {

	private Stage window;
	private LocalDateTime selected;

	@FXML
	private CheckBox isCourseMeeting;
	@FXML
	private TextField enterMeetingType;

	@FXML
	private ChoiceBox<CourseDescription> chooseCourse;
	@FXML
	private ChoiceBox<String> chooseTypeOfCourseMeeting;
	@FXML
	private ChoiceBox<String> chooseTypeOfNonCourseMeeting;

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

		this.window = new Stage();
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

		chooseTypeOfNonCourseMeeting.valueProperty()
				.addListener(new ChangeListener<String>() {
					@Override
					public void changed(
							ObservableValue<? extends String> observable,
							String oldType, String newType) {

						if (newType != null) {

							if (newType.equals("Other")) {

							} else {

							}
						}
					}
				});
	}
}
