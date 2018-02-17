package views;

import java.util.Observable;
import java.util.Observer;

import core.Main;
import core.Profile;
import core.TermDescription;
import javafx.scene.canvas.Canvas;

public class CourseSchedule implements Observer {

	private Observable profile;
	private TermDescription term;
	private Canvas canvas;

	public CourseSchedule(TermDescription term) {

		this.profile = Main.active;
		this.profile.addObserver(this);

		this.term = term;
		this.canvas = new Canvas();
		drawSchedule();
	}

	private void drawSchedule() {

	}

	public Canvas getCanvas() {

		return this.canvas;
	}

	public void setTerm(TermDescription term) {

		this.term = term;
		drawSchedule();
	}

	@Override
	public void update(Observable arg0, Object arg1) {

		if (arg0 instanceof Profile) {
			drawSchedule();
		}
	}
}
