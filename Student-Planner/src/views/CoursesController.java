package views;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import core.Course;
import core.Main;
import core.Term;
import core.TermDescription;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import sqlite.SqliteWrapperException;
import utility.ColorUtil;

public class CoursesController {

	@FXML
	private ChoiceBox<TermDescription> startTerm;
	@FXML
	private ChoiceBox<TermDescription> endTerm;
	@FXML
	private TextField deptField;
	@FXML
	private TextField codeField;
	@FXML
	private TextField titleField;
	@FXML
	private ColorPicker color;

	@FXML
	public void initialize() throws SqliteWrapperException, SQLException {

		// Populate choices for startTerm & endTerm
		ResultSet terms = Main.sqlite.query("select * from term");

		ArrayList<TermDescription> termDescriptions = new ArrayList<>();

		while (terms.next()) {

			long startDay = terms.getLong(Term.Lookup.START_DATE.index);

			LocalDate start = LocalDate.ofEpochDay(startDay);

			TermDescription currentTerm = new TermDescription(
					terms.getString(Term.Lookup.NAME.index), start);

			termDescriptions.add(currentTerm);
		}

		startTerm.setItems(FXCollections.observableArrayList(termDescriptions));
		endTerm.setItems(FXCollections.observableArrayList(termDescriptions));

		startTerm.setValue(termDescriptions.get(0));
		endTerm.setValue(termDescriptions.get(0));
	}

	@FXML
	public void add() throws SqliteWrapperException, SQLException {

		long startTermStartDay = startTerm.getValue().getStartDay();
		long endTermStartDay = endTerm.getValue().getStartDay();

		int courseCode = 0;
		boolean courseCodeIsValid = true;

		try {

			courseCode = Integer.parseInt(codeField.getText());

		} catch (NumberFormatException e) {

			courseCodeIsValid = false;

			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Cannot add course");
			alert.setContentText("Course code must be an integer");
			alert.showAndWait();
		}

		if (courseCodeIsValid) {

			Course.addCourse(startTermStartDay, endTermStartDay,
					deptField.getText(), courseCode, titleField.getText(),
					ColorUtil.colorToHex(color.getValue()));
		}
	}

	@FXML
	public void next()
			throws SqliteWrapperException, SQLException, IOException {

		Parent main = FXMLLoader
				.load(this.getClass().getResource("/views/Main.fxml"));
		Scene mainScene = new Scene(main, 1280, 960);
		Main.window.setScene(mainScene);
	}
}
