package core;

public class CourseDescription {

	public String dept;
	public int code;
	public TermDescription startTerm;
	public TermDescription endTerm;

	public CourseDescription(String dept, int code, TermDescription startTerm,
			TermDescription endTerm) {

		this.dept = dept;
		this.code = code;
		this.startTerm = startTerm;
		this.endTerm = endTerm;
	}

	@Override
	public String toString() {

		String desc = "";

		desc += dept + " " + code;

		return desc;
	}
}
