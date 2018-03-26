package core;

import javafx.scene.shape.Rectangle;

/**
 * The Class MeetingBlock.
 * 
 * Represents a block on the CourseSchedule.
 */
public class MeetingBlock {

	public MeetingDescription meeting;
	public Rectangle rect;

	/**
	 * Instantiates a new meeting block.
	 *
	 * @param meeting
	 *            the meeting
	 * @param rect
	 *            the rect
	 */
	public MeetingBlock(MeetingDescription meeting, Rectangle rect) {

		this.meeting = meeting;
		this.rect = rect;
	}
}
