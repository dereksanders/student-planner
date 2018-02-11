package views;

import java.io.IOException;
import java.sql.SQLException;

import core.Main;
import core.Term;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import sqlite.SqliteWrapperException;
import utility.ColorUtil;

public class TermsController {

	@FXML
	private TextField nameField;
	@FXML
	private DatePicker startDate;
	@FXML
	private DatePicker endDate;
	@FXML
	private ColorPicker color;

	@FXML
	public void initialize() {

	}

	@FXML
	public void add() throws SqliteWrapperException, SQLException {

		long startDay = startDate.getValue().toEpochDay();
		long endDay = endDate.getValue().toEpochDay();

		Term.addTerm(startDay, endDay, nameField.getText(),
				ColorUtil.colorToHex(color.getValue()));
	}

	@FXML
	public void next()
			throws SqliteWrapperException, SQLException, IOException {

		if (Term.getNumTerms() > 0) {

			Parent courses = FXMLLoader
					.load(this.getClass().getResource("/views/Courses.fxml"));
			Scene addCourses = new Scene(courses, 1280, 960);
			Main.window.setScene(addCourses);

		} else {

			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Cannot proceed");
			alert.setContentText("You must add at least one term to continue");
			alert.showAndWait();
		}
	}
}
