package Models;

public class Course {

	private String cName;
	private String cDesc;
	private String cTeacher;
	private String cTopics[] = new String [20];

	public String getcName() {
		return cName;
	}

	public void setcName(String cName) {
		this.cName = cName;
	}

	public String getcDesc() {
		return cDesc;
	}

	public void setcDesc(String cDesc) {
		this.cDesc = cDesc;
	}

	public String getcTeacher() {
		return cTeacher;
	}

	public void setcTeacher(String cTeacher) {
		this.cTeacher = cTeacher;
	}

	public String[] getcTopics() {
		return cTopics;
	}

	public void setcTopics(String cTopics[]) {
		this.cTopics = cTopics;
	}
	
}
