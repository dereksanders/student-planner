package views;

import java.io.IOException;
import java.sql.SQLException;

import core.Main;
import core.Term;
import javafx.fxml.FXML;
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

			Main.showCoursesView();

		} else {

			Main.showAlert(AlertType.ERROR, "Cannot proceed",
					"You must add at least one term to continue");
		}
	}
}
