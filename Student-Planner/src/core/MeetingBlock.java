package core;

import javafx.scene.shape.Rectangle;

public class MeetingBlock {

	public MeetingDescription meeting;
	public Rectangle rect;

	public MeetingBlock(MeetingDescription meeting, Rectangle rect) {

		this.meeting = meeting;
		this.rect = rect;
	}

	public boolean contains(int pos) {
		return false;
	}
}
