package core;

public class CourseDescription {

	private String dept;
	private int code;
	private TermDescription startTerm;
	private TermDescription endTerm;

	public CourseDescription(String dept, int code, TermDescription startTerm,
			TermDescription endTerm) {

		this.dept = dept;
		this.code = code;
		this.startTerm = startTerm;
		this.endTerm = endTerm;
	}
}
