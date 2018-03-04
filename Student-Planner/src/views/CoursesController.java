package views;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import core.Course;
import core.Main;
import core.Term;
import core.TermDescription;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
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

		ArrayList<TermDescription> termDescriptions = Term.populateTerms();

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

			Main.showAlert(AlertType.ERROR, "Cannot add course",
					"Course code must be an integer");
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

		Main.viewController.showMainView();
	}
}
