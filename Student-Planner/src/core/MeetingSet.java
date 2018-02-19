package core;

public class MeetingSet {

	public enum Lookup {

		ID(1), TERM_START_DATE(2), START_TIME(3), END_TIME(
				4), COURSE_START_TERM_START_DATE(5), COURSE_END_TERM_START_DATE(
						6), COURSE_DEPT(7), COURSE_CODE(
								8), NAME(9), TYPE(10), LOCATION(11);

		public int index;

		private Lookup(int index) {
			this.index = index;
		}
	};
}
