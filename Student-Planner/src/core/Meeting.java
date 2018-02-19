package core;

public class Meeting {

	public enum Lookup {

		SET_ID(1), DATE(2);

		public int index;

		private Lookup(int index) {
			this.index = index;
		}
	};
}
