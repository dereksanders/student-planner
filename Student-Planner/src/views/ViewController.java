package views;

import java.io.IOException;

import core.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class ViewController {

	public static String welcomeViewPath = "/views/Welcome.fxml";
	public static String termsViewPath = "/views/Terms.fxml";
	public static String coursesViewPath = "/views/Courses.fxml";
	public static String mainViewPath = "/views/Main.fxml";

	// Views used for other windows
	public static String addMeetingPath = "/views/AddMeeting.fxml";

	public ViewController() {
	}

	public void showTermsView() throws IOException {

		loadView(termsViewPath);
	}

	public void showCoursesView() throws IOException {

		loadView(coursesViewPath);
	}

	public void showMainView() throws IOException {

		loadView(mainViewPath);
	}

	private void loadView(String path) throws IOException {

		Parent parent = FXMLLoader
				.load(Main.class.getClass().getResource(path));

		Scene view = new Scene(parent, Main.window.getScene().getWidth(),
				Main.window.getScene().getHeight());

		view.getStylesheets().add(Main.class.getClass()
				.getResource("/views/style.css").toExternalForm());

		Main.window.setScene(view);
	}
}
