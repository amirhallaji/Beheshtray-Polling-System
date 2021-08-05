package ir.sample.app.BeheshtRay.models;

import java.util.ArrayList;

public class Student {
    public String student_name;
    public String student_id;
    public String student_faculty;
    public String student_gender;
	public String student_photo;
	public String student_upvotes;
	public String student_downvotes;
	public String user_id;
	public String user_karma;



	public ArrayList <Feedback> feedbacks;


	public ArrayList<Comment> studentComments = new ArrayList<>();

	public Student() {
	}

	public String getStudentName() {
		return student_name;
	}

	public void setStudentName(String studentName) {
		this.student_name = studentName;
	}

	public String getStudent_id() {
		return student_id;
	}

	public void setStudent_id(String student_id) {
		this.student_id = student_id;
	}

	public String getStudent_faculty() {
		return student_faculty;
	}

	public void setStudent_faculty(String student_faculty) {
		this.student_faculty = student_faculty;
	}

	public String getStudent_gender() {
		return student_gender;
	}

	public void setStudent_gender(String student_gender) {
		this.student_gender = student_gender;
	}

	public ArrayList<Comment> getStudentComments() {
		return studentComments;
	}

	public void setStudentComments(ArrayList<Comment> studentComments) {
		this.studentComments = studentComments;
	}

	@Override
	public String toString() {
		return "Student{" +
				"student_name='" + student_name + '\'' +
				", student_id='" + student_id + '\'' +
				", student_faculty='" + student_faculty + '\'' +
				'}';
	}
}
