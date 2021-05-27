package ir.sample.app.BeheshtRay.models;

import ir.sample.app.BeheshtRay.models.Comment;

import java.util.ArrayList;

public class Student {
    public String studentName;
    public String studentId;
    public String studentFaculty;
    public String studentGender;
	public String image_url;



	public ArrayList <Feedback> feedbacks;


	public ArrayList<Comment> studentComments = new ArrayList<>();

	public Student() {
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public String getStudentFaculty() {
		return studentFaculty;
	}

	public void setStudentFaculty(String studentFaculty) {
		this.studentFaculty = studentFaculty;
	}

	public String getStudentGender() {
		return studentGender;
	}

	public void setStudentGender(String studentGender) {
		this.studentGender = studentGender;
	}

	public ArrayList<Comment> getStudentComments() {
		return studentComments;
	}

	public void setStudentComments(ArrayList<Comment> studentComments) {
		this.studentComments = studentComments;
	}
}
